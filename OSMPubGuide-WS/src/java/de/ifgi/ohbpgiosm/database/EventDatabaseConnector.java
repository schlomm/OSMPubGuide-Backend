/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm.database;

import de.ifgi.ohbpgiosm.Connector;
import de.ifgi.ohbpgiosm.Parameter;
import de.ifgi.ohbpgiosm.Query;
import de.ifgi.ohbpgiosm.logging.MyLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import noNamespace.EventType;
import noNamespace.MemberType;
import noNamespace.NodeType;
import noNamespace.OsmDocument;
import noNamespace.OsmType;
import noNamespace.RelationReferType;
import noNamespace.RelationType;
import noNamespace.TagType;
import org.apache.log4j.*;

/**
 *
 * @author christopher
 */
public class EventDatabaseConnector extends Connector{
    
    private static final Logger logger = MyLogger.getInstance().getLogger();
    private static final Properties dbCredentials = PropertyReader.getInstance().getProperty();
    // reading database credentials from properties file
    static final String JDBC_DRIVER = dbCredentials.getProperty("db_driver_name"); 
    static final String DB_URL = dbCredentials.getProperty("db_url");
    static final String PASSWORD = dbCredentials.getProperty("db_password");
    static final String USER = dbCredentials.getProperty("db_user");
    
    private OsmDocument response;
    
    @Override
    public OsmDocument getResponse() {
        return response;
    }

    @Override
    public void run() {
        
        try {
            if (this.queries.isEmpty()) {
                throw new RuntimeException("Queries are empty.");
            }
            Date start = null;
            Date end = null;
            List<String> pubFilterList = null;
            List<String> eventFilterList = null;                       
            
            for(Query query : this.queries){
                switch (query.getQueryType()) {
                    case TEMPORAL:
                        start = ((List<Date>)query.get(Parameter.START)).get(0);
                        end = ((List<Date>)query.get(Parameter.END)).get(0);
                        break;
                    case ATTRIBUTAL:
                        pubFilterList = (List<String>) query.get(Parameter.FILTER);
                        break;
                    case EVENT:
                        eventFilterList = (List <String>) query.get(Parameter.EVENT_FILTER);
                        break;
                }
                
                // TODO case differentiation based on existing start, end .. parameters
                // which SQL statement is created and send to the DB
                
            }
            
            
            String sqlQuery = this.createSQLQuery(start, end, pubFilterList, eventFilterList);
            executeQuery(sqlQuery);
            
            this.finish();
            this.notifyObservers();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(EventDatabaseConnector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        
    }
   
    protected String createSQLQuery(Date start, Date end, List<String> attr, List<String> e_attr) {
        String sql = "";
        boolean entryFee = false;
        String filterPubSQL = "pub";
        String filterEventSQL = "temporal_event";
        String generalFilterSQL = "";
        
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        
        if (start != null) {
            generalFilterSQL = " AND is_open(pub_select.pub_ref, '"+formatter.format(start)+"+02'"; //makes it easier to handle the timezone
            if (end != null) {
                generalFilterSQL += ", '"+formatter.format(end)+"+02'";
            }
            generalFilterSQL += ")";
        }
        
        // create pub filter
        if (attr != null && attr.size() > 0) {
            filterPubSQL = "(SELECT * FROM pub WHERE ";
            for (String s : attr) {
                if (s.contains("maximumBeerPrice")) {
                    String value = s.split("=")[1];
                    if (filterPubSQL.endsWith("WHERE ")) {
                        filterPubSQL += "beer_price <= "+value;
                    } else {
                        filterPubSQL += " AND beer_price <= "+value;
                    }
                }
                if (s.equalsIgnoreCase("hasHappyHour")) {
                    if (filterPubSQL.endsWith("WHERE ")) {
                        filterPubSQL += "happy_hour";
                    } else {
                        filterPubSQL += " AND happy_hour";
                    }
                }
                if (s.equalsIgnoreCase("hasEntryFee")) {
                    entryFee = true;
                }
                
            }
            filterPubSQL += ")";
        }
        
        //create event filter
        if (e_attr != null && e_attr.size() > 0) {
            filterEventSQL = "(Select * FROM temporal_event WHERE ";
            
            for (String s : e_attr) {
                if (s.contains("eventType")) {
                    String value = s.split("=")[1];
                    if (filterEventSQL.endsWith("WHERE ")) {
                        filterEventSQL += "type = '"+value+"'";
                    } else {
                        filterEventSQL += " AND '"+value+"' like type";
                    }
                }
                if (entryFee) {
                    if (filterEventSQL.endsWith("WHERE ")) {
                        filterEventSQL += "entry_fee";
                    } else {
                        filterEventSQL += " AND entry_fee";
                    }
                }
            }
            filterEventSQL += ")";
        }
        
        sql = "SELECT * FROM "+filterPubSQL+" AS pub_select NATURAL INNER JOIN "+filterEventSQL+" AS event_select NATURAL INNER JOIN opened WHERE pub_select.pub_ref = event_select.pub_ref"+ generalFilterSQL + "AND start_time::date ='"+ dateFormatter.format(start)+"'";
        
        return sql;
    }
    
    protected void executeQuery(String query) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER).newInstance();
            logger.debug("Registerd jdbc driver with java application");
            
            //Open connection
            logger.debug("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            //Execute a query            
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            logger.debug("Query executed.");
            
            this.response = resultSetToOsmDoc(rs);
            
            //statement = connection.createStatement();
            //ResultSet rs2 = statement.executeQuery(SELECT start_time, end_time  FROM pub JOIN temporal_event ON pub.pub_ref = temporal_event.pub_ref JOIN opened ON temporal_event.event_id = opened.event_id WHERE pub.pub_ref =" +  pubId.toString());
            logger.debug("Query executed.");
            
            
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        
        logger.debug("Closed database connection");
    }  
    
    
    protected OsmDocument resultSetToOsmDoc(ResultSet rs) throws SQLException{
        OsmDocument osmDoc = OsmDocument.Factory.newInstance();
        OsmType osmElem = osmDoc.addNewOsm();       
        
        while (rs.next()) {
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            ArrayList<String> columnNameList = new ArrayList();
            
            for (int i = 1; i <= columnsNumber; i++) {
                columnNameList.add(rsmd.getColumnName(i));
            }
            
            NodeType pubNode = osmElem.addNewNode();
            Long pubId = null;
            if (columnNameList.contains("pub_ref")){
                Long pubRef = rs.getLong("pub_ref");
                pubId = pubRef;
                pubNode.setId(pubRef);
            }
            
            if (columnNameList.contains("beer_price")){
                Double beerPrice = rs.getDouble("beer_price");
                TagType beerPriceTag = pubNode.addNewTag();
                beerPriceTag.setK("price:beer");
                beerPriceTag.setV(String.valueOf(beerPrice));
            }
            
            if (columnNameList.contains("happy_hour")){
                Boolean hasHappyHour = rs.getBoolean("happy_hour");
                TagType happyHourTag = pubNode.addNewTag();
                happyHourTag.setK("happy_hour");
                happyHourTag.setV(String.valueOf(hasHappyHour));
            }       
            
            if (columnNameList.contains("event")){
                Boolean event = rs.getBoolean("event");
                if (event) {
                    EventType eventType = osmElem.addNewEvent();
                    if(columnNameList.contains("start_time")){
                        Calendar cal = new GregorianCalendar();
                        Date startTime = rs.getDate("start_time", cal);
                        cal.setTime(startTime);
                        eventType.setStart(cal);
                    }
                    
                    if (columnNameList.contains("end_time")){
                        Calendar cal = new GregorianCalendar();
                        Date endTime = rs.getDate("end_time", cal);
                        cal.setTime(endTime);
                        eventType.setEnd(cal);
                    }
                    
                    if (columnNameList.contains("name")){
                        String name = rs.getString("name");
                        TagType eventNameTag = eventType.addNewTag();
                        eventNameTag.setK("name");
                        eventNameTag.setV(name);
                    }
                    if (columnNameList.contains("type")){
                        String type = rs.getString("type");
                        TagType eventTypeTag = eventType.addNewTag();
                        eventTypeTag.setK("type");
                        eventTypeTag.setV(type);
                    }
                    if (columnNameList.contains("description")){
                        String description = rs.getString("description");
                        TagType eventDescriptionTag = eventType.addNewTag();
                        eventDescriptionTag.setK("desciption");
                        eventDescriptionTag.setV(description);
                    }
                    if (columnNameList.contains("entry_fee")){
                        String entryFee = rs.getString("entry_fee");
                        TagType eventCostTag = eventType.addNewTag();
                        eventCostTag.setK("cost");
                        eventCostTag.setV(entryFee);
                    }
                    
                    RelationType relationType = osmElem.addNewRelation();
                    MemberType memberType = relationType.addNewMember();
                    memberType.setRef(pubId); 
                    memberType.setRole("location");
                    memberType.setType(RelationReferType.NODE);
                }
            
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");    
            TagType tucTag = pubNode.addNewTag();
            tucTag.setK("tuc");
            tucTag.setV(df.format(rs.getDate("end_time")));
            
        }}
        
        System.out.print(osmDoc.toString());
        return osmDoc;
    }
    
    private long calculateTimeDifference(Date end){
         Calendar cal = new GregorianCalendar();
         Date now = Calendar.getInstance().getTime();
         long difference = (now.getTime() - end.getTime()) / 60000;
         logger.debug(String.valueOf(difference));
         return difference; 

    }
}

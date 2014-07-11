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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import noNamespace.OsmDocument;
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
                    start = (Date)query.get(Parameter.START);
                    end = (Date)query.get(Parameter.END);
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
    }
   
    protected String createSQLQuery(Date start, Date end, List<String> attr, List<String> e_attr) {
        String sql = "";
        boolean entryFee = false;
        String filterPubSQL = "pub";
        String filterEventSQL = "temporal_event";
        String generalFilterSQL = "";
        
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
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
        
        sql = "SELECT * FROM "+filterPubSQL+" AS pub_select NATURAL INNER JOIN "+filterEventSQL+" AS event_select WHERE pub_select.pub_ref = event_select.pub_ref"+ generalFilterSQL;
        
        return sql;
    }
    
    private void executeQuery(String query) {
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
            response = resultSetToOsmDoc(rs);            
            
            
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
    
    
    private OsmDocument resultSetToOsmDoc(ResultSet rs) throws SQLException{
        
        OsmDocument osmDoc = null;
        
        while (rs.next()) {
            // parse all values into node structure
  
        }
        
        return osmDoc;
    }   
        
}

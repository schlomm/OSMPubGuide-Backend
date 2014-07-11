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
import java.sql.*;
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
        
        for(Query query : this.queries){
            
            Date start = (Date) query.get(Parameter.START);
            Date end = (Date) query.get(Parameter.END);
            // hasHappyHour, hasEntryFee, maximumBeerPrice
            List<String> pubFilterList = (List<String>) query.get(Parameter.FILTER);
            // eventType
            List<String> eventFilterList = (List <String>) query.get(Parameter.EVENT_FILTER);
            
            // TODO case differentiation based on existing start, end .. parameters
            // which SQL statement is created and send to the DB
            executeQuery("SELECT * FROM pub");            
        }
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

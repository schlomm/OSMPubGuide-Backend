/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm.database;

import de.ifgi.ohbpgiosm.logging.MyLogger;
import java.sql.*;
import java.util.Properties;
import org.apache.log4j.*;

/**
 *
 * @author christopher
 */
public class PostgreSql {
    
    private static final Logger logger = MyLogger.getInstance().getLogger();
    private static final Properties dbCredentials = PropertyReader.getInstance().getProperty();
    // reading database credentials from properties file
    static final String JDBC_DRIVER = dbCredentials.getProperty("db_driver_name"); 
    static final String DB_URL = dbCredentials.getProperty("db_url");
    static final String PASSWORD = dbCredentials.getProperty("db_password");
    static final String USER = dbCredentials.getProperty("db_user");
    
    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        
        try {
            //Register JDBC driver
            Class.forName("org.postgresql.Driver").newInstance();
            logger.debug("Registerd jdbc driver with java application");
            
            //Open a connection
            logger.debug("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            //Execute a query
            logger.debug("Creating database...");
            statement = connection.createStatement();
//
//            String sql = "CREATE DATABASE STUDENTS";
//            stmt.executeUpdate(sql);
//           logger.debug("Database created successfully...");
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
        
        logger.debug("Goodbye!");
    }
}

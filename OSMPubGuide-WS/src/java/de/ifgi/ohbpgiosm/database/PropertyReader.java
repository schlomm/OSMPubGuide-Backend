/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifgi.ohbpgiosm.database;

import de.ifgi.ohbpgiosm.logging.MyLogger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author christopher
 */
public class PropertyReader {

    private static PropertyReader propertyReader;
    private static Properties property;
    private static InputStream input;
    private static final Logger logger = MyLogger.getInstance().getLogger();
    
    private PropertyReader(){
        property = new Properties();
        init(input);
    }
    
    public static PropertyReader getInstance(){
        if (propertyReader == null) {
            propertyReader = new PropertyReader();
        }
        return propertyReader;
    }
    
    private static void init(InputStream input) {     
        
        try {
            input = PropertyReader.class.getResourceAsStream("dbCredentials.properties");
            property.load(input);
            logger.debug("Loaded property file.");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    public static Properties getProperty(){
        return property;
               
    } 
                    
    

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import de.ifgi.ohbpgiosm.database.EventDatabaseConnector;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author florian
 */
public class EventDBTest extends EventDatabaseConnector{
    EventDatabaseConnector con = new EventDatabaseConnector();
    
    public EventDBTest() {
    }
    
    @Test
    public void createSQL() throws ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //TODO might be adapted for seconds
        Date start = formatter.parse("2014-07-15T19:00:00");
        Date end = formatter.parse("2014-07-15T23:00:00");
        List<String> filter = new ArrayList<String>();
        filter.add("maximumBeerPrice=3.0");
        filter.add("hasHappyHour");
        
        String test1 = this.createSQLQuery(start, null, filter, null);
        System.out.print(test1);
    }
}

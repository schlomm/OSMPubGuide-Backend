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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import noNamespace.EventType;
import noNamespace.OsmDocument;
import noNamespace.OsmType;
import org.junit.Test;

/**
 *
 * @author florian
 */
public class EventDBTest extends EventDatabaseConnector{
    EventDatabaseConnector con = new EventDatabaseConnector();
    
    private class FormattedCalendar extends GregorianCalendar {
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            return formatter.format(this.getTime());
        }
    }
    
    public EventDBTest() {
    }
    
    @Test
    public void createSQL() throws ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //TODO might be adapted for seconds
        Date start = formatter.parse("2014-07-15T19:00:00");
        //Date end = formatter.parse("2014-07-15T23:00:00");
        List<String> filter = new ArrayList<String>();
        filter.add("maximumBeerPrice=3.0");
        filter.add("hasHappyHour");
        
        String test1 = this.createSQLQuery(start, null, filter, null);
        this.executeQuery(test1);
        
        System.out.println(test1);
    }
    
    @Test
    public void makeEvent() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d1 = formatter.parse("2014-07-12T13:00:00");
        
        GregorianCalendar c = new GregorianCalendar();
        //FormattedCalendar c = new FormattedCalendar();
        c.setTime(d1);
        
        OsmDocument doc = OsmDocument.Factory.newInstance();
        OsmType osm = doc.addNewOsm();
        EventType et = osm.addNewEvent();
        et.setStart(c);
        
        System.out.println(doc);
    }
}

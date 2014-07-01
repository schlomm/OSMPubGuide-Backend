/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import de.ifgi.ohbpgiosm.util.QueryCreator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author florian
 */
public class ConnectorTest {
    
    private List<Query> buildQuery() throws ParseException {
        QueryCreator qc = QueryCreator.getInstance();
        HashMap<Parameter,String> input = new HashMap<>();
        input.put(Parameter.BBOX, "1.0,2.0,3.0,4.0");
        input.put(Parameter.FILTER, "hasHappyHour");
        
        List<Query> queries = qc.createQueries(input);
        
        return queries;
    }
    
    @Test
    public void createConnector() throws ParseException {
        List<Query> queries = this.buildQuery();
        List<Connector> connectors = new ArrayList<>();
        TestConnector c = new TestConnector(50000000);
        TestConnector c2 = new TestConnector(2000000000);
        
        //create the list of connectors
        connectors.add(c);
        connectors.add(c2);
        
        //add the corresponding queries to the connectors
        for (Query q : queries) {
            c.addQuery(q);
            c2.addQuery(q);
        }
        //QueryDelegator qd = new QueryDelegator(queries);
        ResponseMerger rm = new ResponseMerger();
        rm.addConnector(c);
        rm.addConnector(c2);
        
        c.addObserver(rm);
        c2.addObserver(rm);
        
        System.out.println("Start at "+new Date(System.currentTimeMillis()).toString());
        for (Connector con : connectors) {
            con.sendRequest();
        }
        System.out.println("End at "+new Date(System.currentTimeMillis()).toString());
    }
    
}

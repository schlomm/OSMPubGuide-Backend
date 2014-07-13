/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import de.ifgi.ohbpgiosm.database.EventDatabaseConnector;
import java.util.List;
import java.util.Vector;
import noNamespace.OsmDocument;

/**
 *
 * @author Florian
 */
public class QueryDelegator implements Runnable {
    private List<Query> queries = null;
    private Connector overpass_con = null;
    private Connector eventdb_con = null;
    private ResponseMerger rm = null;
    private OsmDocument doc = null;
    private List<Connector> connectors = null;
    
    public QueryDelegator(List<Query> queries) {
        this.queries = queries;
        this.connectors = new Vector<>();
        
        this.rm = new ResponseMerger();
        //this initializes the connectors
        this.createConnectors();
        
    }
    
    //TODO set correct return type (OsmDOcument/Document?)
    public OsmDocument getResponse() {
        this.rm.merge();
        return this.rm.getMergedResponse();
        /*
        if (this.rm.hasFinished()) {
            
        } else {
            throw new RuntimeException("ResponseMerger hasn't finished");
        }
                */
    }
    
    private void createConnectors() {
        //TODO set the correct connector classes in the future
        
        this.overpass_con = new OverpassConnector();
        this.eventdb_con = new EventDatabaseConnector();

    }
    
    private void initConnectors() {
        //add single connectors to the list of connectors
        //check if connector has a query
        if (this.overpass_con.hasQuery()) {
            this.connectors.add(this.overpass_con);
        }
        
        if (this.eventdb_con.hasQuery()) {
            this.connectors.add(this.eventdb_con);
        }
        
        //add observer to the connectors and vice versa
        for (Connector c : this.connectors) {
            c.addObserver(this.rm);
            this.rm.addConnector(c);
        }
    }
    
    private void delegate() {
        //distinguish the queries and add them to connector
        for (Query q : this.queries) {
            switch(q.getQueryType()) {
                case SPATIAL:
                    this.overpass_con.addQuery(q);
                    break;
                case TEMPORAL:
                case EVENT:
                    this.eventdb_con.addQuery(q);
                    break;
                case ATTRIBUTAL:
                    //if it contains beer prize add to event 
                    List<String> list = (List<String>)q.get(Parameter.FILTER);
                    for (String s : list) {
                        if (s == "maximumBeerPrice" || s == "hasHappyHour" || s == "hasEntryFee") {
                            this.eventdb_con.addQuery(q);
                            break;
                        }
                    }
                    this.overpass_con.addQuery(q);
                    //TODO check with Christopher that he is prepared
                    break;
            }
        }
    }
    
    @Override
    public void run() {
        //delegate queries
        this.delegate();
        
        this.initConnectors();
        //send requests
        for (Connector c : this.connectors) {
            c.sendRequest();
        }
        
    }
    
}

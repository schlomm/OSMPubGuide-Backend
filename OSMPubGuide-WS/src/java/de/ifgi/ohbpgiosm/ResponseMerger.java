/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import noNamespace.OsmDocument;
import noNamespace.OsmType;

/**
 *
 * @author florian
 */
public class ResponseMerger implements Observer {
    private int notifies = 0;
    private List<Connector> connectors = new ArrayList<>();
    private boolean finished;

    
    /**
     * Constructor
     */
    public ResponseMerger() {
        this.finished = false;
    }
    
    /**
     * Add a connector to the response merger that can be called for the information.
     * @param c A connector to a external source
     * @return 
     */
    public boolean addConnector(Connector c) {
       return connectors.add(c);
    }
    
    public boolean hasFinished() {
        return this.finished;
    }
    
    public OsmDocument merge() {
        OsmDocument osm = OsmDocument.Factory.newInstance();
        OsmType o = osm.addNewOsm();
        o.addNewEvent();
        o.addNewNode();
        o.addNewRelation();
        
        //do crazy stuff to the document
        
        //1. take the document from Overpass and add all elements to the osm document
        //2. take all events and relations from DB Osm document and add them to the osm document
        //3. if there are nodes or ways, then find them in the osm document and add the tags from the DB document
        
        return osm;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        this.notifies++;
        
        if (this.notifies == this.connectors.size()) { //if all connector have finished their request
            //this.merge();
            this.finished = true;
            Logger.getLogger("ResponseMerger").log(Level.INFO, "All done!");
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import java.util.ArrayList;
import java.util.List;
import noNamespace.OsmDocument;

/**
 *
 * @author florian
 */
public class ResponseMerger implements Observer {
    private byte notifies = 0;
    private List<Connector> connectors = new ArrayList<>();
    
    /**
     * Constructor
     */
    public ResponseMerger() {
        
    }
    
    /**
     * Add a connector to the response merger that can be called for the information.
     * @param c A connector to a external source
     * @return 
     */
    public boolean addConnector(Connector c) {
       return connectors.add(c);
    }
    
    public OsmDocument merge() {
        OsmDocument osm = OsmDocument.Factory.newInstance();
        
        //do crazy stuff to the document
        
        return osm;
    }
    
    @Override
    public void note() {
        this.notifies++;
        
        if (this.notifies == this.connectors.size()) {
            this.merge();
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

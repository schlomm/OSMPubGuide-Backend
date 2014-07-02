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
 * @author ondrej
 */
public class OverpassConnector extends Connector{
    
    private Query q;
    private OsmDocument osmDoc;
                
    //private List<Query> queries = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();
    
    /**
     * Constructor
     */
    public OverpassConnector(Query q) {
        addQuery(q);
    }
    
    
    @Override
    public void sendRequest(){
        //
        //OsmDocument.Factory.parse(null); //or other param - chjeck doucmentatiuon
       //return Object;
    }
    
    
    
    
    
    @Override    
    public boolean addQuery(Query q){
        this.queries.add(q);
        return true;
        //catch
    }
    
    
    
    public OsmDocument getResponse(){
        return this.osmDoc;
    }
    
  
}

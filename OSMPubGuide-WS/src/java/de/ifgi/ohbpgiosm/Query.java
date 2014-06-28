/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author florian
 */
public class Query<K,V> extends HashMap<K,V>{
    private QueryType qt = null;
    
    public Query(QueryType type) {
        super();
        this.qt = type;
    }
    
    public QueryType getQueryType() {
        return this.qt;
    }
    
    /*
    can later be removed or changed... this was implemented in combination with testing the differentiation of 
    */
    public String toXMLString() {
        String out = "<"+this.qt.name()+">";
        
        for (K key : this.keySet()) {
            List<Object> list = (List<Object>)this.get(key);
            out += "<element>";
            for (Object obj : list) {
                out+= "<key>"+key.toString()+"</key><value>"+obj.toString()+"</value>";
            }
            out+= "</element>";
            
            
        }
        
        out += "</"+this.qt.name()+">";
        
        return out;
    }
}

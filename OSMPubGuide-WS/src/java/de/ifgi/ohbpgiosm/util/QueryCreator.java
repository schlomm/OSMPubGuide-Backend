/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm.util;

import de.ifgi.ohbpgiosm.Parameter;
import de.ifgi.ohbpgiosm.Query;
import de.ifgi.ohbpgiosm.QueryType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.lang.Float;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author florian
 */
public class QueryCreator {
    private static QueryCreator instance = null;
    private final String LIST_SEPARATOR = ",";
    
    private QueryCreator () { 
        
    }
    
    public static QueryCreator getInstance() {
        if (QueryCreator.instance == null) {
            QueryCreator.instance = new QueryCreator();
        }
        return QueryCreator.instance;
    }
    
    /**
     * Differentiates the query types based on the submitted parameters.
     * 
     * @param parameters
     * @return 
     */
    public List<Query> createQueries(HashMap<Parameter,String> parameters) throws ParseException {
        ArrayList<Query> queryList = new ArrayList<>();
        Query<Parameter,List<Object>> space = new Query<>(QueryType.SPATIAL);
        Query<Parameter,List<Object>> time = new Query<>(QueryType.TEMPORAL);
        Query<Parameter,List<Object>> attribute = new Query<>(QueryType.ATTRIBUTAL);
        Query<Parameter,List<Object>> db_attr = new Query<>(QueryType.ATTRIBUTAL);
        Query<Parameter,List<Object>> eventAttr = new Query<>(QueryType.EVENT);
        
        
        //add parameters to the queries...
        Set<Parameter> ss = parameters.keySet();
        for (Parameter key : ss) {
            String value = parameters.get(key);
            if (value.isEmpty()) continue; //skip the parameter if empty
            
            switch (key) {
                case BBOX:
                    String[] temp1 = value.split(this.LIST_SEPARATOR);
                    ArrayList<Object> list1 = new ArrayList<>();
                    
                    for (int i = 0; i < temp1.length; i++) {
                        list1.add(new Float(temp1[i]));
                    }
                    
                    space.put(key, list1);
                    break;
                case FILTER:
                    String[] temp2 = value.split(this.LIST_SEPARATOR);
                    ArrayList<Object> overpass_attr_list = new ArrayList<>();
                    ArrayList<Object> db_attr_list = new ArrayList<>();
                    
                    for (int i = 0; i < temp2.length; i++) {
                        switch (temp2[i].split("=")[0]) {
                            case "maximumBeerPrice":
                            case "hasEntryFee":
                            case "hasHappyHour":
                                db_attr_list.add(temp2[i]);
                                break;
                            default:
                                overpass_attr_list.add(temp2[i]);
                                break;
                        }
                    }
                    if (overpass_attr_list.size() > 0) {
                        attribute.put(key, overpass_attr_list);
                    }
                    if (db_attr_list.size() > 0) {
                        db_attr.put(key, db_attr_list);
                    }
                    break;
                case EVENT_FILTER:
                    String[] temp3 = value.split(this.LIST_SEPARATOR);
                    ArrayList<Object> list3 = new ArrayList<>();
                    
                    for (int i = 0; i < temp3.length; i++) {
                        list3.add(temp3[i]);
                    }
                    eventAttr.put(key, list3);
                    break;
                case START:
                case END:
                    ArrayList<Object> list4 = new ArrayList<>();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    Date d = formatter.parse(value);
                    list4.add(d);
                    time.put(key, list4);
                    break;
            }
        }
        
        
        if(!space.isEmpty()) queryList.add(space);
        if(!time.isEmpty()) queryList.add(time);
        if(!attribute.isEmpty()) queryList.add(attribute);
        if(!eventAttr.isEmpty()) queryList.add(eventAttr);
        
        return queryList;
    }
}

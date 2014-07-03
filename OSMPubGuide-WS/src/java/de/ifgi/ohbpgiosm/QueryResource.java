/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import de.ifgi.ohbpgiosm.util.QueryCreator;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import noNamespace.OsmDocument;

/**
 * REST Web Service for querying (Temporal data) OSM
 *
 * @author Florian
 */
@Path("/query")
@RequestScoped
public class QueryResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of QueryResource
     */
    public QueryResource() {
    }

    /**
     * Queries the temporal extension Web service for the OSM data.
     * 
     * Example: http://localhost:8080/OSMPubGuide-WS/tosm/query?bbox=1,2,3,4&start=2014-06-27T19:00&end=2014-06-28T08:00&filter=hasFood,hasHappyHour&eventFilter=eventType=band
     * 
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml(
            @DefaultValue("") @QueryParam("bbox") String bboxString,
            @DefaultValue("") @QueryParam("start") String start,
            @DefaultValue("") @QueryParam("end") String end,
            @DefaultValue("") @QueryParam("filter") String filter,
            @DefaultValue("") @QueryParam("eventFilter") String eventFilter
            
    ){
        
        HashMap<Parameter,String> parameters = new HashMap<>();
        parameters.put(Parameter.BBOX, bboxString);
        parameters.put(Parameter.START, start);
        parameters.put(Parameter.END, end);
        parameters.put(Parameter.FILTER, filter);
        parameters.put(Parameter.EVENT_FILTER, eventFilter);
        
        QueryCreator qc = QueryCreator.getInstance();
        List<Query> queries = null;
        try {
            queries = qc.createQueries(parameters);
        } catch (ParseException ex) {
            Logger.getLogger(QueryResource.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>There was a parsing error</error>";
        } 
        //TODO implement Connector and the query delegation
        QueryDelegator delegator = new QueryDelegator(queries);

        Thread t = new Thread(delegator);
        
        try {
            /*
                The query delegator is a Runnable and this means that we can start
                Thread for this. A Thread is active for the time until all lines
                of code were executed.
            */
            
            t.start();
            t.join();
            OsmDocument osm = delegator.getResponse();

            return osm.toString();
        } catch (IllegalThreadStateException e) {
            t.interrupt();
            return "<error>There was an unexpected error while delegating the request.</error>";
        } catch (InterruptedException ex) {
            Logger.getLogger(QueryResource.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>A Thread was interrupted</error>";
        }
        
        
    }
    
    /**
     * Purely for testing the creation of the queries
     */
    private String testOutput(List<Query> queries) {
        String out = "<queries>";

        for (Query q : queries) {
            out+=q.toXMLString();
        }  

        out += "</queries>";
        
        return out;
    }

    /**
     * PUT method for updating or creating an instance of QueryResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}

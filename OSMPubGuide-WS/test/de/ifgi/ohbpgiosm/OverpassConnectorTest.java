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
import org.junit.Test;

import java.io.*;
import noNamespace.OsmDocument;
import org.apache.xmlbeans.XmlOptions;
import org.junit.Assert;

/**
 *
 * @author ondrej
 */
public class OverpassConnectorTest {

    private List<Query> buildQuery() throws ParseException {
        QueryCreator qc = QueryCreator.getInstance();
        HashMap<Parameter, String> input = new HashMap<>();
        input.put(Parameter.BBOX, "51.94892,7.5880337,51.973354,7.6595306");
        input.put(Parameter.FILTER, "isBarrierFree,hasFood,hasOutdoorSeats");
        List<Query> queries = qc.createQueries(input);
        return queries;
    }

    @Test
    public void testConnector() throws ParseException {
        List<Query> queries = this.buildQuery();
        Connector overpassConnector = new OverpassConnector();
        for (Query q : queries) {
            overpassConnector.addQuery(q);
        }
        overpassConnector.sendRequest();
        XmlOptions opts = new XmlOptions();
        opts.setCharacterEncoding("UTF-8");
        OsmDocument osmd = overpassConnector.getResponse();
        
        
        //System.out.print(overpassConnector.getResponse().validate(opts));
        Assert.assertTrue(overpassConnector.getResponse().validate()); //TODO fix OSM library to accept note and meta elements
        //System.out.print(osmd);
        //saveAsFile(overpassConnector, "txt");
    }

    public void saveAsFile(Connector overpassConnector, String ext) {
        try {
            FileWriter fstream = new FileWriter(System.currentTimeMillis() + "out." + ext);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(overpassConnector.getResponse().toString());
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

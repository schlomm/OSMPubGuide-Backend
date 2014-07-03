/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifgi.ohbpgiosm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import noNamespace.BboxQueryType;
import noNamespace.HasKvType;
import noNamespace.OsmDocument;
import noNamespace.OsmScriptDocument;
import noNamespace.OsmScriptType;
import noNamespace.QueryOSMType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author ondrej
 */
public class OverpassConnector extends Connector {

    private String hostName = "http://overpass-api.de/api/interpreter";

    /**
     * Constructor
     */
    public OverpassConnector() {
    }

    /**
     * Method to be called when Connector.sendRequest is called Composes the OSM
     * API script and and sends the actual request Stores internally the
     * response.
     *
     * @return void
     */
    @Override
    public void run() {
        XmlOptions opts = new XmlOptions();
        opts.setCharacterEncoding("UTF-8");

        OsmScriptDocument osd = OsmScriptDocument.Factory.newInstance(opts); //the document
        OsmScriptType ost = osd.addNewOsmScript(); 
        
        Document response;
        try {
            composeOsmScript(ost);
            response = HttpClient.getInstance().sendPostRequest(this.hostName, osd.toString());
            this.response = OsmDocument.Factory.parse(response);
        } catch (IOException ex) {
            Logger.getLogger(OverpassConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OverpassConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OverpassConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XmlException ex) {
            Logger.getLogger(OverpassConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OverpassConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method to compose xml request format for OSM Overpass API
     *
     * @param ost OsmScriptType
     * @return void
     */
    private void composeOsmScript(OsmScriptType ost) throws Exception {

        noNamespace.QueryType qt = ost.addNewQuery();
        qt.setType(QueryOSMType.NODE);

        HasKvType hkt = qt.addNewHasKv();
        hkt.setK("amenity");
        hkt.setV("pub");

        for (Query tempQ : queries) {
            switch (tempQ.getQueryType()) {
                case SPATIAL:
                    addSpatialSegment(qt, tempQ);
                    break;
                case ATTRIBUTAL:
                    addAtributalSegment(qt, tempQ);
                    break;
                default:
                    throw new Exception("Invalid attribute type.");
            }
        }
        ost.addNewPrint();
    }

    /**
     * Method to add BBOX segment of query to OSM Overpass API OsmScriptType
     * @param qt to add BBOX to
     * @param tempQ Query
     * @return void
     */
    private void addSpatialSegment(noNamespace.QueryType qt, Query tempQ) {
        BboxQueryType bbox = qt.addNewBboxQuery();
        for (Iterator it = tempQ.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            List<Object> list = (List<Object>) tempQ.get(key);
            
            if(checkBbox(list)){
               bbox.setS((Float) list.get(0));
               bbox.setW((Float) list.get(1));
               bbox.setN((Float) list.get(2));
               bbox.setE((Float) list.get(3));
            } else {
                throw new Error("Invalid format of BBOX parameter.");
            }
        }
    }

    /**
     * Method to check for correct format of BBOX
     * @param list List of Objects
     * @return boolean
     */
    private boolean checkBbox(List<Object> list){
        boolean isCorrect = false;
        
        if(list.size() == 4){
            isCorrect = true;
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i) ==  null) isCorrect = false;
            }
        }
        return isCorrect;
    }
    
     /**
     * Method to add ATTRIBUTE segment of query to OSM Overpass API OsmScriptType
     * @param qt to add BBOX to
     * @param tempQ Query
     * @return void
     */
    private void addAtributalSegment(noNamespace.QueryType qt, Query tempQ) {
        for (Iterator it = tempQ.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            List<Object> list = (List<Object>) tempQ.get(key);
            for (Object obj : list) {
                if(obj != null){
                    HasKvType hkt = qt.addNewHasKv();
                    hkt.setK(obj.toString());
                }
            }
        }
    }
}

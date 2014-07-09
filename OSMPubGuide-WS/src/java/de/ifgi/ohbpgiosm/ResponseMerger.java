/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifgi.ohbpgiosm;

import de.ifgi.ohbpgiosm.database.PostgreSql;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import noNamespace.OsmDocument;
import noNamespace.OsmType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author florian
 */
public class ResponseMerger implements Observer {

    private int notifies = 0;
    private List<Connector> connectors = new ArrayList<>();
    private boolean finished;
    private OsmDocument mergedResponse;

//    private OsmDocument osmResponse;
//    private OsmDocument dbResponse;
    //just for testing purpose:
    private Document osmResponse;
    private Document dbResponse;

    /**
     * Constructor
     */
    public ResponseMerger() {
        this.finished = false;
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

    /**
     * Add a connector to the response merger that can be called for the
     * information.
     *
     * @param c A connector to a external source
     * @return
     */
    public boolean addConnector(Connector c) {
        return connectors.add(c);
    }

    public boolean hasFinished() {
        return this.finished;
    }

    public void merge() {
        try {
            //first collect both responses
            //commented because of lack of DbConnector
            //replaced temporaly with addTestDbData() and addTestOsmData(),
            //to be removed after DbConnector is available
            //getConnectorsResponse();

            //create new OsmDocument as a container for final result
            mergedResponse = OsmDocument.Factory.newInstance();
            //mergedResponse = OsmDocument.Factory.parse(this.osmResponse);
//          OsmType o = osm.addNewOsm();
//          o.addNewEvent();
//          o.addNewNode();
//          o.addNewRelation();

            //do crazy stuff to the document
            //1. take the document from Overpass and add all elements to the osm document
            //2. take all events and relations from DB Osm document and add them to the osm document
            //3. if there are nodes or ways, then find them in the osm document and add the tags from the DB document
            mergeNodes();
            mergeOtherElements("/events/event");
            mergeOtherElements("/relations/relation");
        } catch (XPathExpressionException ex) {
            Logger.getLogger(ResponseMerger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ResponseMerger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //TODO: enable database connector when ready
    private void getConnectorsResponse() {
        for (int i = 0; i < this.connectors.size(); i++) {
            System.out.println(connectors.get(i).getClass());
            if (connectors.get(i) instanceof OverpassConnector) {
                //this.osmResponse = connectors.get(i).getResponse();
            } /*else if(connectors.get(i) instanceof PostgreSql){
             this.dbResponse = connectors.get(i).getResponse();
             }*/

        }
    }

    private void mergeNodes() throws XPathExpressionException, Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        //extract all <pub> from db response
        NodeList dbNodeList = (NodeList) xPath.compile("/items/pubs/pub").evaluate(this.dbResponse, XPathConstants.NODESET);

        //loop over all <pub> from db response
        for (int i = 0; i < dbNodeList.getLength(); i++) {
            //extract id of a current <pub>
            String dbCurrNodeId = dbNodeList.item(i).getAttributes().getNamedItem("id").getNodeValue();
            //extract all <tag> from current <pub>
            NodeList dbCurrNodeTags = dbNodeList.item(i).getChildNodes();

            //extract <node> from osm, referenced by <pub> id
            NodeList osmNodeList = (NodeList) xPath.compile("/osm/node[@id='" + dbCurrNodeId + "']").evaluate(this.osmResponse, XPathConstants.NODESET);
            //check if datasets are not empty
            if (osmNodeList.getLength() == 1 && dbCurrNodeTags.getLength() > 0) {
                Node osmNode = osmNodeList.item(0);
                for (int j = 0; j < dbCurrNodeTags.getLength(); j++) {
                    osmNode.appendChild(osmResponse.importNode(dbCurrNodeTags.item(j), true));
                }
            } else {
                throw new Exception("Duplicated ID of OSM elements.");
            }
        }
    }

    private void mergeOtherElements(String type) throws XPathExpressionException, Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        //extract all <events>/<relationship> from db response
        NodeList dbNodeList = (NodeList) xPath.compile("/items" + type).evaluate(this.dbResponse, XPathConstants.NODESET);
        NodeList osmRootNodes = (NodeList) xPath.compile("/osm").evaluate(this.osmResponse, XPathConstants.NODESET);

        if (osmRootNodes.getLength() == 1) {
            Node osmRootNode = osmRootNodes.item(0);
            //loop over all <event>/<relationship> from db response
            for (int i = 0; i < dbNodeList.getLength(); i++) {
                Node eventNode = dbNodeList.item(i);
                if (eventNode.hasChildNodes()) {
                    osmRootNode.appendChild(osmResponse.importNode(eventNode, true));
                } else {
                    throw new Exception("Duplicated ID of OSM elements.");
                }
            }
        }
    }

    public Document getMergedResponse() {
        return this.osmResponse;
    }

    /**
     * ***********************************************************************
     */
    /* TESTING PURPOSE 
     /***************************************************************************/
    //just for testing purpose
    public void addTestOsmData(Document response) {
        this.osmResponse = response;
    }

    //just for testing purpose
    public void addTestDbData(Document response) {
        this.dbResponse = response;
    }

}

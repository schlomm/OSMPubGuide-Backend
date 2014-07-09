/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifgi.ohbpgiosm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import noNamespace.OsmDocument;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Ondrej
 */
public class ResponseMergerTest {

    public static final String OSM_EXAMPLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<osm generator=\"Overpass API\" version=\"0.6\">\n"
            + " <node id=\"100\" lat=\"51.9529291\" lon=\"7.6396627\">\n"
            + "  <tag k=\"addr:city\" v=\"Münster\"/>\n"
            + "  <tag k=\"addr:country\" v=\"DE\"/>\n"
            + "  <tag k=\"addr:housenumber\" v=\"101\"/>\n"
            + "  <tag k=\"addr:postcode\" v=\"48155\"/>\n"
            + "  <tag k=\"addr:street\" v=\"Test Street\"/>\n"
            + "  <tag k=\"amenity\" v=\"pub\"/>\n"
            + "  <tag k=\"food\" v=\"yes\"/>\n"
            + "  <tag k=\"name\" v=\"Test Pub100\"/>\n"
            + "  <tag k=\"website\" v=\"www.test-pub.de\"/>\n"
            + "  <tag k=\"wheelchair\" v=\"no\"/>\n"
            + "	<tag k=\"outdoor_seating\" v=\"no\" />\n"
            + "</node>\n"
            + "<node id=\"200\" lat=\"51.9529291\" lon=\"7.6396627\">\n"
            + "  <tag k=\"addr:city\" v=\"Münster\"/>\n"
            + "  <tag k=\"addr:country\" v=\"DE\"/>\n"
            + "  <tag k=\"addr:housenumber\" v=\"101\"/>\n"
            + "  <tag k=\"addr:postcode\" v=\"48155\"/>\n"
            + "  <tag k=\"addr:street\" v=\"Test Street\"/>\n"
            + "  <tag k=\"amenity\" v=\"pub\"/>\n"
            + "  <tag k=\"food\" v=\"yes\"/>\n"
            + "  <tag k=\"name\" v=\"Test Pub200\"/>\n"
            + "  <tag k=\"website\" v=\"www.test-pub.de\"/>\n"
            + "  <tag k=\"wheelchair\" v=\"no\"/>\n"
            + "	<tag k=\"outdoor_seating\" v=\"no\" />\n"
            + "</node>\n"
            + "</osm>";

    public static final String DB_EXAMPLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<items>\n"
            + "	<pubs>\n"
            + "		<pub id=\"100\">\n"
            + "		  <tag k=\"price:beer\" v=\"1\"/>\n"
            + "			<tag k=\"opening_hours\" v=\"Su-Tu 11:00-01:00, We-Th 11:00-03:00, Fr 11:00-06:00, Sa 11:00-07:00\"/>\n"
            + "			<tag k=\"happy_hour\" v=\"no\" />\n"
            + "			<tag k=\"toc\" v=\"540\"/>\n"
            + "		</pub>\n"
            + "		<pub id=\"200\">\n"
            + "		  <tag k=\"price:beer\" v=\"2\"/>\n"
            + "			<tag k=\"opening_hours\" v=\"Su-Tu 11:00-01:00, We-Th 11:00-03:00, Fr 11:00-06:00, Sa 11:00-07:00\"/>\n"
            + "			<tag k=\"happy_hour\" v=\"yes\" />\n"
            + "			<tag k=\"toc\" v=\"540\"/>\n"
            + "		</pub>\n"
            + "	</pubs>\n"
            + "	<events>\n"
            + "		<event id=\"10000001\" start=\"2014-07-05T21:00:00\" end=\"\">\n"
            + "			<tag k=\"name\" v=\"BandXYZgig\" />\n"
            + "			<tag k=\"type\" v=\"concert\" />\n"
            + "			<tag k=\"description\" v=\"This is a famous band and they are playing their latest album.\" />\n"
            + "			<tag k=\"cost\" v=\"paid\"/>\n"
            + "		</event>\n"
            + "		<event id=\"10000001\" start=\"2014-07-05T21:00:00\" end=\"\">\n"
            + "			<tag k=\"name\" v=\"Band XYZ gig\" />\n"
            + "			<tag k=\"type\" v=\"concert\" />\n"
            + "			<tag k=\"description\" v=\"This is a famous band and they are playing their latest album.\" />\n"
            + "			<tag k=\"cost\" v=\"paid\"/>\n"
            + "		</event>\n"
            + "		<event id=\"10000002\" start=\"2014-07-06T20:00:00\" end=\"\">\n"
            + "			<tag k=\"name\" v=\"Band XYZ gig\" />\n"
            + "			<tag k=\"type\" v=\"concert\" />\n"
            + "			<tag k=\"description\" v=\"This is a famous band and they are playing their latest album.\" />\n"
            + "		</event>\n"
            + "	</events>\n"
            + "	<relations>\n"
            + "		<relation id=\"7654321\">\n"
            + "			<member type=\"node\" ref=\"\" role=\"location\" />\n"
            + "			<member type=\"event\" ref=\"10000001\" role=\"event\" />\n"
            + "			<tag k=\"type\" v=\"event_list\" />\n"
            + "		</relation>\n"
            + "		<relation id=\"7654322\">\n"
            + "			<member type=\"node\" ref=\"\" role=\"location\" />\n"
            + "			<member type=\"event\" ref=\"10000002\" role=\"event\" />\n"
            + "			<tag k=\"type\" v=\"event_list\" />\n"
            + "		</relation>\n"
            + "	</relations>\n"
            + "</items>";

    @Test
    public void runTest() throws XPathExpressionException, ParserConfigurationException {
        DocumentBuilderFactory builderFactory;
        builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = builderFactory.newDocumentBuilder();

        Document osmExample = loadExample(builder, OSM_EXAMPLE);
        Document dbExample = loadExample(builder, DB_EXAMPLE);

        //testXmlValidity(osmExample, "/osm/node/tag[@k='name']/@v");
        //testXmlValidity(dbExample, "/pubs/pub/@id");
        ResponseMerger merger = new ResponseMerger();

        //OverpassConnector connector = new OverpassConnector();
        //merger.addConnector(connector);
        merger.addTestOsmData(osmExample);
        merger.addTestDbData(dbExample);

        merger.merge();
        try {
            printResult(merger);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(ResponseMergerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ResponseMergerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Document loadExample(DocumentBuilder builder, String con) {
        Document xmlDocument = null;
        try {
            xmlDocument = builder.parse(new ByteArrayInputStream(con.getBytes()));
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ResponseMergerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlDocument;
    }

    private void testXmlValidity(Document xmlDocument, String expression) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            System.out.println(nodeList.item(i).getFirstChild().getNodeValue());
        }
    }

    private void printResult(ResponseMerger merger) throws TransformerConfigurationException, TransformerException {
        Document result = merger.getMergedResponse();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult res = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(result);
        transformer.transform(source, res);

        String xmlOutput = res.getWriter().toString();
        System.out.println(xmlOutput);
    }
}

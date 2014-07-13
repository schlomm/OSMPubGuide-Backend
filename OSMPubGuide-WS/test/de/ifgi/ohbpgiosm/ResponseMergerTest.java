/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifgi.ohbpgiosm;

import noNamespace.OsmDocument;
import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Ondrej
 */
public class ResponseMergerTest extends ResponseMerger{

    private String OSM_EXAMPLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
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
            + "<node id=\"300\" lat=\"51.9529291\" lon=\"7.6396627\">\n"
            + "  <tag k=\"addr:city\" v=\"Münster\"/>\n"
            + "  <tag k=\"addr:country\" v=\"DE\"/>\n"
            + "  <tag k=\"addr:housenumber\" v=\"109\"/>\n"
            + "  <tag k=\"addr:postcode\" v=\"48155\"/>\n"
            + "  <tag k=\"addr:street\" v=\"Test Street\"/>\n"
            + "  <tag k=\"amenity\" v=\"pub\"/>\n"
            + "  <tag k=\"food\" v=\"yes\"/>\n"
            + "  <tag k=\"name\" v=\"Test Pub300\"/>\n"
            + "  <tag k=\"website\" v=\"www.test-pub3.de\"/>\n"
            + "  <tag k=\"wheelchair\" v=\"limited\"/>\n"
            + "	<tag k=\"outdoor_seating\" v=\"no\" />\n"
            + "</node>\n"
            + "</osm>";

    private final String DB_EXAMPLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<osm>\n"
            + "		<node id=\"100\">\n"
            + "		  <tag k=\"price:beer\" v=\"1\"/>\n"
            + "			<tag k=\"opening_hours\" v=\"Su-Tu 11:00-01:00, We-Th 11:00-03:00, Fr 11:00-06:00, Sa 11:00-07:00\"/>\n"
            + "			<tag k=\"happy_hour\" v=\"no\" />\n"
            + "			<tag k=\"tuc\" v=\"540\"/>\n"
            + "		</node>\n"
            + "		<node id=\"200\">\n"
            + "		  <tag k=\"price:beer\" v=\"2\"/>\n"
            + "			<tag k=\"opening_hours\" v=\"Su-Tu 11:00-01:00, We-Th 11:00-03:00, Fr 11:00-06:00, Sa 11:00-07:00\"/>\n"
            + "			<tag k=\"happy_hour\" v=\"yes\" />\n"
            + "			<tag k=\"tuc\" v=\"540\"/>\n"
            + "		</node>\n"
            + "		<event id=\"10000001\" start=\"2014-07-05T21:00:00\" end=\"\">\n"
            + "			<tag k=\"name\" v=\"BandXYZgig\" />\n"
            + "			<tag k=\"type\" v=\"concert\" />\n"
            + "			<tag k=\"description\" v=\"This is a famous band and they are playing their latest album.\" />\n"
            + "			<tag k=\"cost\" v=\"paid\"/>\n"
            + "		</event>\n"
            + "		<event id=\"10000002\" start=\"2014-07-06T20:00:00\" end=\"\">\n"
            + "			<tag k=\"name\" v=\"Band XYZ gig\" />\n"
            + "			<tag k=\"type\" v=\"concert\" />\n"
            + "			<tag k=\"description\" v=\"This is a famous band and they are playing their latest album.\" />\n"
            + "		</event>\n"
            + "		<relation id=\"7654321\">\n"
            + "			<member type=\"node\" ref=\"100\" role=\"location\" />\n"
            + "			<member type=\"event\" ref=\"10000001\" role=\"event\" />\n"
            + "			<tag k=\"type\" v=\"event_list\" />\n"
            + "		</relation>\n"
            + "		<relation id=\"7654322\">\n"
            + "			<member type=\"node\" ref=\"200\" role=\"location\" />\n"
            + "			<member type=\"event\" ref=\"10000002\" role=\"event\" />\n"
            + "			<tag k=\"type\" v=\"event_list\" />\n"
            + "		</relation>\n"
            + "</osm>";

    @Before
    public void setup() throws XmlException {
        this.dbResponse = OsmDocument.Factory.parse(this.DB_EXAMPLE);
        this.osmResponse = OsmDocument.Factory.parse(this.OSM_EXAMPLE);
        //this.mergedResponse = null;
    }
    
    @Test
    public void testMerge() {
        this.merge();
        System.out.println(this.mergedResponse);
    }

            /*
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
        //merger.addTestOsmData(osmExample);
        //merger.addTestDbData(dbExample);

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
 */
    /*
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
    */
    
    
}

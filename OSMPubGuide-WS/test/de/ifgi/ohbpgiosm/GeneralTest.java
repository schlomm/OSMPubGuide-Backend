/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import noNamespace.EventType;
import noNamespace.MemberType;
import noNamespace.NodeType;
import noNamespace.OsmDocument;
import noNamespace.OsmType;
import noNamespace.RelationReferType;
import noNamespace.RelationType;
import noNamespace.TagType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author florian
 */
public class GeneralTest {
    
    public GeneralTest() {
    }

    @Test
    public void FinalTest() throws IOException, ParserConfigurationException, MalformedURLException, SAXException {

        String testUrl1 = "http://giv-openpubguide.uni-muenster.de:8080/"
                        + "OSMPubGuide-WS/tosm/query?bbox=51.94892,7.5880337,51.973354,7.6595306";

        String testUrl2 = "http://giv-openpubguide.uni-muenster.de:8080/OSMPubGuide-WS/tosm/query?"
                + "bbox=51.94892,7.5880337,51.973354,7.6595306&"
                + "start=2014-07-15T21:00:00&end=2014-07-15T23:00:00&";

        
        Document response;
        try {
            response = HttpClient.getInstance().sendGetRequest(testUrl1);
            saveAsFile(stringify(response));
        } catch (TransformerException ex) {
            Logger.getLogger(GeneralTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String stringify(Document result) throws TransformerConfigurationException, TransformerException {

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult res = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(result);
        transformer.transform(source, res);

        String xmlOutput = res.getWriter().toString();
        return xmlOutput;
    }
    
    public void saveAsFile(String response) {
        try {
            File file = new File("/home/brasko/testresults/" + System.currentTimeMillis() + "out.xml");
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(response);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public void testFindNode() {
        OsmDocument doc = OsmDocument.Factory.newInstance();
        OsmType osm =  doc.addNewOsm();
        
        NodeType n1 = osm.addNewNode();
        NodeType n2 = osm.addNewNode();
        NodeType n3 = osm.addNewNode();
        EventType et = osm.addNewEvent();
        
        
        n1.setId(10000);
        TagType tt = n1.addNewTag();
        tt.setK("hallo");
        tt.setV("spencer");
        n2.setId(20000);
        n3.setId(30000);
        et.setId(1);
        RelationType rt = osm.addNewRelation();
        rt.setId(8888888);
        
        MemberType mt1 = rt.addNewMember();
        MemberType mt2 = rt.addNewMember();
        
        mt1.setRef(10000);
        mt1.setRole("location");
        mt1.setType(RelationReferType.NODE);
        mt2.setRef(1);
        mt2.setRole("event");
        mt2.setType(RelationReferType.EVENT);
        
        //System.out.println(doc);
        try {
            NodeType nt = this.findNode(10000, doc);
            if (nt != null) {
                System.out.println(nt);
            }
            
            MemberType mt = this.findMemberNode(8888888, doc);
            if (mt != null) {
                System.out.println(mt);
            }
            
        } catch (XmlException ex) {
            Logger.getLogger(GeneralTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private MemberType findMemberNode(long id, OsmDocument doc) throws XmlException {
        XmlObject[] bla = doc.execQuery("/osm/relation[@id="+id+"]/member[@role='location' and @type='node']");
        if (bla.length > 0) {
            return MemberType.Factory.parse(bla[0].toString());
        } else {
            return null;
        }
    }
    
    
    private NodeType findNode(long id, OsmDocument doc) throws XmlException {
        
        XmlObject[] bla = doc.execQuery("/osm/node[@id="+id+"]");
        
        if (bla.length > 0) {
            //System.out.println(results[0]);
            //NodeType node = NodeType.Factory.newInstance();
            
            return NodeType.Factory.parse(bla[0].toString());
        }
        
        return null;
    }
}

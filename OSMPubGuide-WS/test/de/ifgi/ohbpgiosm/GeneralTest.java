/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author florian
 */
public class GeneralTest {
    
    public GeneralTest() {
    }

    @Test
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

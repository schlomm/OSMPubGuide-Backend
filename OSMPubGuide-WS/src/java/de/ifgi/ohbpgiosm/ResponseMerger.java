/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifgi.ohbpgiosm;

import de.ifgi.ohbpgiosm.database.EventDatabaseConnector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import noNamespace.EventType;
import noNamespace.MemberType;
import noNamespace.NodeType;
import noNamespace.OsmDocument;
import noNamespace.OsmType;
import noNamespace.RelationType;
import noNamespace.TagType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

/**
 *
 * @author florian
 */
public class ResponseMerger implements Observer {

    private int notifies = 0;
    private List<Connector> connectors = new ArrayList<>();
    private boolean finished;
    protected OsmDocument mergedResponse;

//    private OsmDocument osmResponse;
//    private OsmDocument dbResponse;
    //just for testing purpose:
    protected OsmDocument osmResponse;
    protected OsmDocument dbResponse;

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
            this.merge();
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
        this.getConnectorsResponse();

        
        if (this.osmResponse == null) {
            throw new RuntimeException("Overpass connector was not set");
        }
        
        if (this.dbResponse == null) { //just spatial
            this.mergedResponse = this.osmResponse;
            return;
        } else {
            this.mergeDocuments();
        }
    }

    //TODO: enable database connector when ready
    private void getConnectorsResponse() {
        for (Connector c: this.connectors) {
            if (c instanceof EventDatabaseConnector) {
                this.dbResponse = c.getResponse();
            } else if (c instanceof OverpassConnector) {
                this.osmResponse = c.getResponse();
            }
        }
    }
    
    /**
     * When this method is called it is assumed, that both documents were found.
     */
    private void mergeDocuments() {
        this.mergedResponse = OsmDocument.Factory.newInstance();
        OsmType osm = this.mergedResponse.addNewOsm();
        
        //loop through the db document for nodes
        List<NodeType> nodes = Arrays.asList(this.dbResponse.getOsm().getNodeArray());
        List<NodeType> n_match = new ArrayList<>();
        
        for( NodeType db_node : nodes) {
            try {
                NodeType osm_node = this.findNode(db_node.getId(), this.osmResponse);
                //look for corresponces
                
                if (osm_node != null) {
                    
                    NodeType newNode = this.mergedResponse.getOsm().addNewNode();
                    newNode.setId(osm_node.getId());
                    newNode.setChangeset(osm_node.getChangeset());
                    newNode.setLat(osm_node.getLat());
                    newNode.setLon(osm_node.getLon());
                    newNode.setTimestamp(osm_node.getTimestamp());
                    newNode.setUid(osm_node.getUid());
                    newNode.setUser(osm_node.getUser());
                    newNode.setVersion(osm_node.getVersion());
                    newNode.setVisible(osm_node.getVisible());
                    
                    //if you find a match --> it means the spatial boundary argument is satisfied
                    TagType[] oldTags = osm_node.getTagArray();
                    TagType[] newTags = db_node.getTagArray();
                    //System.out.println(oldTags.length+"   "+newTags.length);
                    for (int i = 0; i < oldTags.length; i++) {
                        TagType newt = newNode.addNewTag();
                        newt.setK(oldTags[i].getK());
                        newt.setV(oldTags[i].getV());
                    }
                    for (int i = 0; i < newTags.length; i++) {
                        TagType newt = newNode.addNewTag();
                        newt.setK(newTags[i].getK());
                        newt.setV(newTags[i].getV());
                    }
                } else {
                    continue; //else continue, because the bounding box is that small, that the temporal node does not satisfy the condition
                }
                
            } catch (XmlException ex) {
                Logger.getLogger(ResponseMerger.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
        }        
        //add nodes to mergedResponse
        
        //get the relations from the db doc
        List<RelationType> rels = (List<RelationType>)Arrays.asList(this.dbResponse.getOsm().getRelationArray());
        List<RelationType> r_match = new ArrayList<RelationType>();
        List<EventType> events = new ArrayList<>();
        
        //loop through the list
        for (RelationType rt : rels) {
            try {
                //check at each relation if the node is contained in the mergedDocument (new one)
                // this means looking for the member with type "node"
                long node_ref = this.getRelationLocationID(rt, this.dbResponse);
                
                // if node is contained then add the relation and the event
                if (node_ref > 0) {
                    r_match.add(rt); //put into matches for relation
                    //this.copy(rt, this.mergedResponse);
                    EventType[] r_events = this.getEventsForRelation(rt, this.dbResponse);

                    events.addAll((List<EventType>)Arrays.asList(r_events));
                }
                //note: the event has to be in, because otherwise there would not have been a relation
            } catch (XmlException ex) {
                Logger.getLogger(ResponseMerger.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
        }
        for (EventType et : events) {
            this.copy(et, this.mergedResponse);
        }
        for (RelationType rt: r_match) {
            this.copy(rt,this.mergedResponse);
        }
    }
    
    private long getRelationLocationID(RelationType rt, OsmDocument doc) throws XmlException {
        System.out.println("search for relation with id: "+rt.getId());
        XmlObject[] xmls = doc.selectPath("/osm/relation[@id="+rt.getId()+"]/member[@role='location']");
        
        if (xmls.length > 0) {
            return MemberType.Factory.parse(xmls[0].toString()).getRef();
        } else return -1;
    }
    
    private EventType[] getEventsForRelation(RelationType rt, OsmDocument doc) throws XmlException {
        List<EventType> events = new ArrayList<>();
        List<Long> event_ids = new ArrayList<>();
        
        //get the ids of the events of the relation
        XmlObject[] event_members = doc.selectPath("/osm/relation[@id="+rt.getId()+"]/member[@role='event' and @type='event']");
        for (int i= 0; i < event_members.length; i++) {
            MemberType mt = MemberType.Factory.parse(event_members[i].toString());
            event_ids.add(mt.getRef());
        }
        //find the ids based on the references in the id list
        for (long l : event_ids) {
            XmlObject[] e = doc.selectPath("/osm/event[@id="+l+"]");
            //there has to be the event!
            events.add(EventType.Factory.parse(e[0].toString()));
        }
        EventType[] ets = new EventType[events.size()];
        for (int i = 0; i < events.size(); i++) {
            ets[i] = events.get(i);
        }
        
        return ets;
    }
    
    private void copy(Object node, OsmDocument target) {
        if (node instanceof RelationType) {
            RelationType rt = (RelationType)node;
            RelationType newt = target.getOsm().addNewRelation();
            newt.setId(rt.getId());
            newt.setTimestamp(rt.getTimestamp());
            newt.setUid(rt.getUid());
            newt.setUser(rt.getUser());
            newt.setVersion(rt.getVersion());
            newt.setVisible(rt.getVisible());
            
            MemberType[] members = rt.getMemberArray();
            for (int i = 0; i < members.length; i++) {
                MemberType new_member = newt.addNewMember();
                new_member.setRef(members[i].getRef());
                new_member.setRole(members[i].getRole());
                new_member.setType(members[i].getType());
            }
            
            TagType[]tags = rt.getTagArray();
            for (int i = 0; i < tags.length;i++) {
                TagType new_tag = newt.addNewTag();
                new_tag.setK(tags[i].getK());
                new_tag.setV(tags[i].getV());
            }
        }
        if (node instanceof EventType) {
            EventType et = (EventType)node;
            EventType newe = target.getOsm().addNewEvent();
            
            newe.setId(et.getId());
            newe.setTimestamp(et.getTimestamp());
            newe.setUid(et.getUid());
            newe.setUser(et.getUser());
            newe.setVersion(et.getVersion());
            newe.setVisible(et.getVisible());
            newe.setStart(et.getStart());
            
            if (!et.isSetEnd()) {
                newe.setEnd(et.getEnd());
            }
            
            TagType[]tags = et.getTagArray();
            for (int i = 0; i < tags.length;i++) {
                TagType new_tag = newe.addNewTag();
                new_tag.setK(tags[i].getK());
                new_tag.setV(tags[i].getV());
            }
        }
    }
    
    private NodeType findNode(long id, OsmDocument doc) throws XmlException {
        XmlObject[] results = doc.selectPath("/osm/node[@id="+id+"]");
        
        if (results.length > 0) {
            NodeType nt = NodeType.Factory.parse(results[0].toString());
            return nt;
        }
        
        return null;
        
    }
    
    public OsmDocument getMergedResponse() {
        return this.mergedResponse;
    }
}

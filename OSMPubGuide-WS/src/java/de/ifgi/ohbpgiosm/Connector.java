package de.ifgi.ohbpgiosm;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import noNamespace.OsmDocument;

/**
 *
 * @author florian
 */
public abstract class Connector extends Observable implements Runnable{
    /**
     * This list contains all queries that are set on a connector.
     */
    protected List<Query> queries = new ArrayList<>();
    
    /**
     * The OsmDocument that needs to be created, when the run method is 
     * executed (as the response of a request)
     */
    protected OsmDocument response = null;
    
    /**
     * The sendRequest method creates a Thread and starts it. The actual request
     * of the connector has to be defined in the run method.
     */
    public void sendRequest() {
        if (queries.size() > 0) {
            try {
                Thread thr = new Thread(this);
                thr.start();
                thr.join();

            } catch (InterruptedException ex) {
                Logger.getLogger(TestConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new IllegalArgumentException("Connector was intended to perform a request, but no query was added on "+this.getClass());
        }
    };
    
    /**
     * Adds a query to the connector that is needed to send a request to the external
     * source.
     * 
     * @param q A query as defined also in this package
     * @return True if the query was successfully added, false otherwise.
     */
    public boolean addQuery(Query q) {
        return this.queries.add(q);
    }
    
    /**
     * This method returns a OsmDocument. It is intended to call this method after the sendRequest function was
     * executed and the response has been received. It basically means that the registered observer accesses the
     * repsonse after it was notified.
     * 
     * @return noNamespace.OsmDocument The extended OsmDocument, which can contain the event data or the standard OSM data structures.
     */
    public OsmDocument getResponse() {
        return this.response;
    }
    
    /**
     * This method retrieves information whether or not a connector has queries. If
     * no queries are present it would mean, that no query parameters were set. So,
     * we don't need to execute it.
     * 
     * @return true if at least on query is set
     */
    public boolean hasQuery() {
        return !queries.isEmpty();
    };
    
    /**
     * This method calls the protected method setChange() which is needed, to declare that
     * a change on the object has happened. To trigger an event on a registered observer.
     */
    public void finish() {
        this.setChanged();
    }
    
    /**
     * The run method is inherited from the Runnable class from which we create a Thread. This means that this
     * method needs to execute all the information gathering (e.g. query in the data base or request to the server)
     */
    public abstract void run();
}

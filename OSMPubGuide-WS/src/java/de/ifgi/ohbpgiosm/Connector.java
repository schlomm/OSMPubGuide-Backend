/*
 * Classes implementing this interface are meant to connect to external data
 * sources. For example those data sources can be web services or a data base.
 * 
 * In gerneral these classes operate via requests and might be synchron. This
 * means that the connector sends a request to the data source and a file or
 * class will be returned, which then is processed in one of a observer.
 */

package de.ifgi.ohbpgiosm;

/**
 *
 * @author florian
 */
public interface Connector {
    
    /**
     * This method creates a query in the form that the external data source / 
     * service accepts. A connector contains already a query and if one is set
     * then a request will be created and send to the external source / service.
     * 
     * @return An object that is received from the external data source / service
     */
    public Object sendRequest();
    
    /**
     * This method allows to register an observer that is notified after we have
     * got an external response.
     * 
     * @param o Observer that handles the response from extern.
     * @return True if the Observer was successfully added, false otherwise
     */
    public boolean register(Observer o);
    
    /**
     * Adds a query to the connector that is needed to send a request to the external
     * source.
     * 
     * @param q A query as defined also in this package
     * @return True if the query was successfully added, false otherwise.
     */
    public boolean addQuery(Query q);
}

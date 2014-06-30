package de.ifgi.ohbpgiosm;

import java.io.IOException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.QueryParam;


/**
 * @author christopher
 *
 */


//Sets the path to base URL + /pubs
@Path("/pubs")
public class Pubs {

	/**
	 * 
	 * Example requests: 
	 * http://localhost:8080/de.ifgi.ohbpgiosm/rest/pubs/getpubswithinbbox
	 * http://localhost:8080/de.ifgi.ohbpgiosm/rest/pubs/getpubswithinbbox?south=41.886288445510516&west=12.483901977539062&north=41.893700240146295&east=12.500102519989014
	 * http://localhost:8080/de.ifgi.ohbpgiosm/rest/pubs/getpubswithinbbox?south=51.948920578035946&west=7.58803367614746&north=51.97335506954467&east=7.659530639648438
	 * 
	 * @param south: south boundary
	 * @param west:
	 * @param north:
	 * @param east:
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	
	@GET
	@Path("/getpubswithinbbox")
	@Produces(MediaType.TEXT_XML)
	public Document getPubsWithinBBox(@DefaultValue("51.948920578035946") @QueryParam("south") String south,
			@DefaultValue("7.58803367614746") @QueryParam("west") String west,
			@DefaultValue("51.97335506954467") @QueryParam("north") String north,
			@DefaultValue("7.659530639648438") @QueryParam("east") String east) throws IOException, ParserConfigurationException, SAXException {
		
		String hostName = "http://overpass-api.de/api/interpreter";
		String queryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><osm-script><query type=\"node\"><has-kv k=\"amenity\" v=\"pub\"/><bbox-query s=\""
				+ south
				+ "\" w=\""
				+ west
				+ " \" n=\""
				+ north
				+ "\" e=\""
				+ east + "\"/></query><print/></osm-script>";
		
		Document response = HttpClient.getInstance().sendPostRequest(hostName, queryString);
		
		return response;
	}

}

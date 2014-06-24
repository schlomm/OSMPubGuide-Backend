package de.ifgi.ohbpgiosm;

import java.io.IOException;
import java.io.StringReader;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import noNamespace.BboxQueryType;
import noNamespace.HasKvType;
import noNamespace.OsmDocument;
import noNamespace.OsmScriptDocument;
import noNamespace.OsmScriptType;
import noNamespace.QueryOSMType;
import noNamespace.QueryType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


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
	 * http://localhost:8080/OSMPubGuide-WS/request/pubs/getpubswithinbbox
	 * http://localhost:8080/OSMPubGuide-WS/request/pubs/getpubswithinbbox?south=41.886288445510516&west=12.483901977539062&north=41.893700240146295&east=12.500102519989014
	 * http://localhost:8080/OSMPubGuide-WS/request/pubs/getpubswithinbbox?south=51.948920578035946&west=7.58803367614746&north=51.97335506954467&east=7.659530639648438
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
			@DefaultValue("7.659530639648438") @QueryParam("east") String east) throws IOException, ParserConfigurationException, SAXException, XmlException {
		
		String hostName = "http://overpass-api.de/api/interpreter";
                
                //create the OSM-script document for the Overpass query
                /*
                <osm-script>
                    <query type="node">
                        <has-kv k="amenity" v="pub"/>
                        <bbox-query s="" w="" n="" e=""/>
                    </query>
                    <print/>
                </osm-script>
                */
                XmlOptions opts = new XmlOptions();
                opts.setCharacterEncoding("UTF-8");
                
                OsmScriptDocument osd = OsmScriptDocument.Factory.newInstance(opts); //the document
                OsmScriptType ost = osd.addNewOsmScript(); //add the osm script type
                    QueryType qt = ost.addNewQuery(); //add a query
                        qt.setType(QueryOSMType.NODE); //set the query type

                    HasKvType hkt = qt.addNewHasKv(); //add the has key-value query constraint
                        hkt.setK("amenity"); //set key
                        hkt.setV("pub"); //set value

                    BboxQueryType bbox = qt.addNewBboxQuery(); //add bounding box query
                        //set coordinates
                        bbox.setS(Float.parseFloat(south));
                        bbox.setW(Float.parseFloat(west));
                        bbox.setN(Float.parseFloat(north));
                        bbox.setE(Float.parseFloat(east));
                ost.addNewPrint(); //also add the print element (important for getting information back)
                        
		Document response = HttpClient.getInstance().sendPostRequest(hostName, osd.toString());
		
                OsmDocument rdoc = OsmDocument.Factory.parse(response);
                //maybe do some cool stuff with it, e.g. merging
                
                //convert back to a dom document so that Jersey is happy...   
		return this.convertXMLBeansToDomDoc(rdoc);
	}
        /*
         * Basically this function creates a String from the XMLBeans document and parses this String to
         * a DOM document.
         */
        private Document convertXMLBeansToDomDoc(OsmDocument beans) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder; 
            Document doc = null;
            try 
            {  
                builder = factory.newDocumentBuilder();  
                doc = builder.parse( new InputSource( new StringReader( beans.toString() ) ) ); 
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
            return doc;
        }

}

package de.ifgi.ohbpgiosm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import noNamespace.OsmDocument;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author christopher
 *
 * TODO Use Apache HTTP client http://hc.apache.org/httpclient-3.x/
 */
public class HttpClient {

    private static HttpClient instance = null;

    private HttpClient() {
    }

    ;
	
	public static HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }
        return instance;
    }

    public Document sendPostRequest(String hostName, String queryString) throws IOException, ParserConfigurationException, SAXException {

        URL osm = new URL(hostName);
        HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
        printout.writeBytes("data=" + URLEncoder.encode(queryString, "utf-8"));
        printout.flush();
        printout.close();

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document response = docBuilder.parse(connection.getInputStream());

        return response;
    }

    public Document sendGetRequest(String url) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/xml");

        int responseCode = con.getResponseCode();
        System.out.println("Sending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document response = db.parse(con.getInputStream());

        return response;
    }
}

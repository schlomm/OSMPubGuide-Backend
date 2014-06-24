/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 *
 * @author florian
 */
public class URLValidation {
    @Test
    public void validateURLs() {
        String url1 = "http://giv-openpubguide.uni-muenster.de:8080/de.ifgi.ohbpgiosm/rest/pubs/getpubswithinbbox?south=41.886288445510516&west=12.483901977539062&north=41.893700240146295&east=12.500102519989014&filter=hasOutdoorSeats";
        String url2 = "http://giv-openpubguide.uni-muenster.de:8080/de.ifgi.ohbpgiosm/rest/pubs/getpubswithinbbox?south=41.886288445510516&west=12.483901977539062&north=41.893700240146295&east=12.500102519989014&filter=hasOutdoorSeats,hasFood";
        String url3 = "http://giv-openpubguide.uni-muenster.de:8080/de.ifgi.ohbpgiosm/rest/pubs/getpubswithinbbox?south=41.886288445510516&west=12.483901977539062&north=41.893700240146295&east=12.500102519989014&filter=hasOutdoorSeats,maximumBeerPrice=1.75";
        
        try {
            new URL(url1);
            new URL(url2);
            new URL(url3);
            assertTrue(true);
        } catch (MalformedURLException ex) {
            Logger.getLogger(URLValidation.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(false);
        }
    }
}

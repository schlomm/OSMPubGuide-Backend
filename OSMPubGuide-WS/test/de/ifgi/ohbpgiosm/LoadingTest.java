package de.ifgi.ohbpgiosm;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import noNamespace.OsmDocument;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;

public class LoadingTest {
        private String basePath = LoadingTest.class.getProtectionDomain().getCodeSource().getLocation().getFile()+"de/ifgi/ohbpgiosm/";
	private String path1 = basePath + "files/map.osm";
	private String path2 = basePath + "files/map.xml";
	private String path3 = basePath + "files/overpass.osm";

	@Test
	public void loadXML() {
                System.out.println(basePath);
		try {
			File f2 = new File(path2);
			OsmDocument d2 = OsmDocument.Factory.parse(f2);
			assertTrue(d2.validate());
		} catch (XmlException | IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	@Test
	public void loadOSM() {
		try {
			File f1 = new File(path1);
			OsmDocument d1 = OsmDocument.Factory.parse(f1);
			assertTrue(d1.validate());
		} catch (XmlException | IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void loadOverpass() {
		try {
			File file = new File(path3);
			OsmDocument doc = OsmDocument.Factory.parse(file);
			assertTrue(doc.validate());
		} catch (XmlException | IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}

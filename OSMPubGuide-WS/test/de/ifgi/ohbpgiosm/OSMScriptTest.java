package de.ifgi.ohbpgiosm;

import noNamespace.BboxQueryType;
import noNamespace.HasKvType;
import noNamespace.OsmScriptDocument;
import noNamespace.OsmScriptType;
import noNamespace.QueryOSMType;
import noNamespace.QueryType;
import org.junit.Test;

/**
 *
 * @author florian
 */
public class OSMScriptTest {
    
    @Test
    public void createOSMScript() {
        String south = "51.948920578035946";
        String north = "51.97335506954467";
        String west = "7.58803367614746";
        String east = "7.659530639648438";
        
        OsmScriptDocument osd = OsmScriptDocument.Factory.newInstance();
        OsmScriptType ost = osd.addNewOsmScript();
        QueryType qt = ost.addNewQuery();
        qt.setType(QueryOSMType.NODE);

        HasKvType hkt = qt.addNewHasKv();
        hkt.setK("amenity");
        hkt.setV("pub");

        BboxQueryType bbox = qt.addNewBboxQuery();
        bbox.setS(Float.parseFloat(south));
        bbox.setW(Float.parseFloat(west));
        bbox.setN(Float.parseFloat(north));
        bbox.setE(Float.parseFloat(east));
        
        ost.addNewPrint();
        
        System.out.println(osd.toString());
    }
}

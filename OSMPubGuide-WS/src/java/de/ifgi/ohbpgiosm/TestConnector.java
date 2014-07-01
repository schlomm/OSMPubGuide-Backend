/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import noNamespace.OsmDocument;

/**
 *
 * @author florian
 */
public class TestConnector extends Connector {

    private final int waitTime;
    
    @Override
    public void run() {
        //do stuff that takes time...
        for (int i = 0; i < this.waitTime; i++) { 
            //this test connector perform a loop to be busy...
        }

        //write a line that we are finished with this thread
        System.out.println("Finished Connector after "+this.waitTime+" ms");
        
        //the following is very important, it informs the ResponseMerger that the request is finished
        this.finish(); //declare that the observable was changed
        this.notifyObservers(); //inform the registered observers that a change occured (call the observers update function)
    }
    
    public TestConnector(int time) {
        this.waitTime = time;
    }

}

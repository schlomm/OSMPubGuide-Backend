/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author florian
 */
@javax.ws.rs.ApplicationPath("tosm")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(de.ifgi.ohbpgiosm.Hello.class);
        resources.add(de.ifgi.ohbpgiosm.Pubs.class);
        resources.add(de.ifgi.ohbpgiosm.QueryResource.class);
    }
    
}

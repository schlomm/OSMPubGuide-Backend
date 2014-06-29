/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm;

/**
 *
 * @author florian
 */
public interface Observer {
    /**
     * If the observed class calls this method then a certain function 
     * has to be applied. It basically follows the Observer pattern.
     * 
     */
    public void note();
}

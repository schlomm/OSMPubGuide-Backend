/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ifgi.ohbpgiosm.util;

/**
 *
 * @author florian
 */
public class BoundingBox {
    private final String SEPARATOR = ",";
    
    private float west;
    private float east;
    private float north;
    private float south;
    
    public BoundingBox(String bboxString) {
        String[] coordinate = bboxString.split(this.SEPARATOR);
        
        if (coordinate.length != 4) {
            //TODO: throw exception
        }
        
        this.west = Float.parseFloat(coordinate[0]);
        this.south = Float.parseFloat(coordinate[1]);
        this.east = Float.parseFloat(coordinate[2]);
        this.north = Float.parseFloat(coordinate[3]);
    }
    
    public float getWest() {
        return this.west;
    }
    
    public float getSouth() {
        return this.south;

    }
    
    public float getEast() {
        return this.east;
    }
    
    public float getNorth() {
        return this.north;
    }
    
    @Override
    public String toString() {
        return this.west+","+this.south+","+this.east+","+this.north;
    }
}

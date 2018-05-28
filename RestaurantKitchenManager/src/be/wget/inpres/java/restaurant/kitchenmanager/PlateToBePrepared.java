/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager;

/**
 *
 * @author wget
 */
public class PlateToBePrepared {
    
    private String plateCode;
    private int computedTime;
    
    public PlateToBePrepared(String plateCode, int computedTime) {
        this.plateCode = plateCode;
        this.computedTime = computedTime;
    }

    public String getPlateCode() {
        return plateCode;
    }

    public int getComputedTime() {
        return computedTime;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager;

import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @author wget
 */
class IngredientsEvent extends EventObject {
    
    private String plateCode;
    private ArrayList<String> ingredients;
    private int plateQuantity;
    
    public IngredientsEvent(
        Object source,
        String plateCode,
        ArrayList<String> ingredients,
        int plateQuantity) {
        super(source);
        this.plateCode = plateCode;
        this.ingredients = ingredients;
        this.plateQuantity = plateQuantity;
    }

    public String getPlateCode() {
        return plateCode;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public int getPlateQuantity() {
        return plateQuantity;
    }
}

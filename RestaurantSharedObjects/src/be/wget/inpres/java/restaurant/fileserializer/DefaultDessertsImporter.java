/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.fileserializer;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.dataobjects.PlateCategory;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wget
 */
public class DefaultDessertsImporter extends DefaultPlatesImporter {
    public DefaultDessertsImporter(
        RestaurantConfig applicationConfig,
        String filename
    ) {
        super(applicationConfig, PlateCategory.DESSERT, filename);
    }
    
    public ArrayList<Dessert> getDefaultPlates() {
        if (this.defaultPlates.isEmpty()) {
            this.parseFile();
        }
        ArrayList<Dessert> defaultMainCourses = new ArrayList<>();
        for (Plate plate: this.defaultPlates) {
            defaultMainCourses.add((Dessert)plate);
        }
        return defaultMainCourses;
    }
    
    public HashMap<String, Dessert> getDefaultPlatesHashMap() {
        if (this.defaultPlates.isEmpty()) {
            this.parseFile();
        }
        HashMap<String, Dessert> defaultMainCourses = new HashMap<>();
        for (Plate plate: this.defaultPlates) {
            defaultMainCourses.put(
                ((Dessert)plate).getCode(),
                (Dessert)plate);
        }
        return defaultMainCourses;
    }
}

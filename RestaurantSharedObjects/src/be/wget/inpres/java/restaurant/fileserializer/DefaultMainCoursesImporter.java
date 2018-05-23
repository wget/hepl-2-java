/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.fileserializer;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.dataobjects.PlateCategory;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wget
 */
public class DefaultMainCoursesImporter extends DefaultPlatesImporter {
    public DefaultMainCoursesImporter(
        RestaurantConfig applicationConfig,
        String filename
    ) {
        super(applicationConfig, PlateCategory.MAIN_COURSE, filename);
    }
    
    public ArrayList<MainCourse> getDefaultPlates() {
        if (this.defaultPlates.isEmpty()) {
            this.parseFile();
        }
        ArrayList<MainCourse> defaultMainCourses = new ArrayList<>();
        for (Plate plate: this.defaultPlates) {
            defaultMainCourses.add((MainCourse)plate);
        }
        return defaultMainCourses;
    }
    
    public HashMap<String, MainCourse> getDefaultPlatesHashMap() {
        if (this.defaultPlates.isEmpty()) {
            this.parseFile();
        }
        HashMap<String, MainCourse> defaultMainCourses = new HashMap<>();
        for (Plate plate: this.defaultPlates) {
            defaultMainCourses.put(
                ((MainCourse)plate).getCode(),
                (MainCourse)plate);
        }
        return defaultMainCourses;
    }
}

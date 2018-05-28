/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wget
 */
public class GetRecipeBean {
    
    RestaurantConfig applicationConfig;
    String plateCode;
    int quantity;
    HashMap<String, ArrayList<String>> ingredients;
    private HashMap<String, MainCourse> defaultMainCourses;
    private HashMap<String, Dessert> defaultDesserts;
    ArrayList<IngredientsListener> ingredientsListeners;
    
    public GetRecipeBean() {
        this.plateCode = "";
        this.quantity = 0;
        this.ingredients =  new HashMap<>();
        this.ingredientsListeners = new ArrayList<>();
    }
    
    public String getPlateCode() {
        return this.plateCode;
    }
    
    public void setPlateCode(String plateCode) {
        this.plateCode = plateCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setApplicationConfig(RestaurantConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        File f = new File(this.applicationConfig.getIngredientsFilename());
        
        // When application config set, load ingredients.
        
        // FIXME: If no default file, populate default random data.
        if (!f.exists()) {
            ArrayList<String> plateIngredients = new ArrayList<>();
            plateIngredients.add("viande");
            plateIngredients.add("citron");
            plateIngredients.add("graisse");
            this.ingredients.put("VRH", plateIngredients);
            plateIngredients = new ArrayList<>();
            plateIngredients.add("foo");
            plateIngredients.add("bar");
            plateIngredients.add("hello");
            this.ingredients.put("CC", plateIngredients);
            plateIngredients = new ArrayList<>();
            plateIngredients.add("lorem");
            plateIngredients.add("ipsum");
            plateIngredients.add("graisse");
            this.ingredients.put("FE", plateIngredients);
            plateIngredients = new ArrayList<>();
            plateIngredients.add("Sed");
            plateIngredients.add("ut");
            plateIngredients.add("perspiciatis");
            this.ingredients.put("GF", plateIngredients);
            plateIngredients = new ArrayList<>();
            plateIngredients.add("unde");
            plateIngredients.add("omnis");
            plateIngredients.add("iste");
            this.ingredients.put("PA", plateIngredients);

            try {
                FileOutputStream fos = new FileOutputStream(
                    this.applicationConfig.getTablesKitchenManagerFilename());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(this.ingredients);
                return;
            } catch (IOException ex) {}
        }
        
        try {
            FileInputStream fis = new FileInputStream(
                    this.applicationConfig.getIngredientsFilename());
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.ingredients = (HashMap)ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(GetRecipeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDefaultMainCourses(HashMap<String, MainCourse> defaultMainCourses) {
        this.defaultMainCourses = defaultMainCourses;
    }

    public void setDefaultDesserts(HashMap<String, Dessert> defaultDesserts) {
        this.defaultDesserts = defaultDesserts;
    }
    
    public void addIngredientsListener(IngredientsListener listener) {
        System.out.println("GetRecipeBean: addIngredientsListener called");
        if (!this.ingredientsListeners.contains(listener)) {
            System.out.println("GetRecipeBean: adding new listener");
            this.ingredientsListeners.add(listener);
        }
    }
    
    public void removeIngredientsListener(IngredientsListener listener) {
        if (!this.ingredientsListeners.contains(listener)) {
            this.ingredientsListeners.remove(listener);
        }
    }
    
    public void notifyIngredientsEvent() {
        ArrayList<String> currentPlateIngredients;
        try {
            currentPlateIngredients = this.ingredients.get(this.plateCode);
        } catch (Exception e) {
            currentPlateIngredients = new ArrayList<>();
            currentPlateIngredients.add("some");
            currentPlateIngredients.add("other");
            currentPlateIngredients.add("ingredient");
        }
        IngredientsEvent event = new IngredientsEvent(
            this,
            this.plateCode,
            currentPlateIngredients,
            this.quantity);
        for (IngredientsListener listener: this.ingredientsListeners) {
            System.out.println("GetRecipeBean: notifyIngredientsEvent");
            listener.ingredientsReceived(event);
        }
    }
}

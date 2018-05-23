/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.fileserializer;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.dataobjects.PlateCategory;
import be.wget.inpres.java.restaurant.myutils.StringSlicer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author wget
 */
public abstract class DefaultPlatesImporter {
    
    protected String filename;
    protected RestaurantConfig applicationConfig;
    protected ArrayList<Plate> defaultPlates;
    protected PlateCategory plateCategory;
    
    public DefaultPlatesImporter(
        RestaurantConfig applicationConfig,
        PlateCategory plateCategory,
        String filename) {
        this.applicationConfig = applicationConfig;
        this.filename = filename;
        this.plateCategory = plateCategory;
        this.defaultPlates = new ArrayList<>();
    }
    
    protected void parseFile() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = in.readLine()) != null) {
                ArrayList<String> plateFields = new StringSlicer(
                line,
                this.applicationConfig.getOrderFieldDelimiter())
                    .listComponents();
                Plate plate = null;
                if (this.plateCategory == PlateCategory.MAIN_COURSE) {
                    plate = new MainCourse(
                        plateFields.get(1),
                        Double.parseDouble(plateFields.get(2)),
                        plateFields.get(0));
                } else if (this.plateCategory == PlateCategory.DESSERT) {
                    plate = new Dessert(
                        plateFields.get(1),
                        Double.parseDouble(plateFields.get(2)),
                        plateFields.get(0));
                }
                
                this.defaultPlates.add(plate);
            }
        } catch (IOException ex) {
            this.populateDefaultPlates();
        }
    }
    
    protected void populateDefaultPlates() {
        if (!this.defaultPlates.isEmpty()) {
            return;
        }
            
        if (this.plateCategory == PlateCategory.MAIN_COURSE) {
            this.defaultPlates.add(
                new MainCourse("Veau au rollmops sauce Herve", 15.75, "VRH"));
            this.defaultPlates.add(
                new MainCourse("Cabillaud chantilly de Terre Neuve", 16.9, "CC"));
            this.defaultPlates.add(
                new MainCourse("Fillet de boeuf Enfer des papilles", 16.8, "FE"));
            this.defaultPlates.add(
                new MainCourse("Gruyère farci aux rognons-téquila", 13.4, "GF"));
            this.defaultPlates.add(
                new MainCourse("Potée auvergnate au miel", 12.5, "PA"));
            return;
        }
        
        if (this.plateCategory == PlateCategory.DESSERT) {
            this.defaultPlates.add(
                new Dessert("Mousse au chocolat salé", 5.35, "D_MC"));
            this.defaultPlates.add(
                new Dessert("Sorbet citron courgette Colonel", 6.85, "D_SC"));
            this.defaultPlates.add(
                new Dessert("Duo de crêpes Juliettes", 6, "D_CJ"));
            this.defaultPlates.add(
                new Dessert("Dame grise", 5.55, "D_DG"));
            this.defaultPlates.add(
                new Dessert("Crème très brulée carbonne", 7, "D_CB"));
            return;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.fileserializer;

import be.wget.inpres.java.restaurant.config.DefaultTables;
import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Table;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @author wget
 */
public class TablesSerializer {
    
    private RestaurantConfig applicationConfig;
    HashMap<String, Table> tables;
    
    public TablesSerializer(RestaurantConfig applicationConfig,
            HashMap<String, Table> tables) {
        this.applicationConfig = applicationConfig;
        this.tables = tables;
    }
    
    private void populateDefaultTables() {
        this.tables = new HashMap<>();
        HashMap<String, Table> defaultTables = new DefaultTables().getDefaultTables();
        for (String table: defaultTables.keySet()) {

            this.tables.put(table, defaultTables.get(table));
        }
    }
    
    public HashMap<String, Table> loadTables() {
        try {
            FileInputStream fis = new FileInputStream(
                this.applicationConfig.getTablesRoomManagerFilename());
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.tables = (HashMap<String, Table>)ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            this.populateDefaultTables();
        }
        return this.tables;
    }
    
    public void saveTables() throws IOException {
        FileOutputStream fos = new FileOutputStream(
        this.applicationConfig.getTablesRoomManagerFilename());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this.tables);
    }
}

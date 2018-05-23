/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.config;

import be.wget.inpres.java.restaurant.dataobjects.Table;
import java.util.HashMap;

/**
 *
 * @author wget
 */
public class DefaultTables {
    
    protected HashMap<String, Table> tables;
    
    public DefaultTables() {
        this.tables = new HashMap<>();
        this.tables.put("G1", new Table("G1", 4));
        this.tables.put("G2", new Table("G2", 4));
        this.tables.put("G3", new Table("G3", 4));
        this.tables.put("C11", new Table("C11", 4));
        this.tables.put("C12", new Table("C12", 6));
        this.tables.put("C13", new Table("C13", 4));
        this.tables.put("C21", new Table("C21", 6));
        this.tables.put("C22", new Table("C22", 6));
        this.tables.put("D1", new Table("D1", 4));
        this.tables.put("D2", new Table("D2", 2));
        this.tables.put("D3", new Table("D3", 2));
        this.tables.put("D3", new Table("D4", 2));
        this.tables.put("D5", new Table("D5", 2));
    }
    

    public HashMap<String, Table> getDefaultTables() {
        return this.tables;
    }
}

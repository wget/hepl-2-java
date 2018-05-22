/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.config;

import java.awt.Container;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author wget
 */
public class RestaurantConfig {
    final private String settingsFilename = "settings.conf";
    final private String defaultApplicationName = "Le gourmet audacieux";
    final private String defaultServerAddress = "localhost";
    final private int defaultServerPort = 55555;
    final private String orderLineDelimiter = "#";
    final private String orderFieldDelimiter = "&";
    final private String kitchenOrderAcceptedPayload = "ORDER_ACCEPTED";
    final private String kitchenOrderDeclinedPayload = "ORDER_REFUSED";
    
    private Properties restaurantConfig;
    private boolean configFileDefined = false;

    public RestaurantConfig() {
        this.restaurantConfig = new Properties();
	InputStream input = null;
	try {
            input = new FileInputStream(this.settingsFilename);
            this.restaurantConfig.load(input);
            this.configFileDefined = true;
	} catch (IOException ex) {}
    }

    public String getSettingsFilename() {
        return this.settingsFilename;
    }
    
    public boolean isConfigFileDefined() {
        return this.configFileDefined;
    }
    
    public String getRestaurantName() {
        String applicationName = this.restaurantConfig.getProperty("RestaurantName");
        if (applicationName == null || applicationName.isEmpty()) {
            return this.defaultApplicationName;
        }
        return applicationName;
    }
    
    public String getServerAddress() {
        String address = this.restaurantConfig.getProperty("ServerAddress");
        if (address == null || address.isEmpty()) {
            return this.defaultServerAddress;
        }
        return address;
    }
    
    public int getServerPort() {
        String port = this.restaurantConfig.getProperty("ServerPort");
        int parsedPort;
        if (port == null || port.isEmpty()) {
            return this.defaultServerPort;
        }
        try {
            parsedPort = Integer.parseInt(port);
        } catch (Exception e) {
            return this.defaultServerPort;
        }
        return parsedPort;
    }
    
    public String getOrderLineDelimiter() {
        String delimiter = this.restaurantConfig.getProperty("OrderLineDelimiter");
        if (delimiter == null || delimiter.isEmpty()) {
            return this.orderLineDelimiter;
        }
        return delimiter;
    }
    
    public String getOrderFieldDelimiter() {
        String delimiter = this.restaurantConfig.getProperty("OrderFieldDelimiter");
        if (delimiter == null || delimiter.isEmpty()) {
            return this.orderFieldDelimiter;
        }
        return delimiter;
    }
    
    public String getKitchenOrderAcceptedPayload() {
        String payload = this.restaurantConfig.getProperty("OrderAcceptedPayload");
        if (payload == null || payload.isEmpty()) {
            return this.kitchenOrderAcceptedPayload;
        }
        return payload;
    }
    
    public String getKitchenOrderDeclinedPayload() {
        String payload = this.restaurantConfig.getProperty("OrderDeclinedPayload");
        if (payload == null || payload.isEmpty()) {
            return this.kitchenOrderDeclinedPayload;
        }
        return payload;
    }
}

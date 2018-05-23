/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.orderprotocol;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import be.wget.inpres.java.restaurant.dataobjects.Table;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 *
 * @author wget
 */
public class NetworkProtocolEncoder {
    
    protected RestaurantConfig applicationConfig;
    protected ArrayList<PlateOrder> ordersToSend;
    protected Table currentTable;
     
    public NetworkProtocolEncoder(    
        RestaurantConfig applicationConfig,
        Table currentTable,
        ArrayList<PlateOrder> ordersToSend) {
        this.applicationConfig = applicationConfig;
        this.currentTable = currentTable;
        this.ordersToSend = ordersToSend;
    }

    public String encodeRequest() {
        StringBuilder ordersToServer = new StringBuilder();
        // Always force date to be specified as ISO8601
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String orderTime = df.format(new Date());
        for (PlateOrder order: this.ordersToSend) {
            ordersToServer.append(order.getQuantity());
            ordersToServer.append(this.applicationConfig.getOrderFieldDelimiter());
            if (order.getPlate() instanceof MainCourse) {
                ordersToServer.append(((MainCourse)order.getPlate()).getCode());
            } else {
                ordersToServer.append(((Dessert)order.getPlate()).getCode());
            }
            ordersToServer.append(this.applicationConfig.getOrderFieldDelimiter());
            ordersToServer.append(this.currentTable.getNumber());
            ordersToServer.append(this.applicationConfig.getOrderFieldDelimiter());
            ordersToServer.append(orderTime);
            ordersToServer.append(this.applicationConfig.getOrderFieldDelimiter());
            ordersToServer.append(order.getComment());
            ordersToServer.append(this.applicationConfig.getOrderLineDelimiter());
        }
        return ordersToServer.toString();
    }
}

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
import java.util.ArrayList;

/**
 *
 * @author wget
 */
public class NetworkProtocolServeNotifySender {
    protected RestaurantConfig applicationConfig;
    protected ArrayList<PlateOrder> ordersToNotify;

    public NetworkProtocolServeNotifySender(
        RestaurantConfig applicationConfig,
        ArrayList<PlateOrder> ordersToNotify) {
        this.applicationConfig = applicationConfig;
        this.ordersToNotify = ordersToNotify;
    }

    public String encodeRequest() {
        StringBuilder ordersToNotify = new StringBuilder();
        for (PlateOrder order: this.ordersToNotify) {
            ordersToNotify.append(NetworkProtocolRequestType.SERVE_NOTIFY);
            ordersToNotify.append(this.applicationConfig.getOrderFieldDelimiter());
            ordersToNotify.append(order.getQuantity());
            ordersToNotify.append(this.applicationConfig.getOrderFieldDelimiter());
            if (order.getPlate() instanceof MainCourse) {
                ordersToNotify.append(((MainCourse)order.getPlate()).getCode());
            } else {
                ordersToNotify.append(((Dessert)order.getPlate()).getCode());
            }
            ordersToNotify.append(this.applicationConfig.getOrderLineDelimiter());
        }
        return ordersToNotify.toString();
    }
}

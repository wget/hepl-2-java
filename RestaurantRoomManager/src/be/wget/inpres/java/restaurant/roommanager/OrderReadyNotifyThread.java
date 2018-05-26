/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.roommanager;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import be.wget.inpres.java.restaurant.orderprotocol.NetworkProtocolMalformedFieldException;
import be.wget.inpres.java.restaurant.orderprotocol.NetworkProtocolServeNotifyReceiver;
import be.wget.inpres.java.restaurant.orderprotocol.NetworkProtocolUnexpectedFieldException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import network.NetworkBasicServer;

/**
 *
 * @author wget
 */
public class OrderReadyNotifyThread extends Thread {
    
    private RestaurantConfig applicationConfig;
    private ArrayList<MainCourse> defaultMainCourses;
    private ArrayList<Dessert> defaultDesserts;
    private NetworkBasicServer networkReceiver;
    private ArrayList<PlateOrder> ordersReady;
    private ArrayList<PlateOrder> newOrdersReady;
    private JCheckBox ordersReadyCheckbox;
    private OrderReadyNotifyThreadMonitor orderReadyNotifyThreadMonitor;
    private boolean continueExecution;
    
    public OrderReadyNotifyThread(
            RestaurantConfig applicationConfig,
            ArrayList<MainCourse> defaultMainCourses,
            ArrayList<Dessert> defaultDesserts,
            NetworkBasicServer networkReceiver,
            ArrayList<PlateOrder> ordersReady,
            JCheckBox ordersReadyCheckbox) {
        this.applicationConfig = applicationConfig;
        this.defaultMainCourses = defaultMainCourses;
        this.defaultDesserts = defaultDesserts;
        this.networkReceiver = networkReceiver;
        this.ordersReady = ordersReady;
        this.ordersReadyCheckbox = ordersReadyCheckbox;

        this.newOrdersReady = new ArrayList<>();
        this.orderReadyNotifyThreadMonitor = new OrderReadyNotifyThreadMonitor(
            this.ordersReady,
            this.newOrdersReady);
        this.continueExecution = true;
    }
    
    public void run() {
        while (continueExecution) {
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(OrderReadyNotifyThread.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            String requestReceived = this.networkReceiver.getMessage();
            if (requestReceived.isEmpty() ||
                requestReceived.equals(applicationConfig.getNetworkNoValue())) {
                continue;
            }
            
            NetworkProtocolServeNotifyReceiver parser =
                new NetworkProtocolServeNotifyReceiver(
                    this.applicationConfig,
                    this.defaultMainCourses,
                    this.defaultDesserts,
                    requestReceived);

            try {
                this.newOrdersReady = parser.getOrders();
            } catch (NetworkProtocolUnexpectedFieldException |
                     NetworkProtocolMalformedFieldException ex) {
                continue;
            }
            
            // Update model
            this.orderReadyNotifyThreadMonitor.addNewOrdersReady(
                this.newOrdersReady);
            
            // Update UI
            this.ordersReadyCheckbox.setSelected(true);
        }
    }
    
    public void stopExecution() {
        this.continueExecution = false;
    }
    
    public void clearOrdersReady() {
        this.orderReadyNotifyThreadMonitor.clearOrdersReady();
    }
    
    public ArrayList<PlateOrder> getOrdersReady() {
        return this.orderReadyNotifyThreadMonitor.getOrdersReady();
    }
}
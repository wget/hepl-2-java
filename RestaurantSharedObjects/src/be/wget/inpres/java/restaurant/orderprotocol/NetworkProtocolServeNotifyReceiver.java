/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.orderprotocol;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import be.wget.inpres.java.restaurant.dataobjects.Table;
import be.wget.inpres.java.restaurant.myutils.StringSlicer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wget
 */
public class NetworkProtocolServeNotifyReceiver {
    protected RestaurantConfig applicationConfig;
    protected HashMap<String, Table> defaultTables;
    protected HashMap<String, MainCourse> defaultMainCourses;
    protected HashMap<String, Dessert> defaultDesserts;
    protected String request;
    protected ArrayList<PlateOrder> orders;

    public NetworkProtocolServeNotifyReceiver(
        RestaurantConfig applicationConfig,
        ArrayList<MainCourse> mainCourses,
        ArrayList<Dessert> desserts,
        String request) {
        this.applicationConfig = new RestaurantConfig();

        this.defaultMainCourses = new HashMap<>();
        this.defaultDesserts = new HashMap<>();

        for (MainCourse mainCourse: mainCourses) {
            this.defaultMainCourses.put(mainCourse.getCode(), mainCourse);
        }

        for (Dessert dessert: desserts) {
            this.defaultDesserts.put(dessert.getCode(), dessert);
        }

        this.request = request;
        this.orders = new ArrayList<>();
    }

    protected void parseRequest()
        throws NetworkProtocolUnexpectedFieldException,
               NetworkProtocolMalformedFieldException {
        StringSlicer orderLines = new StringSlicer(
            request,
            this.applicationConfig.getOrderLineDelimiter());
        ArrayList<String> orderFields;
        for (String orderLine: orderLines.listComponents()) {
            orderFields = new StringSlicer(
                orderLine,
                this.applicationConfig.getOrderFieldDelimiter())
                    .listComponents();

            try {
                String requestType = orderFields
                    .get(NetworkProtocolServeNotifyFields.REQUEST_TYPE);
                if (Integer.parseInt(requestType) !=
                    NetworkProtocolRequestType.SERVE_NOTIFY) {
                    throw new NetworkProtocolMalformedFieldException();
                }
            } catch (Exception e) {
                throw new NetworkProtocolMalformedFieldException();
            }

            // Add orders details
            String plateCode = orderFields.get(
                NetworkProtocolServeNotifyFields.PLATE_CODE);
            Plate plate;
            // This is a main course
            if (this.defaultMainCourses.get(plateCode) != null) {
                plate = this.defaultMainCourses.get(plateCode);
            // This is a dessert
            } else if (this.defaultDesserts.get(plateCode) != null) {
                plate = this.defaultDesserts.get(plateCode);
            } else {
                throw new NetworkProtocolUnexpectedFieldException();
            }

            PlateOrder order = new PlateOrder(
                plate,
                Integer.parseInt(orderFields.get(
                    NetworkProtocolServeNotifyFields.QUANTITY)));

            // Add order to orders list
            this.orders.add(order);
        }
    }

    public ArrayList<PlateOrder> getOrders()
        throws NetworkProtocolUnexpectedFieldException,
               NetworkProtocolMalformedFieldException {
        if (orders.isEmpty()) {
            this.parseRequest();
        }
        return orders;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.orderprotocol;

import be.wget.inpres.java.restaurant.config.DefaultTables;
import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import be.wget.inpres.java.restaurant.dataobjects.Table;
import be.wget.inpres.java.restaurant.myutils.StringSlicer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author wget
 */
public class NetworkProtocolDecoder {
    
    protected RestaurantConfig applicationConfig;
    protected HashMap<String, Table> defaultTables;
    protected HashMap<String, MainCourse> defaultMainCourses;
    protected HashMap<String, Dessert> defaultDesserts;
    protected String request;
    
    protected ArrayList<Table> tables;
    protected ArrayList<PlateOrder> orders;
    
    public NetworkProtocolDecoder(
        RestaurantConfig applicationConfig,
        HashMap<String, MainCourse> mainCourses,
        HashMap<String, Dessert> desserts,
        String request) {
        this.applicationConfig = new RestaurantConfig();
        this.defaultTables = new DefaultTables().getDefaultTables();
        this.defaultMainCourses = mainCourses;
        this.defaultDesserts = desserts;
        this.request = request;
        this.tables = new ArrayList<>();
        this.orders = new ArrayList<>();
    }
    
    protected void parseRequest()
        throws NetworkOrderProtocolUnexpectedFieldException,
               NetworkOrderProtocolMalformedFieldException {
        StringSlicer orderLines = new StringSlicer(
            request,
            this.applicationConfig.getOrderLineDelimiter());
        ArrayList<String> orderFields;
        for (String orderLine: orderLines.listComponents()) {
            orderFields = new StringSlicer(
                orderLine,
                this.applicationConfig.getOrderFieldDelimiter())
                    .listComponents();
            System.out.println("DEBUG fields : " + orderFields);

            // Add orders details
            String plateCode = orderFields.get(NetworkOrderProtocolFields.PLATE_CODE);
            Plate plate;
            // This is a main course
            if (this.defaultMainCourses.get(plateCode) != null) {
                plate = this.defaultMainCourses.get(plateCode);
            // This is a dessert
            } else if (this.defaultDesserts.get(plateCode) != null) {
                plate = this.defaultDesserts.get(plateCode);
            } else {
                throw new NetworkOrderProtocolUnexpectedFieldException();
            }
            
            PlateOrder order = new PlateOrder(
                plate,
                Integer.parseInt(
                    orderFields.get(NetworkOrderProtocolFields.QUANTITY)));

            try {
                order.setComment(orderFields.get(
                    NetworkOrderProtocolFields.ORDER_COMMENT));
            } catch (IndexOutOfBoundsException e) {}

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH);
            try {
                order.setOrderDate(df.parse(orderFields.get(NetworkOrderProtocolFields.ARRIVAL_TIME)));
            } catch (ParseException ex) {
                throw new NetworkOrderProtocolMalformedFieldException();
            }
         
            // Add order to table
            String tableNumber = orderFields.get(
                NetworkOrderProtocolFields.TABLE_NUMBER);
            Table tableToAdd = null;
            for (Table table: this.tables) {
                if (table.getNumber().equals(tableNumber)) {
                    tableToAdd = table;
                }
            }
            if (tableToAdd == null) {
                tableToAdd = new Table(
                    tableNumber,
                    defaultTables.get(tableNumber).getMaxCovers());
                this.tables.add(tableToAdd);
            }
            tableToAdd.addOrder(order);
            
            // Add order to orders list
            this.orders.add(order);
        }
    }

    public ArrayList<PlateOrder> getOrders()
        throws NetworkOrderProtocolUnexpectedFieldException,
               NetworkOrderProtocolMalformedFieldException {
        if (orders.isEmpty()) {
            this.parseRequest();
        }
        return orders;
    }

    public ArrayList<Table> getTables() 
        throws NetworkOrderProtocolUnexpectedFieldException,
               NetworkOrderProtocolMalformedFieldException {
        if (tables.isEmpty()) {
            this.parseRequest();            
        }
        return tables;
    }
}

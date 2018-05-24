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
public class NetworkProtocolRequestTypeInference {
    
    protected RestaurantConfig applicationConfig;
    protected String request;
    protected int requestType;
    
    public NetworkProtocolRequestTypeInference(
        RestaurantConfig applicationConfig,
        HashMap<String, MainCourse> mainCourses,
        HashMap<String, Dessert> desserts,
        String request) {
        this.applicationConfig = new RestaurantConfig();
        this.request = request;
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
                int requestType = Integer.parseInt(orderFields
                    .get(NetworkProtocolServeNotifyFields.REQUEST_TYPE));

                switch (requestType) {
                    case NetworkProtocolRequestType.ORDER:
                        this.requestType = NetworkProtocolRequestType.ORDER;
                        break;
                    case NetworkProtocolRequestType.SERVE_NOTIFY:
                        this.requestType = NetworkProtocolRequestType.SERVE_NOTIFY;
                        break;
                    default:
                        this.requestType = -1;
                        throw new NetworkProtocolMalformedFieldException();
                }
            } catch (Exception e) {
                throw new NetworkProtocolMalformedFieldException();
            }
            
            // We are just taking the first item of the request to determine
            // its type. We are (over?)simplying and considering the network
            // request can only contain lines with the same network type.
            break;
        }
    }

    public int getNetworkRequestType() {
        return this.requestType;
    }
}

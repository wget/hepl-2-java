/*
 * Copyright (C) 2018 wget
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package be.wget.inpres.java.restaurant.roommanager;

import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import java.util.ArrayList;

/**
 *
 * @author wget
 */
public class OrderReadyNotifyThreadMonitor {
    private ArrayList<PlateOrder> ordersReady;
    private ArrayList<PlateOrder> newOrdersReady;

    public OrderReadyNotifyThreadMonitor(
            ArrayList<PlateOrder> ordersReady,
            ArrayList<PlateOrder> newOrdersReady) {
        this.ordersReady = ordersReady;
        this.newOrdersReady = newOrdersReady;
    };
    
    public synchronized void addNewOrdersReady(
        ArrayList<PlateOrder> newOrdersReady) {
        this.ordersReady.addAll(newOrdersReady);
    }
    
    public synchronized ArrayList<PlateOrder> getOrdersReady() {
        return this.ordersReady;
    }
    
    public synchronized void clearOrdersReady() {
        this.ordersReady.clear();
        this.newOrdersReady.clear();
    }
}

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
package be.wget.inpres.java.restaurant.dataobjects;

import java.util.ArrayList;

/**
 *
 * @author wget
 */
public class Table {
    // The table number can contain alphabetic chars.
    protected String number;
    protected ArrayList<PlateOrder> orders;
    protected int maxCovers;
    protected int effectiveCovers;
    protected double billAmount;
    protected boolean billPaid;
    protected String waiterName;

    public Table(String number, int maxCovers) {
        this.number = number;
        this.maxCovers = maxCovers;
        this.orders = new ArrayList<>();
        this.billPaid = false;
    }   
    
    public void addOrder(PlateOrder order) {
        this.orders.add(order);
    }

    public void removeOrder(int position) {
        this.orders.remove(position);
    }

    public void setEffectiveCovers(int covers) {
        if (covers < 1) {
            this.effectiveCovers = 1;
            return;
        }
        this.effectiveCovers = covers;
    }

    public void setBillAmount(double amount) {
        this.billAmount = amount;
    }

    public void setBillPaid() {
        this.billPaid = true;
    }
    
    public void setWaiterName(String name) {
        this.waiterName = name;
    }

    public String getNumber() {
        return number;
    }

    public ArrayList<PlateOrder> getOrders() {
        return orders;
    }

    public int getMaxCovers() {
        return maxCovers;
    }

    public int getEffectiveCovers() {
        return effectiveCovers;
    }

    public double getBillAmount() {
        return billAmount;
    }
    
    public boolean isBillPaid() {
        return this.billPaid;
    }

    public String getWaiterName() {
        return waiterName;
    }
}

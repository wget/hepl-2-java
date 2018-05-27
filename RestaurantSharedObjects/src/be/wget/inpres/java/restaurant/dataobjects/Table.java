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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author wget
 */
public class Table implements Serializable {
    // The table number can contain alphabetic chars.
    protected String number;
    protected ArrayList<PlateOrder> orders;
    protected int maxCovers;
    protected int effectiveCovers;
    protected boolean billPaid;
    protected String waiterName;
    protected BigDecimal drinkAmount;

    public Table(String number, int maxCovers) {
        this.number = number;
        this.maxCovers = maxCovers;
        this.orders = new ArrayList<>();
        this.billPaid = false;
        this.effectiveCovers = 0;
        this.drinkAmount = new BigDecimal(0);
    }   
    
    public void addOrder(PlateOrder orderToAdd) {
        PlateOrder orderFound = null;
        for (PlateOrder order: this.orders) {
            if (order.isSent()) {
                continue;
            }
            if (order.getPlate() instanceof MainCourse &&
                orderToAdd.getPlate() instanceof MainCourse) {
                if (((MainCourse)order.getPlate()).getCode().equals(
                    ((MainCourse)orderToAdd.getPlate()).getCode())) {
                    orderFound = order;
                    break;
                }
            } else if (order.getPlate() instanceof Dessert &&
                       orderToAdd.getPlate() instanceof Dessert) {
                if (((Dessert)order.getPlate()).getCode().equals(
                    ((Dessert)orderToAdd.getPlate()).getCode())) {
                    orderFound = order;
                    break;
                }
            }
        }
        
        if (orderToAdd.getPlate() instanceof MainCourse) {
            this.effectiveCovers += orderToAdd.getQuantity();
        }
        
        if (orderFound == null) {
            this.orders.add(orderToAdd);
            return;
        }
            
        orderFound.addQuantity(orderToAdd.getQuantity());
        orderFound.setComment(orderToAdd.getComment());
    }

    public void removeOrder(PlateOrder orderToRemove) {
        String plateCodeToRemove;
        if (orderToRemove.getPlate() instanceof MainCourse) {
            plateCodeToRemove = ((MainCourse)orderToRemove.getPlate()).getCode();
        } else {
            plateCodeToRemove = ((Dessert)orderToRemove.getPlate()).getCode();
        }
        
        for (int i = 0; i < this.orders.size(); i++) {

            PlateOrder order = this.orders.get(i);
            
            String plateCode;
            if (order.getPlate() instanceof MainCourse) {
                plateCode = ((MainCourse)order.getPlate()).getCode();
            } else {
                plateCode = ((Dessert)order.getPlate()).getCode();
            }
            
            if (plateCode.equals(plateCodeToRemove)) {
                if (order.getQuantity() - orderToRemove.getQuantity() <= 0) {
                    this.orders.remove(i);
                } else {
                    order.setQuantity(order.getQuantity() - orderToRemove.getQuantity());
                    this.orders.set(i, order);
                }
                
                if (order.getPlate() instanceof MainCourse) {
                    if (order.getQuantity() - orderToRemove.getQuantity() < 0) {
                        this.effectiveCovers = 0;
                        System.out.println("Effective cover less 1:" + this.effectiveCovers);
                    } else {
                        this.effectiveCovers -= orderToRemove.getQuantity();
                        System.out.println("Effective cover less 2:" + this.effectiveCovers);
                    }
                }
            }
        }
    }

    public void setEffectiveCovers(int covers) {
        if (covers < 1) {
            this.effectiveCovers = 1;
            return;
        }
        this.effectiveCovers = covers;
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

    public BigDecimal getBillAmount() {
        BigDecimal billAmount = new BigDecimal(0);
        for (PlateOrder order: this.orders) {
            billAmount = billAmount.add(order.getPrice());
        }
        billAmount = billAmount.add(this.drinkAmount);
        System.out.println("DEBUG from table class: "+billAmount.toString());
        return billAmount;
    }
    
    public void setDrinkAmount(BigDecimal amount) {
        this.drinkAmount = amount;
    }
    
    public BigDecimal getDrinkAmount() {
        return this.drinkAmount;
    }
    
    public boolean isBillPaid() {
        return this.billPaid;
    }

    public String getWaiterName() {
        return waiterName;
    }
    
    public void bulkCompress() {
        ArrayList<PlateOrder> cleanedOrders = new ArrayList<>();
        for (int i = 0; i < this.orders.size(); i++) {
            
            PlateOrder order = this.orders.get(i);
            String plateCode = null;
            if (order.getPlate() instanceof MainCourse) {
                plateCode = ((MainCourse)order.getPlate()).getCode();
            } else if (order.getPlate() instanceof Dessert) {
                plateCode = ((Dessert)order.getPlate()).getCode();
            }
            
            int j = 0;
            for (; j < cleanedOrders.size(); j++) {
                PlateOrder orderCleaned = cleanedOrders.get(j);
                String plateCodeCleanOrder = null;
                if (orderCleaned.getPlate() instanceof MainCourse) {
                    plateCodeCleanOrder = ((MainCourse)orderCleaned.getPlate()).getCode();
                } else if (orderCleaned.getPlate() instanceof Dessert) {
                    plateCodeCleanOrder = ((Dessert)orderCleaned.getPlate()).getCode();
                }
                if (plateCodeCleanOrder.equals(plateCode)) {
                    break;
                }
            }
            // If not found
            if (j == cleanedOrders.size()) {
                cleanedOrders.add(order);
                continue;
            }
            
            // We have an occurrence with the same plate code, gonna check if
            // we need to merge them depending if they have been sent or not.
            PlateOrder orderCleaned = cleanedOrders.get(j);
            if (orderCleaned.isSent() == order.isSent()) {
                orderCleaned.addQuantity(order.getQuantity());
                orderCleaned.setComment(order.getComment());
            }
        }
        this.orders = cleanedOrders;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager.guis;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author wget
 */
public class PlatesReceivedTableModel extends DefaultTableModel {
    Class[] types = new Class [] {
        Integer.class,
        String.class,
        String.class,
        String.class
    };

    public PlatesReceivedTableModel() {
        super(new Object [][] {

        },
        new String [] {
            "Quantity", "Plate", "Table", "Arrival time"
        });
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}

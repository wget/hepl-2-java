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
class PlatesBeingPreparedTableModel extends DefaultTableModel {

    private HashMap<ArrayList, Boolean> cellsEditableState;
    Class[] types = new Class [] {
        Integer.class,
        String.class,
        String.class,
        String.class,
        Boolean.class,
        Boolean.class,
        Boolean.class
    };

    public PlatesBeingPreparedTableModel() {
        super(new Object [][] {

        },
        new String [] {
            "Quantity", "Plate", "Table", "Arrival time",
            "Being prepared", "Ready to serve", "Served"
        });
        this.cellsEditableState = new HashMap<>();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
    }

    public void setCellEditable(int row, int column, boolean state) {
        System.out.println("Setting cell editabl state for row: " + row + ", column: " + column + ", value: " + state);
        ArrayList<Integer> key = new ArrayList<>();
        key.add(row);
        key.add(column);
        this.cellsEditableState.put(key, state);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        ArrayList<Integer> key = new ArrayList<>();
        key.add(row);
        key.add(column);
        System.out.println("Requesting isCellEditable for row: " + row + ", column: " + column + ", value: " + this.cellsEditableState.get(key));
        return this.cellsEditableState.get(key);
    }
}
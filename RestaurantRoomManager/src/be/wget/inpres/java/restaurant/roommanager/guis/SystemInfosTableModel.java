/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.roommanager.guis;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author wget
 */
public class SystemInfosTableModel extends DefaultTableModel {
    Class[] types = new Class [] {
        String.class,
        String.class
    };

    public SystemInfosTableModel() {
        super(new Object [][] {

        },
        new String [] {
            "Property", "Value"
        });
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}

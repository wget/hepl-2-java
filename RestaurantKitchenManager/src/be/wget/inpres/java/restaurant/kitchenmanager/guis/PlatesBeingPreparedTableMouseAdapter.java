/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager.guis;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author wget
 */
class PlatesBeingPreparedTableMouseAdapter extends MouseAdapter {
        
    private JTable table;
    private JFrame frame;
    private RestaurantConfig config;
    private HashMap<ArrayList, Boolean> cellsAlreadyClicked;

    public PlatesBeingPreparedTableMouseAdapter(
        RestaurantConfig config,
        JFrame frame,
        JTable table) {
        this.config = config;
        this.frame = frame;
        this.table = table;
        this.cellsAlreadyClicked = new HashMap<>();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getSource() != this.table) {
            return;
        }

        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());

        if (column < PlatesBeingPreparedTableColumns.BEING_PREPARED ||
            column > PlatesBeingPreparedTableColumns.SERVED) {
            return;
        }

        ArrayList<Integer> coordinates = new ArrayList<>();
        coordinates.add(row);
        coordinates.add(column - 1);

        boolean isPreviousCellChecked = false;
        if (column == PlatesBeingPreparedTableColumns.BEING_PREPARED) {
            isPreviousCellChecked = true;
        } else {
            isPreviousCellChecked = this.cellsAlreadyClicked.get(coordinates) != null;
        }

        coordinates = new ArrayList<>();
        coordinates.add(row);
        coordinates.add(column);
        boolean isCellChecked = true;

        if (this.cellsAlreadyClicked.get(coordinates) == null) {
            if (isPreviousCellChecked) {
                this.cellsAlreadyClicked.put(coordinates, true);
            }
            isCellChecked = false;
        }

        if (column == PlatesBeingPreparedTableColumns.READY_TO_SERVE
            && !isPreviousCellChecked) {
            JOptionPane.showMessageDialog(
                frame,
                "You first need to prepare the order before setting it as ready to serve.",
                this.config.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (column == PlatesBeingPreparedTableColumns.SERVED &&
            !isPreviousCellChecked) {
            JOptionPane.showMessageDialog(
                frame,
                "You first need to have the order ready to serve before setting it served.",
                this.config.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isCellChecked) {
            switch (column) {
                case PlatesBeingPreparedTableColumns.BEING_PREPARED:
                    JOptionPane.showMessageDialog(
                        frame,
                        "An order being prepared cannot be cancelled.",
                        this.config.getRestaurantName(),
                        JOptionPane.WARNING_MESSAGE);
                    break;
                case PlatesBeingPreparedTableColumns.READY_TO_SERVE:
                    JOptionPane.showMessageDialog(
                        frame,
                        "An order ready to serve must be served.",
                        this.config.getRestaurantName(),
                        JOptionPane.WARNING_MESSAGE);
                    break;
                case PlatesBeingPreparedTableColumns.SERVED:
                    JOptionPane.showMessageDialog(
                        frame,
                        "An order served cannot be edited.",
                        this.config.getRestaurantName(),
                        JOptionPane.WARNING_MESSAGE);
                    break;
                default:
                    break;
            }
        }
    }
}
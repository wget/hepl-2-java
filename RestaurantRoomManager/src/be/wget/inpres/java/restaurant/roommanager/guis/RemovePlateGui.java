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
package be.wget.inpres.java.restaurant.roommanager.guis;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.fileserializer.DefaultMainCoursesSerializer;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author wget
 */
@SuppressWarnings("serial")
public class RemovePlateGui extends javax.swing.JDialog implements KeyListener {

    RestaurantConfig applicationConfig;
    ArrayList<MainCourse> defaultMainCourses;
    
    /**
     * Creates new form AboutGui
     */
    public RemovePlateGui(
            Frame parent,
            RestaurantConfig applicationConfig,
            ArrayList<MainCourse> defaultMainCourses,
            String currency) {
        super(parent, true);
        initComponents();
        this.setTitle("List plates (main courses)");
        this.platesList.setModel(new DefaultListModel<>());
        this.platesList.addKeyListener(this);
        this.applicationConfig = applicationConfig;
        this.defaultMainCourses = defaultMainCourses;

        DefaultListModel<String> dlm =
            (DefaultListModel<String>)this.platesList.getModel();
        for (int i = 0; i < defaultMainCourses.size(); i++) {
            StringBuilder tableLine = new StringBuilder();
            Plate plate = defaultMainCourses.get(i);
            if (plate instanceof MainCourse) {
                tableLine.append(((MainCourse)plate).getCode());
            } else {
                continue;
            }
            tableLine.append(": ");
            tableLine.append(plate.getLabel());
            tableLine.append(" (");
            tableLine.append(plate.getPrice());
            tableLine.append(" ");
            tableLine.append(currency);
            tableLine.append(")");
            dlm.addElement(tableLine.toString());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        removePlateButton = new javax.swing.JButton();
        removePlateLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        platesList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        removePlateButton.setText("Remove selected plate");
        removePlateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePlateButtonActionPerformed(evt);
            }
        });

        removePlateLabel.setText("Select the plate you want to remove from the application:");

        jScrollPane1.setViewportView(platesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removePlateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(removePlateButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(removePlateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(removePlateButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void removePlateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePlateButtonActionPerformed
        this.removeSelectedPlate();
        this.setVisible(false);
    }//GEN-LAST:event_removePlateButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> platesList;
    private javax.swing.JButton removePlateButton;
    private javax.swing.JLabel removePlateLabel;
    // End of variables declaration//GEN-END:variables

    private void removeSelectedPlate() {
        
        if (this.platesList.getSelectedIndex() == -1) {
            return;
        }

        this.defaultMainCourses.remove(this.platesList.getSelectedIndex());
        
        // Populate list of plates
        ArrayList<Plate> plates = new ArrayList<>();
        for (int i = 0; i < this.defaultMainCourses.size(); i++) {
            plates.add(this.defaultMainCourses.get(i));
        }
        DefaultMainCoursesSerializer mainCoursesSerializer =
            new DefaultMainCoursesSerializer(
                this.applicationConfig,
                plates);
        try {
            mainCoursesSerializer.saveFile();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "An issue occurred when trying to save the file with the main courses",
                "Serialization impossible",
                JOptionPane.ERROR_MESSAGE); 
        }
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            this.removeSelectedPlate();
            this.setVisible(false);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
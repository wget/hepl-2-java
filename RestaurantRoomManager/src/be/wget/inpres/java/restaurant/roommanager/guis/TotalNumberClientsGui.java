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

import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import be.wget.inpres.java.restaurant.dataobjects.Table;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.swing.JDialog;

/**
 *
 * @author wget
 */
@SuppressWarnings("serial")
public class TotalNumberClientsGui extends JDialog implements KeyListener {

    /**
     * Creates new form AboutGui
     */
    public TotalNumberClientsGui(Frame parent, HashMap<String, Table> tables) {
        super(parent, true);
        initComponents();
        this.setTitle("Total number of clients");
        
        this.totalNumberClientsTextField.setEditable(false);
        this.totalNumberClientsTextField.addKeyListener(this);
        
        this.notesTextArea.setWrapStyleWord(true);
        this.notesTextArea.setLineWrap(true);
        this.notesTextArea.setEditable(false);
        this.notesTextArea.addKeyListener(this);
        
        int totalClients = 0;
        for (String tableNumber: tables.keySet()) {
            for (PlateOrder order: tables.get(tableNumber).getOrders()) {
                if (order.getPlate() instanceof MainCourse) {
                    totalClients += order.getQuantity();
                }
            }
        }
        
        String clientLabel = (totalClients > 1) ? "clients": "client";
        this.totalNumberClientsTextField.setText(
            String.valueOf(totalClients) +
            " " +
            clientLabel
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        totalNumberClientsLabel = new javax.swing.JLabel();
        totalNumberClientsTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        notesTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        totalNumberClientsLabel.setText("Total number of clients for this service:");

        totalNumberClientsTextField.setFont(new java.awt.Font("Dialog", 0, 30)); // NOI18N
        totalNumberClientsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        notesTextArea.setColumns(20);
        notesTextArea.setRows(5);
        notesTextArea.setText("Notes: Clients are counted for the current service only. Past files are not taken into account. Only effective clients are taken into account which means the number of clients are determined by the number of main courses ordered for this particular service only.");
        jScrollPane1.setViewportView(notesTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(totalNumberClientsTextField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(totalNumberClientsLabel)
                        .addGap(0, 217, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(totalNumberClientsLabel)
                .addGap(18, 18, 18)
                .addComponent(totalNumberClientsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(okButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea notesTextArea;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel totalNumberClientsLabel;
    private javax.swing.JTextField totalNumberClientsTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        this.setVisible(false);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
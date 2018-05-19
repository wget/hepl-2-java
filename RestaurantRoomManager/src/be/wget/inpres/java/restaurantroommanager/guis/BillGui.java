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
package be.wget.inpres.java.restaurantroommanager.guis;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import javax.swing.JOptionPane;

/**
 *
 * @author wget
 */
public class BillGui extends javax.swing.JDialog implements KeyListener, ItemListener {

    private boolean billPaid;
    
    /**
     * Creates new form BillGui
     */
    public BillGui(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        
        this.tableTextfield.setEditable(false);
        this.platesQuantityTextfield.setEditable(false);
        this.billAmountTextfield.setEditable(false);
        
        
        this.billPaid = false;
        
        this.tableTextfield.addKeyListener(this);
        this.platesQuantityTextfield.addKeyListener(this);
        this.billAmountTextfield.addKeyListener(this);
        this.billActionViewRadio.addKeyListener(this);
        this.billActionPrintRadio.addKeyListener(this);
        this.billActionPayRadio.addKeyListener(this);
        this.billActionViewRadio.addItemListener(this);
        this.billActionPrintRadio.addItemListener(this);
        this.billActionPayRadio.addItemListener(this);
        
        // Set at least one radio selected.
        this.billActionPayRadio.setSelected(true);
        
        // Set tooltips
        this.billActionViewRadio.setToolTipText("Just to see the bill details");
        this.billActionPrintRadio.setToolTipText("Craft an invoice document");
        this.billActionPayRadio.setToolTipText("Register the payment");
    }
    
    public void setTable(String table) {
        this.tableTextfield.setText(table);
    }
    
    public void setPlatesQuantity(int quantity) {
        this.platesQuantityTextfield.setText(String.valueOf(quantity));
    }
    
    public void setBillAmount(BigDecimal amount) {
        this.billAmountTextfield.setText(String.valueOf(amount));
    }
    
    public void setBillPaidState(boolean state) {
        this.billPaid = state;
        if (this.billPaid) {
            this.billAmountTextfield.setBackground(new Color(0, 153, 51));
        } else {
            this.billAmountTextfield.setBackground(new Color(255, 0, 0));
        }
    }
    
    public boolean getPaymentDetails() {
        return this.billPaid;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        billActionRadioGroup = new javax.swing.ButtonGroup();
        tableLabel = new javax.swing.JLabel();
        platesQuantityLabel = new javax.swing.JLabel();
        billAmountLabel = new javax.swing.JLabel();
        billActionViewRadio = new javax.swing.JRadioButton();
        billActionPrintRadio = new javax.swing.JRadioButton();
        billActionPayRadio = new javax.swing.JRadioButton();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        tableTextfield = new javax.swing.JTextField();
        platesQuantityTextfield = new javax.swing.JTextField();
        billAmountTextfield = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tableLabel.setText("Table:");

        platesQuantityLabel.setText("Plates quantity:");

        billAmountLabel.setText("Bill amount:");

        billActionRadioGroup.add(billActionViewRadio);
        billActionViewRadio.setText("View");
        billActionViewRadio.setPreferredSize(new java.awt.Dimension(100, 25));

        billActionRadioGroup.add(billActionPrintRadio);
        billActionPrintRadio.setText("Print");
        billActionPrintRadio.setPreferredSize(new java.awt.Dimension(100, 25));

        billActionRadioGroup.add(billActionPayRadio);
        billActionPayRadio.setText("Pay");
        billActionPayRadio.setPreferredSize(new java.awt.Dimension(100, 25));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        billAmountTextfield.setBackground(new java.awt.Color(0, 153, 51));
        billAmountTextfield.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tableLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(billAmountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                            .addComponent(platesQuantityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tableTextfield)
                            .addComponent(platesQuantityTextfield, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(billAmountTextfield)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(billActionViewRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(billActionPrintRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addComponent(billActionPayRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(platesQuantityTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(platesQuantityLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(billAmountTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(billAmountLabel))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(billActionViewRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(billActionPayRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(billActionPrintRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (this.billActionViewRadio.isSelected()) {
            this.viewBill();
        } else if (this.billActionPrintRadio.isSelected()) {
            this.printBill();
        } else if (this.billActionPayRadio.isSelected()) {
            this.payBill();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton billActionPayRadio;
    private javax.swing.JRadioButton billActionPrintRadio;
    private javax.swing.ButtonGroup billActionRadioGroup;
    private javax.swing.JRadioButton billActionViewRadio;
    private javax.swing.JLabel billAmountLabel;
    private javax.swing.JTextField billAmountTextfield;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel platesQuantityLabel;
    private javax.swing.JTextField platesQuantityTextfield;
    private javax.swing.JLabel tableLabel;
    private javax.swing.JTextField tableTextfield;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            if (this.billActionViewRadio.isSelected()) {
                this.viewBill();
            } else if (this.billActionPrintRadio.isSelected()) {
                this.printBill();
            } else if (this.billActionPayRadio.isSelected()) {
                this.payBill();
            }
        }
    }
    
    private void viewBill() {
        System.out.println("Bill action: view");
    }
    
    private void printBill() {
        System.out.println("Bill action: print");
    }
    
    private void payBill() {
        System.out.println("Bill action: pay");
        if (this.billPaid) {
            JOptionPane.showMessageDialog(this,
            "The bill has already been paid",
            "Payment error",
            JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.billPaid = true;
        this.billAmountTextfield.setBackground(new Color(0, 153, 51));
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == ItemEvent.SELECTED) {
            if (ie.getSource() == this.billActionViewRadio) {
                this.okButton.setText(this.billActionViewRadio.getText());
            } else if (ie.getSource() == this.billActionPrintRadio) {
                this.okButton.setText(this.billActionPrintRadio.getText());
            } else if (ie.getSource() == this.billActionPayRadio) {
                this.okButton.setText(this.billActionPayRadio.getText());
            }
        }
    }
}

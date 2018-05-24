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
package be.wget.inpres.java.restaurant.kitchenmanager.guis;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import be.wget.inpres.java.restaurant.dataobjects.Table;
import be.wget.inpres.java.restaurant.fileserializer.DefaultDessertsImporter;
import be.wget.inpres.java.restaurant.fileserializer.DefaultMainCoursesImporter;
import be.wget.inpres.java.restaurant.orderprotocol.NetworkProtocolMalformedFieldException;
import be.wget.inpres.java.restaurant.orderprotocol.NetworkProtocolUnexpectedFieldException;
import be.wget.inpres.java.restaurant.orderprotocol.NetworkProtocolOrderReceiver;
import be.wget.inpres.java.restaurant.orderprotocol.NetworkProtocolServeNotifySender;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import network.NetworkBasicClient;
import network.NetworkBasicServer;

/**
 *
 * @author wget
 */
public class RestaurantKitchenManagerGui 
        extends javax.swing.JFrame
        implements TableModelListener,
                   ListSelectionListener {

    private NetworkBasicServer networkReceiver;
    private NetworkBasicClient networkSender;
    private RestaurantConfig applicationConfig;
    private Icon applicationIcon;
    private HashMap<String, MainCourse> defaultMainCourses;
    private HashMap<String, Dessert> defaultDesserts;

    private ArrayList<Table> tablesModel;
    private ArrayList<Table> tablesModelTotal;
    private ArrayList<Table> newTablesOrders;
    private int platesReceivedTableModelSizeBefore;
    private int platesReceivedTableModelSizeAfter;
    private int platesBeingPreparedTableModelSizeBefore;
    private int platesBeingPreparedTableModelSizeAfter;
    
    /**
     * Creates new form KitchenManagerGui
     */
    public RestaurantKitchenManagerGui() {
        initComponents();
        this.initAdditionalComponents();
    }
    
    private void initAdditionalComponents() {
        this.applicationConfig = new RestaurantConfig();
        try {
            this.applicationIcon = new ImageIcon(ImageIO.read(RestaurantKitchenManagerGui.class.getResourceAsStream(
                    "/be/wget/inpres/java/restaurant/assets/appIcon.png")));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "The \"assets\" folder couldn't be found.",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
            this.applicationIcon = new ImageIcon();
        }
        this.setIconImage(((ImageIcon)this.applicationIcon).getImage());

        if (!this.applicationConfig.isConfigFileDefined()) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to find the settings file \""
                + this.applicationConfig.getSettingsFilename()
                + "\". Using the defaults settings.",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
        }

        this.setTitle(this.applicationConfig.getRestaurantName() + ": KitchenManager");
        this.setLocationRelativeTo(null);
        this.orderReceivedCheckbox.setEnabled(false);
        this.platesReceivedTable.setModel(new PlatesReceivedTableModel());
        this.platesReceivedTable.setAutoCreateRowSorter(true);
        this.platesReceivedTable.getSelectionModel().addListSelectionListener(this);
        
        this.orderCommentTextArea.setEditable(false);
        
        this.platesBeingPreparedTable.setModel(new PlatesBeingPreparedTableModel());
        this.platesBeingPreparedTable.setAutoCreateRowSorter(true);
        this.platesBeingPreparedTable.getModel().addTableModelListener(this);
        this.platesBeingPreparedTable.addMouseListener(
            new PlatesBeingPreparedTableMouseAdapter(
                this.applicationConfig,
                this,
                this.platesBeingPreparedTable));

        // Populate list of plates
        this.defaultMainCourses = new DefaultMainCoursesImporter(
            this.applicationConfig,
            "plates.default.txt")
                .getDefaultPlatesHashMap();
        this.defaultDesserts = new DefaultDessertsImporter(
            this.applicationConfig,
            "")
                .getDefaultPlatesHashMap();

        this.tablesModel = new ArrayList<>();
        this.tablesModelTotal = new ArrayList<>();
        this.newTablesOrders = new ArrayList<>();
        this.startNetworkServerConnection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        orderReceivedCheckbox = new javax.swing.JCheckBox();
        orderViewButton = new javax.swing.JButton();
        orderReceivedLabel = new javax.swing.JLabel();
        orderAcceptButton = new javax.swing.JButton();
        orderPlatesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        platesReceivedTable = new javax.swing.JTable();
        platesBeingPreparedLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        platesBeingPreparedTable = new javax.swing.JTable();
        orderReadyNotifyButton = new javax.swing.JButton();
        orderDeclineButton = new javax.swing.JButton();
        orderCommentLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        orderCommentTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        orderReceivedCheckbox.setText("New order received");

        orderViewButton.setText("View order");
        orderViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderViewButtonActionPerformed(evt);
            }
        });

        orderReceivedLabel.setText(">>");

        orderAcceptButton.setText("Accept order");
        orderAcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderAcceptButtonActionPerformed(evt);
            }
        });

        orderPlatesLabel.setText("Plates of the order:");

        platesReceivedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Quantity", "Plate", "Table", "Hour"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        platesReceivedTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(platesReceivedTable);

        platesBeingPreparedLabel.setText("Plates currently being prepared:");

        platesBeingPreparedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Quantity", "Plate", "Table", "Arrival time", "Being prepared", "Ready to serve", "Served"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        platesBeingPreparedTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(platesBeingPreparedTable);

        orderReadyNotifyButton.setText("Notify order ready");
        orderReadyNotifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderReadyNotifyButtonActionPerformed(evt);
            }
        });

        orderDeclineButton.setText("Decline order");
        orderDeclineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderDeclineButtonActionPerformed(evt);
            }
        });

        orderCommentLabel.setText("Order comment:");

        orderCommentTextArea.setColumns(20);
        orderCommentTextArea.setRows(5);
        jScrollPane2.setViewportView(orderCommentTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(orderPlatesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(orderReadyNotifyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(orderReceivedCheckbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(orderViewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(orderReceivedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(orderAcceptButton, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                            .addComponent(orderDeclineButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(orderCommentLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(platesBeingPreparedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(orderReceivedCheckbox)
                    .addComponent(orderViewButton)
                    .addComponent(orderAcceptButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orderReceivedLabel)
                    .addComponent(orderDeclineButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderPlatesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(orderCommentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(platesBeingPreparedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderReadyNotifyButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void orderViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderViewButtonActionPerformed

        if (this.networkSender == null) {
            this.startNetworkClientConnection();
        }

        String request = this.networkReceiver.getMessage();
        // RIEN is provided by the underlying networking lib provided by the
        // teacher. Cannot modify it to have a more elegant English based
        // application.
        if (request == null || request.isEmpty() || request.equals("RIEN")) {
            JOptionPane.showMessageDialog(
                this,
                "There is no order received.",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        NetworkProtocolOrderReceiver parser = new NetworkProtocolOrderReceiver(
            this.applicationConfig,
            this.defaultMainCourses,
            this.defaultDesserts,
            request);
        try {
            this.newTablesOrders.addAll(parser.getTables());
                    System.out.println("DEBUG WE ARE HERE");
        System.out.println("WEBUG SIZE newTableOrders: " + this.newTablesOrders.size());
        System.out.println("WEBUG SIZE newTableOrders orders size: " + this.newTablesOrders.get(0).getOrders().size());
        } catch (NetworkProtocolUnexpectedFieldException |
                 NetworkProtocolMalformedFieldException ex) {
            JOptionPane.showMessageDialog(
                this,
                "An error occurred while parsing the received order. " +
                    "The order has been declined.",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
            this.networkReceiver.sendMessage(
                this.applicationConfig.getKitchenOrderDeclinedPayload() +
                this.applicationConfig.getOrderFieldDelimiter() +
                "NETWORK_ISSUE");
            return;
        }
        
        // Update model
        boolean tableFound = false;
        for (Table newTable: this.newTablesOrders) {
            System.out.println("DEBUG newTable received : " + newTable.getNumber());
            tableFound = false;
            for (Table table: this.tablesModelTotal) {
                System.out.println("DEBUG checking with table : " + table.getNumber());
                if (table.getNumber().equals(newTable.getNumber())) {
                    tableFound = true;
                    for (PlateOrder order: newTable.getOrders()) {
                        System.out.println("DEBUG adding order");
                        table.addOrder(order);
                    }
                }
            }
            if (!tableFound) {
                System.out.println("DEBUG adding order (table not found): ");
                this.tablesModelTotal.add(newTable);
            }
        }

        this.populateUi(request);
    }//GEN-LAST:event_orderViewButtonActionPerformed

    private void orderAcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderAcceptButtonActionPerformed
        if (this.newTablesOrders.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No order to accept as no new order has been received yet.",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.tablesModel = this.tablesModelTotal;

        this.networkReceiver.sendMessage(
            this.applicationConfig.getKitchenOrderAcceptedPayload());
        this.newTablesOrders.clear();
    }//GEN-LAST:event_orderAcceptButtonActionPerformed

    private void orderDeclineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderDeclineButtonActionPerformed
        if (this.newTablesOrders.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No order to decline as no new order has been received yet.",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Specify reason
        SpecifyReasonOrderDeclinedGui orderRefusedGui =
            new SpecifyReasonOrderDeclinedGui(this, this.applicationConfig);
        orderRefusedGui.setLocationRelativeTo(this);
        orderRefusedGui.setVisible(true);
        this.networkReceiver.sendMessage(
            this.applicationConfig.getKitchenOrderDeclinedPayload() +
            this.applicationConfig.getOrderFieldDelimiter() +
            orderRefusedGui.getOrderRefusedReason());
        orderRefusedGui.dispose();

        // Update model
        this.tablesModelTotal = this.tablesModel;
        this.newTablesOrders.clear();

        // Update UI
        PlatesReceivedTableModel platesReceivedTableModel =
            (PlatesReceivedTableModel)platesReceivedTable.getModel();
        PlatesBeingPreparedTableModel platesBeingPreparedTableModel =
            (PlatesBeingPreparedTableModel)platesBeingPreparedTable.getModel();
        
        // Having another variable (j) is needed here because sicne we are
        // removing lines from the table model, the iteam number remains at the
        // same location since the row are being shifted.
        // Playing around with an iterator would have been cleaner though and
        // the way to go, but time is lacking here...
        for (int i = this.platesReceivedTableModelSizeBefore, j = i;
             i < this.platesReceivedTableModelSizeAfter;
             i++) {
            System.out.println("DEBUG removeRow from platesReceived: " + i);
            platesReceivedTableModel.removeRow(j);
        }
        for (int i = this.platesBeingPreparedTableModelSizeBefore, j = i;
             i < this.platesBeingPreparedTableModelSizeAfter;
             i++) {
            System.out.println("DEBUG removeRow from platesBeingPrepared: " + i);
            platesBeingPreparedTableModel.removeRow(j);
        }
    }//GEN-LAST:event_orderDeclineButtonActionPerformed

    private void orderReadyNotifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderReadyNotifyButtonActionPerformed
        DefaultTableModel platesBeingPreparedTableModel =
            (DefaultTableModel)platesBeingPreparedTable.getModel();
        ArrayList<PlateOrder> ordersToNotify = new ArrayList<>();
        boolean orderBeingPrepared = false;
        for (int i = 0; i < platesBeingPreparedTableModel.getRowCount(); i++) {
            orderBeingPrepared = (Boolean)platesBeingPreparedTableModel
                .getValueAt(i, PlatesBeingPreparedTableColumns.READY_TO_SERVE);
            if (!orderBeingPrepared) {
                continue;
            }

            String plateCode = (String)platesBeingPreparedTableModel
                .getValueAt(i, PlatesBeingPreparedTableColumns.PLATE_CODE);
            Plate newPlate = null;
            for (String mainCourseCode: this.defaultMainCourses.keySet()) {
                if (this.defaultMainCourses.get(mainCourseCode).getCode()
                    .equals(plateCode)) {
                    newPlate = this.defaultMainCourses.get(mainCourseCode);
                }
            }
            if (newPlate == null) {
                for (String dessertCode: this.defaultDesserts.keySet()) {
                    if (this.defaultDesserts.get(dessertCode).getCode()
                        .equals(plateCode)) {
                        newPlate = this.defaultDesserts.get(dessertCode);
                    }
                }
            }

            int quantity = (int)platesBeingPreparedTableModel
                .getValueAt(i, PlatesBeingPreparedTableColumns.QUANTITY);
            PlateOrder newOrder = new PlateOrder(newPlate, quantity);
            ordersToNotify.add(newOrder);
        }

        if (ordersToNotify.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No order to announce as no new order has been marked as ready to serve yet.",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        NetworkProtocolServeNotifySender networkSender =
            new NetworkProtocolServeNotifySender(
                this.applicationConfig, ordersToNotify);
        String request = networkSender.encodeRequest();
        System.out.println("DEBUG request to send: "+ request);
        System.out.println("DEBUG getTrapServerAddress: "+ this.applicationConfig.getTrapServerAddress());
        System.out.println("DEBUG getTrapServerPort: "+ this.applicationConfig.getTrapServerPort());

        this.networkSender.sendStringWithoutWaiting(request);
    }//GEN-LAST:event_orderReadyNotifyButtonActionPerformed

    private void startNetworkClientConnection() {
        this.networkSender = new NetworkBasicClient(
            this.applicationConfig.getTrapServerAddress(),
            this.applicationConfig.getTrapServerPort());
    }

    private void startNetworkServerConnection() {
        this.networkReceiver = new NetworkBasicServer(this.applicationConfig.getServerPort(), this.orderReceivedCheckbox);
        System.out.println("Server listening on " + this.applicationConfig.getServerPort());
    }

    private void populateUi(String receivedRequest) {

        this.orderReceivedLabel.setSize(
            this.getSize().width - this.orderDeclineButton.getSize().width - 50,
            this.getSize().height - this.orderDeclineButton.getSize().height - 50);
        this.orderReceivedLabel.setText(">> " + receivedRequest);

        PlatesReceivedTableModel platesReceivedTableModel =
            (PlatesReceivedTableModel)platesReceivedTable.getModel();
        PlatesBeingPreparedTableModel platesBeingPreparedTableModel =
            (PlatesBeingPreparedTableModel)platesBeingPreparedTable.getModel();
        platesReceivedTableModelSizeBefore = platesReceivedTableModel.getRowCount();
        platesBeingPreparedTableModelSizeBefore = platesBeingPreparedTableModel.getRowCount();
        int platesBeingPreparedTableRow = platesBeingPreparedTableModelSizeBefore;
        
        for (Table table: this.newTablesOrders) {
            
            for (PlateOrder order: table.getOrders()) {
                
                 System.out.println("DEBUG populateUI adding : " + order.getPlate().getLabel());
                ArrayList<Object> platesReceivedTableLine = new ArrayList<>();
                ArrayList<Object> platesBeingPreparedTableLine = new ArrayList<>();
                
                platesReceivedTableLine.add(
                    PlatesReceivedTableColumns.QUANTITY, order.getQuantity());
                platesBeingPreparedTableLine.add(
                    PlatesReceivedTableColumns.QUANTITY, order.getQuantity());
                if (order.getPlate() instanceof MainCourse) {
                    platesReceivedTableLine.add(
                        PlatesReceivedTableColumns.PLATE_CODE,
                        ((MainCourse)order.getPlate()).getCode());
                    platesBeingPreparedTableLine.add(
                        PlatesReceivedTableColumns.PLATE_CODE,
                        ((MainCourse)order.getPlate()).getCode());
                } else if (order.getPlate() instanceof Dessert) {
                    platesReceivedTableLine.add(
                        PlatesReceivedTableColumns.PLATE_CODE,
                        ((Dessert)order.getPlate()).getCode());
                    platesBeingPreparedTableLine.add(
                        PlatesReceivedTableColumns.PLATE_CODE,
                        ((Dessert)order.getPlate()).getCode());
                }
                platesReceivedTableLine.add(
                    PlatesReceivedTableColumns.TABLE_NUMBER,
                    table.getNumber());
                platesBeingPreparedTableLine.add(
                    PlatesReceivedTableColumns.TABLE_NUMBER,
                    table.getNumber());
                
                // TODO: Add time format based on settings
                platesReceivedTableLine.add(
                    PlatesReceivedTableColumns.ARRIVAL_TIME,
                    order.getOrderDate());
                platesBeingPreparedTableLine.add(
                    PlatesReceivedTableColumns.ARRIVAL_TIME,
                    order.getOrderDate());
                
                // Needed to initialize the checkboxes values for the second
                // Jtable.
                platesBeingPreparedTableLine.add(false);
                platesBeingPreparedTableLine.add(false);
                platesBeingPreparedTableLine.add(false);

                // Needed to know whether the cells can be edited or not.
                // Disable edition of the 4 first columns for first and second
                // tables.
                for (int i = PlatesBeingPreparedTableColumns.QUANTITY;
                    i <= PlatesBeingPreparedTableColumns.SERVED;
                    i++) {
                   platesBeingPreparedTableModel.setCellEditable(
                       platesBeingPreparedTableRow, i, false);
                }
                // Enable edition of the first order state column
                platesBeingPreparedTableModel.setCellEditable(
                   platesBeingPreparedTableRow,
                   PlatesBeingPreparedTableColumns.BEING_PREPARED,
                   true);
                
                platesReceivedTableModel.addRow(
                    platesReceivedTableLine.toArray());
                platesBeingPreparedTableModel.addRow(
                    platesBeingPreparedTableLine.toArray());
                platesBeingPreparedTableRow++;
            }
        }

        this.platesReceivedTableModelSizeAfter = 
            platesReceivedTableModel.getRowCount();
        this.platesBeingPreparedTableModelSizeAfter = 
            platesBeingPreparedTableModel.getRowCount();
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton orderAcceptButton;
    private javax.swing.JLabel orderCommentLabel;
    private javax.swing.JTextArea orderCommentTextArea;
    private javax.swing.JButton orderDeclineButton;
    private javax.swing.JLabel orderPlatesLabel;
    private javax.swing.JButton orderReadyNotifyButton;
    private javax.swing.JCheckBox orderReceivedCheckbox;
    private javax.swing.JLabel orderReceivedLabel;
    private javax.swing.JButton orderViewButton;
    private javax.swing.JLabel platesBeingPreparedLabel;
    private javax.swing.JTable platesBeingPreparedTable;
    private javax.swing.JTable platesReceivedTable;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent e) {

        // Check if this concerns the second table or not.
        if (e.getSource() != this.platesBeingPreparedTable.getModel()) {
            return;
        }
        
        // Check if this only concerns UPDATE events. We don't care about
        // insertions here.
        if (e.getType() != TableModelEvent.UPDATE) {
            return;
        }

        PlatesBeingPreparedTableModel platesBeingPreparedTableModel =
            (PlatesBeingPreparedTableModel)platesBeingPreparedTable.getModel();
        
        if (e.getColumn() == PlatesBeingPreparedTableColumns.BEING_PREPARED) {
            System.out.println("BEING_PREPARED_COLUMN DEBUG");
            platesBeingPreparedTableModel.setCellEditable(
                e.getFirstRow(),
                PlatesBeingPreparedTableColumns.BEING_PREPARED,
                false);
            platesBeingPreparedTableModel.setCellEditable(
                e.getFirstRow(),
                PlatesBeingPreparedTableColumns.READY_TO_SERVE, 
                true);
            return;
        }

        if (e.getColumn() == PlatesBeingPreparedTableColumns.READY_TO_SERVE) {
            System.out.println("READY_TO_SERVE_COLUMN DEBUG");
            platesBeingPreparedTableModel.setCellEditable(
                e.getFirstRow(),
                PlatesBeingPreparedTableColumns.READY_TO_SERVE,
                false);
            platesBeingPreparedTableModel.setCellEditable(
                e.getFirstRow(),
                PlatesBeingPreparedTableColumns.SERVED,
                true);
            return;
        }

        if (e.getColumn() == PlatesBeingPreparedTableColumns.SERVED) {
            System.out.println("SERVED_COLUMN DEBUG");
            platesBeingPreparedTableModel.setCellEditable(
                e.getFirstRow(),
                PlatesBeingPreparedTableColumns.SERVED,
                false);
            return;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() != this.platesReceivedTable.getSelectionModel()) {
            return;
        }

        // Happens when reordering table content or when declining order when
        // the order has been viewed and we have list focus.
        if (this.platesReceivedTable.getSelectedRow() == -1) {
            return;
        }

        String plateCode = (String)this.platesReceivedTable.getModel().getValueAt(
            this.platesReceivedTable.getSelectedRow(),
            PlatesReceivedTableColumns.PLATE_CODE);
        if (plateCode == null) {
            return;
        }
        PlateOrder orderWithComment = null;
        for (Table table: this.tablesModelTotal) {
            for (PlateOrder order: table.getOrders()) {
                if (order.getPlate() instanceof MainCourse) {
                    if (((MainCourse)order.getPlate()).getCode()
                        .equals(plateCode)) {
                        orderWithComment = order;
                        break;
                    }
                } else {
                    if (((Dessert)order.getPlate()).getCode()
                        .equals(plateCode)) {
                        orderWithComment = order;
                        break;
                    }
                }
            }
        }
        if (orderWithComment.getComment().isEmpty()) {
            this.orderCommentTextArea.setText("N/A");
            return;
        }
        this.orderCommentTextArea.setText(orderWithComment.getComment());
    }
}

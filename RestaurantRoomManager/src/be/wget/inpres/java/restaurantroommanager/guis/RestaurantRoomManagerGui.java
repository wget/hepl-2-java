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

import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import be.wget.inpres.java.restaurant.dataobjects.Table;
import be.wget.inpres.java.restaurantroommanager.TooManyCoversException;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author wget
 */
public class RestaurantRoomManagerGui extends javax.swing.JFrame implements KeyListener {

    // Hashtable is deprecated, let's use a Hashmap instead.
    private HashMap<String, String> credentials;
    private HashMap<String, Table> tables;
    private ArrayList<MainCourse> defaultMainCourses;
    private ArrayList<Dessert> defaultDesserts;
    
    private final String applicationName = "Restaurant Room Manager \"Le gourmet audatieux\"";
    private final String currencySymbol = "EUR";
    
    private String waiterName;
    private Table selectedTable;
    private int platesQuantity;
    private int dessertQuantity;
    private ArrayList<Table> savedTables;
    private ArrayList<PlateOrder> ordersToSend;
    private ArrayList<String> ordersToSendStringArray;
    private ArrayList<PlateOrder> servedPlates;
    private BigDecimal billAmount;
    private boolean billPaid;

    /**
     * Creates new form Main
     */
    public RestaurantRoomManagerGui() {

        initComponents();
        // Additional GUI customizations
        this.initAdditionalComponents();
        this.initUXEvents();

        this.populateData();
        this.populateGui();
        
        this.verifyUserCredentials();
    }
    
    private void initAdditionalComponents() {
        this.setTitle(this.applicationName + ": " + waiterName);
        this.setLocationRelativeTo(null);
        this.ordersSentCheckbox.setEnabled(false);
        this.ordersReadyCheckbox.setEnabled(false);
        this.servedPlatesList.setModel(new DefaultListModel<>());
        this.billAmount = new BigDecimal("0");
        this.billPaid = false;
    }
    
    private void populateData() {
        this.savedTables = new ArrayList<>();
        this.ordersToSend = new ArrayList<>();
        this.ordersToSendStringArray = new ArrayList<>();
        this.servedPlates = new ArrayList<>();
        
        // FIXME: Hardcoded credentials
        this.credentials = new HashMap<>();
        this.credentials.put("wget", this.getSha512FromPassword("12345"));
        this.credentials.put("wagner", this.getSha512FromPassword("vilvens"));
        
        // FIXME: Hardcoded tables
        this.tables = new HashMap<>();
        this.tables.put("G1", new Table("G1", 4));
        this.tables.put("G2", new Table("G2", 4));
        this.tables.put("G3", new Table("G3", 4));
        this.tables.put("C11", new Table("C11", 4));
        this.tables.put("C12", new Table("C12", 6));
        this.tables.put("C13", new Table("C13", 4));
        this.tables.put("C21", new Table("C21", 6));
        this.tables.put("C22", new Table("C22", 6));
        this.tables.put("D1", new Table("D1", 4));
        this.tables.put("D2", new Table("D2", 2));
        this.tables.put("D3", new Table("D3", 2));
        this.tables.put("D3", new Table("D4", 2));
        this.tables.put("D5", new Table("D5", 2));
        
        // Populate list of plates
        this.defaultMainCourses = new ArrayList<>();
        this.defaultMainCourses.add(new MainCourse("Veau au rollmops sauce Herve", 15.75, "VRH"));
        this.defaultMainCourses.add(new MainCourse("Cabillaud chantilly de Terre Neuve", 16.9, "CC"));
        this.defaultMainCourses.add(new MainCourse("Fillet de boeuf Enfer des papilles", 16.8, "FE"));
        this.defaultMainCourses.add(new MainCourse("Gruyère farci aux rognons-téquila", 13.4, "VRH"));
        this.defaultMainCourses.add(new MainCourse("Potée auvergnate au miel", 12.5, "PA"));
        this.defaultDesserts = new ArrayList<>();
        this.defaultDesserts.add(new Dessert("Mousse au chocolat salé", 5.35, "D_MC"));
        this.defaultDesserts.add(new Dessert("Sorbet citron courgette Colonel", 6.85, "D_SC"));
        this.defaultDesserts.add(new Dessert("Duo de crêpes Juliettes", 6, "D_CJ"));
        this.defaultDesserts.add(new Dessert("Dame grise", 5.55, "D_SG"));
        this.defaultDesserts.add(new Dessert("Crème très brulée carbonne", 7, "D_CB"));
    }
    
    private void initUXEvents() {
        this.billPayButton.addKeyListener(this);
        this.dessertsCombobox.addKeyListener(this);
        this.dessertsCommentsTextfield.addKeyListener(this);
        this.dessertsOrderButton.addKeyListener(this);
        this.dessertsQuantityTextfield.addKeyListener(this);
        this.drinksAddButton.addKeyListener(this);
        this.drinksAmountTextfield.addKeyListener(this);
        this.platesOrderButton.addKeyListener(this);
        this.ordersReadyCheckbox.addKeyListener(this);
        this.ordersSendButton.addKeyListener(this);
        this.ordersSentCheckbox.addKeyListener(this);
        this.platesCombobox.addKeyListener(this);
        this.platesCommentTextfield.addKeyListener(this);
        this.platesQuantityTextfield.addKeyListener(this);
        this.readAvailablePlatesButton.addKeyListener(this);
        this.tableCombobox.addKeyListener(this);
    }
    
    private void populateGui() {
        // Populate the combobox of restaurant tables
        // toArray() returns an array of objets, we need an array of strings
        // first in order to be able to sort the table alphabetically.
        Object[] tableKeys = this.tables.keySet().toArray();
        String[] tableNames = Arrays.copyOf(tableKeys, tableKeys.length, String[].class);
        Arrays.sort(tableNames);
        this.tableCombobox.setModel(new DefaultComboBoxModel<>(tableNames));
     
        ArrayList<String> mainCourseNames = new ArrayList<>();
        // Use functional operator like in JS.
        // Equivalent to: for(MainCourse plate: this.defaultMainCourses)
        this.defaultMainCourses.forEach((plate) -> {
            mainCourseNames.add(plate.getCode() + ": " + plate.getLabel() +
                " (" + plate.getPrice() + ")");
        });
        
        this.platesCombobox.setModel(new DefaultComboBoxModel<>(
            mainCourseNames.toArray(new String[mainCourseNames.size()])));
        
        ArrayList<String> dessertNames = new ArrayList<>();
        this.defaultDesserts.forEach((plate) -> {
            dessertNames.add(plate.getCode() + ": " + plate.getLabel() +
                " (" + plate.getPrice() + ")");
        });
        this.dessertsCombobox.setModel(new DefaultComboBoxModel<>(
            (String[])dessertNames.toArray(new String[dessertNames.size()]))); 
        
        // The getSelection on combobox is only populated when there has been
        // previously a selection. By default, when the app is launched, while
        // the selected item is the first of the combobox, the app assumes
        // no selection has been made yet, which could lead to a crash.
        // Prevent this.
        this.tableCombobox.setSelectedIndex(0);
        this.platesCombobox.setSelectedIndex(0);
        this.dessertsCombobox.setSelectedIndex(0);
    }
    
    private void verifyUserCredentials() {
        LoginGui login = new LoginGui(this, true, this.applicationName);
        
        while (true) {
            
            login.setVisible(true);
            
            if (login.isDialogCancelled()) {
                System.exit(0);
            }
            
            String hashedPassword = this.credentials.get(login.getUsername());
        
            if (hashedPassword == null) {
                JOptionPane.showMessageDialog(getParent(),
                    "Unable to find the user \""
                    + login.getUsername()
                    + "\". Quitting.",
                    "Restaurant Room Manager",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            this.waiterName = login.getUsername();

            if (!hashedPassword.equals(this.getSha512FromPassword(login.getPassword()))) {
                JOptionPane.showMessageDialog(getParent(),
                    "The password for the user \""
                    + login.getUsername()
                    + " \"is incorrect. Retry.",
                    "Restaurant Room Manager",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            
            break;
        }
        login.dispose();
    }
    
    private String getSha512FromPassword(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());
            
            byte byteData[] = md.digest();
            
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i<byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {}
        return null;
    }
    
        
    private void quitApplicationWithConfirm() {
        Object[] options = {
            "No, continue my work",
            "Yes, quit the app"
        };
        int answer = JOptionPane.showOptionDialog(
            this,
            "Do you really want to quit Restaurant Room Manager?",
            "Exit Restaurant Room Manager",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            // Do not use a custom Icon.
            null,
            // SPecifies the titles of the buttons.
            options,
            // Use default button title.
            options[1]);
        if (answer == JOptionPane.CLOSED_OPTION ||
            // Yes, we needed to revert the sense of the YES_OPTION, because we
            // really wanted to have the No statement at the left, which is Yes
            // by default on Swing.
            answer == JOptionPane.YES_OPTION) {
            return;
        }
        System.exit(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableLabel = new javax.swing.JLabel();
        tableCombobox = new javax.swing.JComboBox<>();
        maxCoversLabel = new javax.swing.JLabel();
        effectiveCoversLabel = new javax.swing.JLabel();
        maxCoversValueLabel = new javax.swing.JLabel();
        effectiveCoversValueLabel = new javax.swing.JLabel();
        servedPlatesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        servedPlatesList = new javax.swing.JList<>();
        drinksLabel = new javax.swing.JLabel();
        drinksAmountTextfield = new javax.swing.JTextField();
        eurLabel = new javax.swing.JLabel();
        drinksAddButton = new javax.swing.JButton();
        billLabel = new javax.swing.JLabel();
        billPaidStateLabel = new javax.swing.JLabel();
        billPayButton = new javax.swing.JButton();
        platesOrdersLabel = new javax.swing.JLabel();
        platesLabel = new javax.swing.JLabel();
        platesCombobox = new javax.swing.JComboBox<>();
        platesQuantityLabel = new javax.swing.JLabel();
        platesQuantityTextfield = new javax.swing.JTextField();
        platesCommentTextfield = new javax.swing.JTextField();
        platesCommentsLabel = new javax.swing.JLabel();
        platesOrderButton = new javax.swing.JButton();
        dessertsLabel = new javax.swing.JLabel();
        dessertsCombobox = new javax.swing.JComboBox<>();
        dessertsQuantityLabel = new javax.swing.JLabel();
        dessertsQuantityTextfield = new javax.swing.JTextField();
        dessertsCommentsLabel = new javax.swing.JLabel();
        dessertsCommentsTextfield = new javax.swing.JTextField();
        dessertsOrderButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        ordersToSendLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ordersToSendList = new javax.swing.JList<>();
        ordersSendButton = new javax.swing.JButton();
        ordersSentCheckbox = new javax.swing.JCheckBox();
        ordersReadyCheckbox = new javax.swing.JCheckBox();
        readAvailablePlatesButton = new javax.swing.JButton();
        billAmountLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tableLabel.setText("Table:");

        tableCombobox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tableComboboxItemStateChanged(evt);
            }
        });
        tableCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableComboboxActionPerformed(evt);
            }
        });

        maxCoversLabel.setText("Max covers:");

        effectiveCoversLabel.setText("Effective covers:");

        maxCoversValueLabel.setText("X");

        effectiveCoversValueLabel.setText("X");

        servedPlatesLabel.setText("Served plates:");

        jScrollPane1.setViewportView(servedPlatesList);

        drinksLabel.setText("Drinks (bar):");

        drinksAmountTextfield.setText("X");

        eurLabel.setText("EUR");

        drinksAddButton.setText("Add");
        drinksAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drinksAddButtonActionPerformed(evt);
            }
        });

        billLabel.setText("Bill:");

        billPaidStateLabel.setText("NOT PAID");

        billPayButton.setText("Checkout");
        billPayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billPayButtonActionPerformed(evt);
            }
        });

        platesOrdersLabel.setText("Plate orders:");

        platesLabel.setText("Plates:");

        platesQuantityLabel.setText("Quantity:");

        platesQuantityTextfield.setText("X");
        platesQuantityTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                platesQuantityTextfieldActionPerformed(evt);
            }
        });

        platesCommentsLabel.setText("Comments:");

        platesOrderButton.setText("Order plates");
        platesOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                platesOrderButtonActionPerformed(evt);
            }
        });

        dessertsLabel.setText("Desserts:");

        dessertsQuantityLabel.setText("Quantity:");

        dessertsQuantityTextfield.setText("X");

        dessertsCommentsLabel.setText("Comments:");

        dessertsOrderButton.setText("Order desserts");
        dessertsOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dessertsOrderButtonActionPerformed(evt);
            }
        });

        ordersToSendLabel.setText("Orders to send:");

        jScrollPane2.setViewportView(ordersToSendList);

        ordersSendButton.setText("Send orders");
        ordersSendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ordersSendButtonActionPerformed(evt);
            }
        });

        ordersSentCheckbox.setText("Orders sent");

        ordersReadyCheckbox.setText("Orders ready");

        readAvailablePlatesButton.setText("Read available plates");

        billAmountLabel.setText("0 EUR");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dessertsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(926, 926, 926))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1092, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tableLabel)
                                .addGap(18, 18, 18)
                                .addComponent(tableCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(80, 80, 80)
                                .addComponent(servedPlatesLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(maxCoversLabel)
                                    .addComponent(effectiveCoversLabel))
                                .addGap(41, 41, 41)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(maxCoversValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                                    .addComponent(effectiveCoversValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(drinksLabel)
                                .addGap(18, 18, 18)
                                .addComponent(drinksAmountTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(billLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(billAmountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(eurLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(billPaidStateLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(drinksAddButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(billPayButton, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dessertsCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(platesLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(platesOrdersLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                                    .addComponent(platesCommentsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(platesCommentTextfield)
                                    .addComponent(platesCombobox, 0, 543, Short.MAX_VALUE)))
                            .addComponent(dessertsCommentsTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dessertsQuantityLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(dessertsQuantityTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(platesOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(platesQuantityLabel)
                                .addGap(37, 37, 37)
                                .addComponent(platesQuantityTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dessertsCommentsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(579, 579, 579)
                        .addComponent(dessertsOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ordersToSendLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ordersSentCheckbox)
                                .addGap(25, 25, 25)
                                .addComponent(ordersReadyCheckbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(readAvailablePlatesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(ordersSendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(billPayButton)
                            .addComponent(billPaidStateLabel)
                            .addComponent(billAmountLabel)
                            .addComponent(billLabel)))
                    .addComponent(drinksAddButton)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tableLabel)
                            .addComponent(tableCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(servedPlatesLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(maxCoversLabel)
                            .addComponent(maxCoversValueLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(effectiveCoversLabel)
                            .addComponent(effectiveCoversValueLabel)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(drinksAmountTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(eurLabel)
                        .addComponent(drinksLabel)))
                .addGap(25, 25, 25)
                .addComponent(platesOrdersLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(platesLabel)
                    .addComponent(platesCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(platesQuantityLabel)
                    .addComponent(platesQuantityTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(platesCommentTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(platesCommentsLabel)
                    .addComponent(platesOrderButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dessertsCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dessertsLabel)
                    .addComponent(dessertsQuantityLabel)
                    .addComponent(dessertsQuantityTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dessertsCommentsLabel)
                    .addComponent(dessertsCommentsTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dessertsOrderButton))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ordersToSendLabel)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ordersSendButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ordersSentCheckbox)
                    .addComponent(ordersReadyCheckbox)
                    .addComponent(readAvailablePlatesButton))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableComboboxActionPerformed
        this.selectedTable = tables.get(this.tableCombobox.getSelectedItem().toString());
        this.maxCoversValueLabel.setText(String.valueOf(this.selectedTable.getMaxCovers()));
    }//GEN-LAST:event_tableComboboxActionPerformed

    private void platesOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platesOrderButtonActionPerformed
        this.orderPlate();
    }//GEN-LAST:event_platesOrderButtonActionPerformed

    private void orderPlate() {
        int plateQuantityInput = 0;
        try {
            plateQuantityInput = Integer.parseInt(this.platesQuantityTextfield.getText());
        
            if (plateQuantityInput <= 0) {
                JOptionPane.showMessageDialog(this,
                    "The plate quantity must be positive",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }

            if (this.platesQuantity + plateQuantityInput > this.selectedTable.getMaxCovers()) {
                throw new TooManyCoversException(plateQuantityInput, this.selectedTable.getMaxCovers());
            }
            
            this.orderCurrentPlate();
        
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "The plate quantity must be a valid number",
                "Invalid quantity",
                JOptionPane.ERROR_MESSAGE); 
        } catch (TooManyCoversException ex) {
            Object[] options = {
                "No, keep my existing " + this.platesQuantity + " covers",
                "Yes, add " + plateQuantityInput + " additional covers"
            };
            int answer = JOptionPane.showOptionDialog(
                this,
                "Do you really want to add " + plateQuantityInput + " additional covers?",
                "Too much covers",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                // Do not use a custom Icon.
                null,
                // Specifies the titles of the buttons.
                options,
                // Use default button title.
                options[1]);
            
            // Cancel everything
            if (answer == JOptionPane.CLOSED_OPTION ||
                // Yes, we needed to revert the sense of the YES_OPTION, because
                // we really wanted to have the No statement at the left, which
                // is Yes by default on Swing.
                answer == JOptionPane.YES_OPTION) {
                return;
            }
            
            this.orderCurrentPlate();
        }
    }

    private void orderCurrentPlate() {
        Plate selectedPlate = this.defaultMainCourses.get(this.platesCombobox.getSelectedIndex());
        int plateQuantityInput = Integer.parseInt(this.platesQuantityTextfield.getText());

        PlateOrder order = new PlateOrder(selectedPlate, plateQuantityInput);
        this.ordersToSend.add(order);
        this.selectedTable.addOrder(order);
        this.ordersToSendStringArray.add(
            plateQuantityInput + " " +
            ((MainCourse)order.getPlate()).getCode() + ": " +
            order.getPlate().getLabel() + " (" +
            Float.valueOf(new DecimalFormat("#.##").format(
                plateQuantityInput * order.getPlate().getPrice()))
                + " " + this.currencySymbol + ")");
        this.ordersToSendList.setListData(
            this.ordersToSendStringArray.toArray(new String[this.ordersToSendStringArray.size()]));
        
        this.platesQuantity += plateQuantityInput;
        this.effectiveCoversValueLabel.setText(String.valueOf(this.platesQuantity));
        this.billAmount = this.billAmount.add(
            new BigDecimal(plateQuantityInput).multiply(
            new BigDecimal(order.getPlate().getPrice())))
            // Round to the nearest value (2 digits).
            // Rounding seems to be needed by law
            // src.: https://introcs.cs.princeton.edu/java/91float/
            .setScale(2, RoundingMode.HALF_EVEN);
        this.billAmountLabel.setText(String.valueOf(this.billAmount + " " + this.currencySymbol));

        this.ordersSentCheckbox.setSelected(false);
    }
    
    private void orderDessert() {
        Plate selectedPlate = this.defaultDesserts.get(this.dessertsCombobox.getSelectedIndex());
        int dessertQuantityInput = Integer.parseInt(this.dessertsQuantityTextfield.getText());
        
        PlateOrder order = new PlateOrder(selectedPlate, dessertQuantityInput);
        this.ordersToSend.add(order);
        this.selectedTable.addOrder(order);
        this.ordersToSendStringArray.add(
            dessertQuantityInput + " " +
            ((Dessert)order.getPlate()).getCode() + ": " +
            order.getPlate().getLabel() + " (" +
            Float.valueOf(new DecimalFormat("#.##").format(
                dessertQuantityInput * order.getPlate().getPrice()))
                + " " + this.currencySymbol + ")");
        this.ordersToSendList.setListData(
            this.ordersToSendStringArray.toArray(new String[this.ordersToSendStringArray.size()]));
        
        this.dessertQuantity += dessertQuantityInput;
        this.billAmount = this.billAmount.add(
            new BigDecimal(dessertQuantityInput).multiply(
            new BigDecimal(Double.toString(order.getPlate().getPrice())))
            .setScale(2, RoundingMode.HALF_EVEN));
        this.billAmountLabel.setText(String.valueOf(this.billAmount + " " + this.currencySymbol));

        this.ordersSentCheckbox.setSelected(false);
    }
    
    private void dessertsOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dessertsOrderButtonActionPerformed
        int dessertQuantityInput = 0;
        try {
            dessertQuantityInput = Integer.parseInt(this.dessertsQuantityTextfield.getText());
        
            if (dessertQuantityInput <= 0) {
                JOptionPane.showMessageDialog(this,
                    "The dessert quantity must be positive",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
            
            this.orderDessert();
        
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "The dessert quantity must be a valid number",
                "Invalid quantity",
                JOptionPane.ERROR_MESSAGE); 
        }
    }//GEN-LAST:event_dessertsOrderButtonActionPerformed

    private void ordersSendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ordersSendButtonActionPerformed
        this.ordersToSendStringArray.forEach((plateToSend) -> {
            ((DefaultListModel<String>)this.servedPlatesList.getModel()).addElement((String)plateToSend);
        });

        for (PlateOrder order: ordersToSend) {
            this.servedPlates.add(order);
            // Effective sending to the other application (socket, IPC, whatever).
            System.out.println("Sending " + order.getQuantity() + "x " + order.getPlate().getLabel());
        }
        this.ordersToSendList.setModel(new DefaultListModel<>());
        this.ordersToSendStringArray.clear();
        this.ordersToSend.clear();

        this.ordersSentCheckbox.setSelected(true);
    }//GEN-LAST:event_ordersSendButtonActionPerformed

    private void tableComboboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tableComboboxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            this.selectedTable = this.tables.get(this.tableCombobox.getSelectedItem().toString());
            System.out.println("New table selected | number: " + this.selectedTable.getNumber() + " | waiter: " + this.selectedTable.getWaiterName());
        }
    }//GEN-LAST:event_tableComboboxItemStateChanged

    private void platesQuantityTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platesQuantityTextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_platesQuantityTextfieldActionPerformed

    private void drinksAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drinksAddButtonActionPerformed
        this.orderDrinks();
    }//GEN-LAST:event_drinksAddButtonActionPerformed

    private void billPayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billPayButtonActionPerformed
        BillGui billDialog = new BillGui(this, true);
        billDialog.setTable(this.tableCombobox.getSelectedItem().toString());
        billDialog.setBillAmount(this.billAmount);
        billDialog.setPlatesQuantity(this.platesQuantity);
        billDialog.setBillPaidState(this.billPaid);
        billDialog.setVisible(true);
        this.billPaid = billDialog.getPaymentDetails();
        if (billPaid) {
            this.billPaidStateLabel.setText("PAID");
        }
        billDialog.dispose();
    }//GEN-LAST:event_billPayButtonActionPerformed

    private void orderDrinks() {
        try {
            double drinksAmount = Double.parseDouble(this.drinksAmountTextfield.getText());

            if (drinksAmount <= 0) {
                JOptionPane.showMessageDialog(this,
                    "The drinks amount must be positive",
                    "Invalid drinks amount",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String drinkLine;
            if (this.platesQuantity > 0) {
                if (this.platesQuantity == 1) {
                    drinkLine = "Drinks with plate (" + drinksAmount + ")";
                } else {
                    drinkLine = "Drinks with plates (" + drinksAmount + ")";
                }
            } else {
                drinkLine = "Drinks without plate (" + drinksAmount + ")";
            }

            ((DefaultListModel<String>)this.servedPlatesList.getModel()).addElement(drinkLine);

            this.billAmount = this.billAmount.add(
                new BigDecimal(drinksAmount).setScale(2, RoundingMode.HALF_EVEN));
            this.billAmountLabel.setText(String.valueOf(
                this.billAmount + " " + this.currencySymbol));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "The drinks amount must be a valid number",
                "Invalid drinks amount",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel billAmountLabel;
    private javax.swing.JLabel billLabel;
    private javax.swing.JLabel billPaidStateLabel;
    private javax.swing.JButton billPayButton;
    private javax.swing.JComboBox<String> dessertsCombobox;
    private javax.swing.JLabel dessertsCommentsLabel;
    private javax.swing.JTextField dessertsCommentsTextfield;
    private javax.swing.JLabel dessertsLabel;
    private javax.swing.JToggleButton dessertsOrderButton;
    private javax.swing.JLabel dessertsQuantityLabel;
    private javax.swing.JTextField dessertsQuantityTextfield;
    private javax.swing.JButton drinksAddButton;
    private javax.swing.JTextField drinksAmountTextfield;
    private javax.swing.JLabel drinksLabel;
    private javax.swing.JLabel effectiveCoversLabel;
    private javax.swing.JLabel effectiveCoversValueLabel;
    private javax.swing.JLabel eurLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel maxCoversLabel;
    private javax.swing.JLabel maxCoversValueLabel;
    private javax.swing.JCheckBox ordersReadyCheckbox;
    private javax.swing.JButton ordersSendButton;
    private javax.swing.JCheckBox ordersSentCheckbox;
    private javax.swing.JLabel ordersToSendLabel;
    private javax.swing.JList<String> ordersToSendList;
    private javax.swing.JComboBox<String> platesCombobox;
    private javax.swing.JTextField platesCommentTextfield;
    private javax.swing.JLabel platesCommentsLabel;
    private javax.swing.JLabel platesLabel;
    private javax.swing.JButton platesOrderButton;
    private javax.swing.JLabel platesOrdersLabel;
    private javax.swing.JLabel platesQuantityLabel;
    private javax.swing.JTextField platesQuantityTextfield;
    private javax.swing.JButton readAvailablePlatesButton;
    private javax.swing.JLabel servedPlatesLabel;
    private javax.swing.JList<String> servedPlatesList;
    private javax.swing.JComboBox<String> tableCombobox;
    private javax.swing.JLabel tableLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.quitApplicationWithConfirm();
        } else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            if (ke.getSource() == this.platesQuantityTextfield) {
                this.orderPlate();
            } else if (ke.getSource() == this.dessertsQuantityTextfield) {
                this.orderDessert();
            } else if (ke.getSource() == this.drinksAmountTextfield) {
                this.orderDrinks();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}

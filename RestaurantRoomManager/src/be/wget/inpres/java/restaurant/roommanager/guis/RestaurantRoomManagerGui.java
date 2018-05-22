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
import be.wget.inpres.java.restaurant.dataobjects.Dessert;
import be.wget.inpres.java.restaurant.dataobjects.Drink;
import be.wget.inpres.java.restaurant.dataobjects.MainCourse;
import be.wget.inpres.java.restaurant.dataobjects.Plate;
import be.wget.inpres.java.restaurant.dataobjects.PlateCategory;
import be.wget.inpres.java.restaurant.dataobjects.PlateOrder;
import be.wget.inpres.java.restaurant.dataobjects.Table;
import be.wget.inpres.java.restaurant.myutils.StringSlicer;
import be.wget.inpres.java.restaurant.roommanager.TooManyCoversException;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import network.NetworkBasicClient;

/**
 *
 * @author wget
 */
public class RestaurantRoomManagerGui extends javax.swing.JFrame implements KeyListener {

    private final String currencySymbol = "EUR";

    // Hashtable is deprecated, let's use a Hashmap instead.
    private HashMap<String, String> credentials;
    private HashMap<String, Table> tables;
    private ArrayList<MainCourse> defaultMainCourses;
    private ArrayList<Dessert> defaultDesserts;
    private ArrayList<Table> savedTables;
    private ArrayList<PlateOrder> ordersToSend;
    private Table currentTable;
    private int effectiveCovers;
    private int selectedTableIndex;
    private BigDecimal billAmount;
    private DefaultListModel<String> ordersToSendListModel;
    private DefaultListModel<String> servedPlatesListModel;
    private RestaurantConfig applicationConfig;
    private NetworkBasicClient networkSender;
    private Icon applicationIcon;

    /**
     * Creates new form Main
     */
    public RestaurantRoomManagerGui() {

        initComponents();

        // Additional GUI customizations
        this.initAdditionalComponents();
        this.initUXEvents();

        this.populateData();
        this.initApplicationGui();
        
        this.authenticateUser();
    }
    
    private void initAdditionalComponents() {
        this.applicationConfig = new RestaurantConfig();

        try {
            this.applicationIcon = new ImageIcon(ImageIO.read(
                RestaurantRoomManagerGui.class.getResourceAsStream("/assets/appIcon.png")));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "The \"assets\" folder coudn't be found.",
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
        this.setTitle(this.applicationConfig.getRestaurantName());
        this.setLocationRelativeTo(null);
        this.ordersSentCheckbox.setEnabled(false);
        this.ordersReadyCheckbox.setEnabled(false);
        this.ordersToSendListModel = new DefaultListModel<>();
        this.ordersToSendList.setModel(this.ordersToSendListModel);
        this.servedPlatesListModel = new DefaultListModel<>();
        this.servedPlatesList.setModel(servedPlatesListModel);
        this.selectedTableIndex = 0;
        this.billAmount = new BigDecimal("0");
        this.effectiveCovers = 0;
        this.ordersToSendList.addKeyListener(this);
        this.startNetworkConnection();
        this.addWindowListener(new RoomManagerWindowAdapter(this));
    }

    class RoomManagerWindowAdapter extends WindowAdapter {

        RestaurantRoomManagerGui frame;

        public RoomManagerWindowAdapter(RestaurantRoomManagerGui frame) {
            super();
            this.frame = frame;
        }

        @Override
        public void windowClosing(WindowEvent event) {
            frame.exitApplication();
        }
    }
    
    private void populateData() {
        this.savedTables = new ArrayList<>();
        this.ordersToSend = new ArrayList<>();
        
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
        this.defaultMainCourses.add(new MainCourse("Gruyère farci aux rognons-téquila", 13.4, "GF"));
        this.defaultMainCourses.add(new MainCourse("Potée auvergnate au miel", 12.5, "PA"));
        this.defaultDesserts = new ArrayList<>();
        this.defaultDesserts.add(new Dessert("Mousse au chocolat salé", 5.35, "D_MC"));
        this.defaultDesserts.add(new Dessert("Sorbet citron courgette Colonel", 6.85, "D_SC"));
        this.defaultDesserts.add(new Dessert("Duo de crêpes Juliettes", 6, "D_CJ"));
        this.defaultDesserts.add(new Dessert("Dame grise", 5.55, "D_DG"));
        this.defaultDesserts.add(new Dessert("Crème très brulée carbonne", 7, "D_CB"));
    }
    
    private void initUXEvents() {
        this.billCheckoutButton.addKeyListener(this);
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
    
    private void initApplicationGui() {
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
            dessertNames.toArray(new String[dessertNames.size()])));
        
        this.currentTable = tables.get(this.tableCombobox.getSelectedItem().toString());
        this.maxCoversValueLabel.setText(String.valueOf(this.currentTable.getMaxCovers()));

        // The getSelection on combobox is only populated when there has been
        // previously a selection. By default, when the app is launched, while
        // the selected item is the first of the combobox, the app assumes
        // no selection has been made yet, which could lead to a crash.
        // Prevent this.
        this.tableCombobox.setSelectedIndex(0);
        this.platesCombobox.setSelectedIndex(0);
        this.dessertsCombobox.setSelectedIndex(0);
    }
    
    private void authenticateUser() {
        LoginGui login = new LoginGui(this, true);
        while (true) {
            login.setVisible(true);
            if (login.isDialogCancelled()) {
                System.exit(1);
            }

            String hashedPassword = this.credentials.get(login.getUsername());
            if (hashedPassword == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Unable to find the user \""
                        + login.getUsername()
                        + "\". Quitting...",
                    this.applicationConfig.getRestaurantName(),
                    JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

            if (!hashedPassword.equals(
                this.getSha512FromPassword(login.getPassword()))) {
                JOptionPane.showMessageDialog(
                    this,
                    "The password for the user \""
                        + login.getUsername()
                        + " \"is incorrect. Quitting...",
                    this.applicationConfig.getRestaurantName(),
                    JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            break;
        }
        this.currentTable.setWaiterName(login.getUsername());
        login.dispose();
        this.setTitle(this.applicationConfig.getRestaurantName()
            + ": " + this.currentTable.getWaiterName());
    }

    private String getSha512FromPassword(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());
            
            byte byteData[] = md.digest();
            
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
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
    
    private void startNetworkConnection() {
        this.networkSender = new NetworkBasicClient(
            this.applicationConfig.getServerAddress(),
            this.applicationConfig.getServerPort());
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
        billCheckoutButton = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        tableLabel.setText("Table:");

        tableCombobox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tableComboboxItemStateChanged(evt);
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

        billCheckoutButton.setText("Checkout");
        billCheckoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billCheckoutButtonActionPerformed(evt);
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
                            .addComponent(billCheckoutButton, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)))
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
                            .addComponent(billCheckoutButton)
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

    private void platesOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platesOrderButtonActionPerformed
        this.orderPlate();
    }//GEN-LAST:event_platesOrderButtonActionPerformed

    private void orderPlate() {
        Plate selectedPlate = this.defaultMainCourses.get(
            this.platesCombobox.getSelectedIndex());
        int plateQuantityInput = 0;

        try {
            plateQuantityInput = Integer.parseInt(
                this.platesQuantityTextfield.getText());
        
            if (plateQuantityInput <= 0) {
                JOptionPane.showMessageDialog(this,
                    "The plate quantity must be positive",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }

            if (this.effectiveCovers
                + plateQuantityInput > this.currentTable.getMaxCovers()) {
                throw new TooManyCoversException(
                    plateQuantityInput,
                    this.currentTable.getMaxCovers());
            }
            
            this.orderCurrentPlate(selectedPlate, plateQuantityInput);
        
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "The plate quantity must be a valid number",
                "Invalid quantity",
                JOptionPane.ERROR_MESSAGE); 
        } catch (TooManyCoversException ex) {
            String noButtonText;
            String yesButtonText;
            String questionText;
            switch (this.effectiveCovers) {
                case 0:
                    noButtonText = "No, keep no cover";
                    break;
                case 1:
                    noButtonText = "No, keep my existing cover";
                    break;
                default:
                    noButtonText = "No, keep my existing " +
                        this.effectiveCovers + " covers";
                    break;
            }
            if (plateQuantityInput == 1) {
                questionText = "Do you really want to add one additional cover?";
                yesButtonText = "Yes, add one additional cover";
            } else {
                questionText = "Do you really want to add "
                    + plateQuantityInput + " additional covers?";
                yesButtonText = "Yes, add "
                    + plateQuantityInput + " additional covers";
            }

            Object[] options = {
                yesButtonText,
                noButtonText
            };
            int answer = JOptionPane.showOptionDialog(
                this,
                questionText,
                "Too much covers",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                // Do not use a custom Icon.
                null,
                // Specifies the titles of the buttons.
                options,
                // Use default button title.
                options[0]);
            
            // Cancel everything
            if (answer == JOptionPane.CLOSED_OPTION ||
                // Yes, we needed to revert the sense of the YES_OPTION, because
                // we really wanted to have the No statement at the left, which
                // is Yes by default on Swing.
                answer == JOptionPane.NO_OPTION) {
                return;
            }

            this.orderCurrentPlate(selectedPlate, plateQuantityInput);
        }
    }

    private void orderCurrentPlate(Plate plate, int quantity) {
        // Update model
        int i = 0;
        Boolean itemChanged = false;
        PlateOrder order = null;
        for (; i < this.ordersToSend.size(); i++) {
            order = this.ordersToSend.get(i);
            if (order.getPlate() instanceof MainCourse &&
                plate instanceof MainCourse) {
                if (((MainCourse)order.getPlate()).getCode().equals(
                    ((MainCourse)plate).getCode())) {
                    order.addQuantity(quantity);
                    this.ordersToSend.set(i, order);
                    itemChanged = true;
                    break;
                }
            } else if (order.getPlate() instanceof Dessert &&
                       plate instanceof Dessert) {
                if (((Dessert)order.getPlate()).getCode().equals(
                    ((Dessert)plate).getCode())) {
                    order.addQuantity(quantity);
                    this.ordersToSend.set(i, order);
                    itemChanged = true;
                    break;
                }
            }
        }

        if (!itemChanged) {
            order = new PlateOrder(plate, quantity);
            this.ordersToSend.add(order);
        }

        if (plate instanceof MainCourse) {
            this.effectiveCovers += quantity;
        }
        this.billAmount = this.billAmount.add(
            new BigDecimal(plate.getPrice())
            .multiply(new BigDecimal(quantity))
            .setScale(2, RoundingMode.HALF_EVEN));

        // Update UI
        String plateCode;
        String plateLabel;
        if (plate instanceof MainCourse) {
            plateCode = ((MainCourse)order.getPlate()).getCode();
            plateLabel = ((MainCourse)order.getPlate()).getLabel();
        } else {
            plateCode = ((Dessert)order.getPlate()).getCode();
            plateLabel = ((Dessert)order.getPlate()).getLabel();
        }
        String orderLine = order.getQuantity() + " " +
            plateCode + ": " +
            plateLabel + " (" +
            order.getPrice().toString() + " " +
            this.currencySymbol + ")";
        if (!itemChanged) {
            this.ordersToSendListModel.addElement(orderLine);
        } else {
            this.ordersToSendListModel.setElementAt(orderLine, i);
        }

        if (plate instanceof MainCourse) {
            this.effectiveCoversValueLabel.setText(
                String.valueOf(this.effectiveCovers));
        }
        this.billAmountLabel.setText(
            String.valueOf(this.billAmount + " " + this.currencySymbol));
    }
    
    private void orderDessert() {
        Plate dessert;
        int dessertQuantity = 0;
        try {
            dessert = this.defaultDesserts.get(
                this.dessertsCombobox.getSelectedIndex());
            dessertQuantity = Integer.parseInt(
                this.dessertsQuantityTextfield.getText());
        
            if (dessertQuantity <= 0) {
                JOptionPane.showMessageDialog(this,
                    "The dessert quantity must be positive",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
            
            this.orderCurrentPlate(dessert, dessertQuantity);
        
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "The dessert quantity must be a valid number",
                "Invalid quantity",
                JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void removeFromOrdersToSend() {
        int selectedOrder = this.ordersToSendList.getSelectedIndex();
        if (this.ordersToSend.isEmpty()) {
            return;
        }
        PlateOrder order = this.ordersToSend.get(selectedOrder);
        boolean isRemovalRequired = false;

        if (order.getQuantity() == 1) {
            isRemovalRequired = true;
        }

        // Update model
        order.removeQuantity();
        double unitPrice = 0;
        if (order.getPlate() instanceof MainCourse) {
            unitPrice = ((MainCourse)order.getPlate()).getPrice();
            this.effectiveCovers--;
        } else if (order.getPlate() instanceof Dessert) {
            unitPrice = ((Dessert)order.getPlate()).getPrice();
        }
        this.billAmount = this.billAmount.subtract(
            new BigDecimal(unitPrice)
            .setScale(2, RoundingMode.HALF_EVEN));

        if (isRemovalRequired) {
            this.ordersToSend.remove(selectedOrder);
        } else {
            this.ordersToSend.set(selectedOrder, order);
        }

        // Update UI
        this.billAmountLabel.setText(
            String.valueOf(this.billAmount + " " + this.currencySymbol));

        if (isRemovalRequired) {
            ((DefaultListModel)this.ordersToSendList.getModel())
                .remove(selectedOrder);
            return;
        }

        String plateCode;
        String plateLabel;
        if (order.getPlate() instanceof MainCourse) {
            plateCode = ((MainCourse)order.getPlate()).getCode();
            plateLabel = ((MainCourse)order.getPlate()).getLabel();
            this.effectiveCoversValueLabel.setText(
                String.valueOf(this.effectiveCovers));
        } else {
            plateCode = ((Dessert)order.getPlate()).getCode();
            plateLabel = ((Dessert)order.getPlate()).getLabel();
        }
        String orderLine = order.getQuantity() + " " +
            plateCode + ": " +
            plateLabel + " (" +
            order.getPrice().toString() + " " +
            this.currencySymbol + ")";
        ((DefaultListModel)this.ordersToSendList.getModel())
            .setElementAt(orderLine, selectedOrder);
    }

    private void dessertsOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dessertsOrderButtonActionPerformed
        this.orderDessert();
    }//GEN-LAST:event_dessertsOrderButtonActionPerformed

    private void ordersSendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ordersSendButtonActionPerformed

        // Effective sending to the other application (socket, IPC, whatever).
        StringBuilder ordersToServer = new StringBuilder();
        DateFormat df = DateFormat.getDateTimeInstance(
        DateFormat.SHORT, DateFormat.SHORT);
        String orderTime = df.format(new Date());
        for (PlateOrder order: this.ordersToSend) {
            ordersToServer.append(order.getQuantity());
            ordersToServer.append(this.applicationConfig.getOrderFieldDelimiter());
            if (order.getPlate() instanceof MainCourse) {
                ordersToServer.append(((MainCourse)order.getPlate()).getCode());
            } else {
                ordersToServer.append(((Dessert)order.getPlate()).getCode());
            }
            ordersToServer.append(this.applicationConfig.getOrderFieldDelimiter());
            ordersToServer.append(this.currentTable.getNumber());
            ordersToServer.append(this.applicationConfig.getOrderFieldDelimiter());
            ordersToServer.append(orderTime);
            ordersToServer.append(this.applicationConfig.getOrderLineDelimiter());
        }

        boolean retryConnection = true;
        String networkAnswer;

        while (retryConnection) {
            retryConnection = false;

            networkAnswer = this.networkSender.sendString(ordersToServer.toString());

            if (networkAnswer == null) {
                Object[] options = {
                    "Yes, retry the connection",
                    "No, cancel"
                };
                int answer = JOptionPane.showOptionDialog(
                    this,
                    "The connection couldn't be established with the kitchen "
                        + "manager. Do you want to retry establishing the "
                        + "connection?",
                    "No network connection",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    // Do not use a custom Icon.
                    null,
                    // Specifies the titles of the buttons.
                    options,
                    // Use default button title.
                    options[0]);
                if (answer == JOptionPane.CLOSED_OPTION ||
                    answer == JOptionPane.NO_OPTION) {
                    return;
                }
                retryConnection = true;
                this.startNetworkConnection();
                continue;
            }

            if (!networkAnswer.equals(
                this.applicationConfig.getKitchenOrderAcceptedPayload())) {

                StringSlicer parsedAnswer = new StringSlicer(
                    networkAnswer,
                    this.applicationConfig.getOrderFieldDelimiter());
                ArrayList<String> answerFields = parsedAnswer.listComponents();
                if (!answerFields.get(0).equals(
                    this.applicationConfig.getKitchenOrderDeclinedPayload())) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Something nasty could happen on the network as "
                            + "answers for refused orders must contain \""
                            + this.applicationConfig.getKitchenOrderDeclinedPayload()
                            + "\". We received \"" + answerFields.get(0)
                            + "\" instead.",
                        "Invalid answer detected",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                OrderDeclinedReasonGui orderDeclinedGui = new OrderDeclinedReasonGui(
                    this, answerFields.get(1));
                orderDeclinedGui.setVisible(true);
                return;
            }

            JOptionPane.showMessageDialog(
                this,
                "The order has been received properly by the kitchen.",
                "Order received",
                JOptionPane.INFORMATION_MESSAGE);

            this.ordersSentCheckbox.setEnabled(true);
        }

        // Update UI
        for (int i = 0; i < this.ordersToSendListModel.size(); i++) {
            this.servedPlatesListModel.addElement(ordersToSendListModel.get(i));
        }
        this.ordersToSendListModel.clear();

        // Update model
        this.currentTable.setEffectiveCovers(this.effectiveCovers);
        for (PlateOrder order: this.ordersToSend) {
            this.currentTable.addOrder(order);
            this.billAmount.add(order.getPrice());
        }
        this.ordersToSend.clear();
        this.currentTable.setBillAmount(Double.parseDouble(this.billAmount.toString()));

        // Update the type of drinks depending if there are other plates sent
        // or not. Applies to the UI (In the served plates list) and in the
        // in-memory model.
        String drinkLine;
        if (this.currentTable.getEffectiveCovers() == 1) {
            drinkLine = "Drinks with plate";
        } else if (this.currentTable.getEffectiveCovers() > 1) {
            drinkLine = "Drinks with plates";
        } else {
            drinkLine = "Drinks without plate";
        }
        ArrayList<PlateOrder> orders = this.currentTable.getOrders();
        for (int i = 0; i < orders.size(); i++) {
            PlateOrder plateOrder = orders.get(i);
            if (plateOrder.getPlate() instanceof Drink) {
                plateOrder.getPlate().setLabel(drinkLine);
                this.servedPlatesListModel.set(i,
                    plateOrder.getQuantity() + " " +
                        plateOrder.getPlate().getLabel() + " (" +
                        plateOrder.getPlate().getPrice() + " " +
                    this.currencySymbol + ")");
            }
        }
    }//GEN-LAST:event_ordersSendButtonActionPerformed

    private void tableComboboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tableComboboxItemStateChanged
        if (evt.getStateChange() != ItemEvent.SELECTED ||
            evt.getSource() != this.tableCombobox) {
            return;
        }

        // If this is the same table (because the user has cancelled the
        // table switch, there is no need to repopulate in-memory objects.
        if (this.tableCombobox.getSelectedIndex() == this.selectedTableIndex) {
            return;
        }

        this.saveCurrentTable();

        // Ask the user if he wants to change the current waiter
        WaiterChangeQuestionGui waiterChangeGui =
            new WaiterChangeQuestionGui(this, true);
        waiterChangeGui.setWaiterName(this.currentTable.getWaiterName());
        waiterChangeGui.setVisible(true);

        // Used to backup the new waiter name further in the code, otherwise
        // we will lose access to it after the table has switched.
        // Saving the current waiter name here is also needed in order to save
        // the current waiter on table for which no plate has been sent.
        String newWaiterName = this.currentTable.getWaiterName();

        if (waiterChangeGui.isWaiterChanging()) {

            LoginGui login = new LoginGui(this, true);
            while (true) {
                login.setVisible(true);
                if (login.isDialogCancelled()) {
                    break;
                }

                String hashedPassword = this.credentials.get(login.getUsername());
                if (hashedPassword == null) {
                    JOptionPane.showMessageDialog(getParent(),
                        "Unable to find the user \""
                        + login.getUsername()
                        + "\". Retry.",
                        this.applicationConfig.getRestaurantName(),
                        JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                if (!hashedPassword.equals(this.getSha512FromPassword(login.getPassword()))) {
                    JOptionPane.showMessageDialog(getParent(),
                        "The password for the user \""
                        + login.getUsername()
                        + " \"is incorrect. Retry.",
                        this.applicationConfig.getRestaurantName(),
                        JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                // Change the waiter
                newWaiterName = login.getUsername();
                this.currentTable.setWaiterName(login.getUsername());
                this.setTitle(this.applicationConfig.getRestaurantName()
                    + ": " + this.currentTable.getWaiterName());

                login.dispose();
                break;
            }
        }
        waiterChangeGui.dispose();

        // Recover the previous state of the table if any
        String newTableNumber = this.tableCombobox.getSelectedItem().toString();
        boolean tableFound = false;
        int tableIndex = 0;
        while (tableIndex < this.savedTables.size()) {
            if (newTableNumber.equals(this.savedTables.get(tableIndex).getNumber())) {
                tableFound = true;
                break;
            }
            tableIndex++;
        }
        if (tableFound) {
            this.currentTable = this.savedTables.get(tableIndex);
        } else {
            this.currentTable = this.tables.get(newTableNumber);
        }

        // Update model with new table info
        this.currentTable.setWaiterName(newWaiterName);
        this.selectedTableIndex = this.tableCombobox.getSelectedIndex();
        this.billAmount = new BigDecimal(
            String.valueOf(this.currentTable.getBillAmount()));

        // Update UI
        // Clean GUI of values from previous table
        this.ordersToSendListModel.clear();
        this.ordersToSend.clear();
        this.servedPlatesListModel.clear();
        this.platesQuantityTextfield.setText("X");
        this.dessertsQuantityTextfield.setText("X");
        this.drinksAmountTextfield.setText("X");
        this.effectiveCovers = 0;
        // Populate UI with the values of the new table
        this.maxCoversValueLabel.setText(String.valueOf(this.currentTable.getMaxCovers()));
        this.effectiveCoversValueLabel.setText(String.valueOf(this.currentTable.getEffectiveCovers()));
        for (PlateOrder order: this.currentTable.getOrders()) {
            if (order.getPlate() instanceof MainCourse ||
                order.getPlate() instanceof Dessert) {
                String code;
                if (order.getPlate() instanceof MainCourse) {
                    code = ((MainCourse)order.getPlate()).getCode();
                    // Should have been above in the code structure we have,
                    // but avoid an additional log(n) cost by browsing the
                    // orders a second time for barely nothing.
                    this.effectiveCovers++;
                } else {
                    code = ((Dessert)order.getPlate()).getCode();
                }

                this.servedPlatesListModel.addElement(
                    order.getQuantity() + " " +
                    code + ": " +
                    order.getPlate().getLabel() + " (" +
                    order.getPrice().toString() + " " +
                    this.currencySymbol + ")"
                );
            } else {
                this.servedPlatesListModel.addElement(
                    order.getQuantity() + " " +
                    order.getPlate().getLabel() + " (" +
                    order.getPrice().toString() + " " +
                    this.currencySymbol + ")"
                );
            }
        }
        this.billAmountLabel.setText(String.valueOf(this.currentTable.getBillAmount()));
        if (this.currentTable.isBillPaid()) {
            this.billPaidStateLabel.setText("PAID");
        } else {
            this.billPaidStateLabel.setText("NOT PAID");
        }
    }//GEN-LAST:event_tableComboboxItemStateChanged

    private void saveCurrentTable() {
        if (this.ordersToSend.size() > 0) {
            Object[] options = {
                "Yes, change table and loose my non sent orders",
                "No, cancel"
            };
            String remainingToSend;
            if (this.ordersToSend.size() == 1) {
                remainingToSend = "There is one order that hasn't been sent yet."
                    + " Changing table will loose them. "
                    + "Do you want to change table?";
            } else {
                remainingToSend = "There are " + this.ordersToSend.size()
                    + " orders that haven't been sent yet. Changing table will "
                    + "loose them. Do you want to change table?";
            }

            int answer = JOptionPane.showOptionDialog(
                this,
                remainingToSend,
                "Orders not sent",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                // Do not use a custom Icon.
                null,
                // Specifies the titles of the buttons.
                options,
                // Use default button title.
                options[1]);

            // Cancel the table switch
            if (answer == JOptionPane.CLOSED_OPTION ||
                // If you are not using GTK, don't forget the YES and NO
                // constant are inverted because we really wanted to have the
                // No statement at the left while Swing uses Yes at the right.
                // We changed back to NO here as GTK should be enabled.
                answer == JOptionPane.NO_OPTION) {

                // Reselect previous table in the combobox item.
                // This has as effect to retrigger this ItemEvent.
                this.tableCombobox.setSelectedIndex(this.selectedTableIndex);
                return;
            }
        }

        // Recompute the bill amount in order to discard non delivered orders
        this.billAmount = new BigDecimal(0);
        for(PlateOrder order: this.currentTable.getOrders()) {
            this.billAmount = this.billAmount.add(order.getPrice());
        }
        this.currentTable.setBillAmount(Double.parseDouble(this.billAmount.toString()));

        // Save the current table
        boolean tableFound = false;
        int tableIndex = 0;
        while (tableIndex < this.savedTables.size()) {
            if (this.currentTable.getNumber().equals(
                this.savedTables.get(tableIndex).getNumber())) {
                tableFound = true;
                break;
            }
            tableIndex++;
        }
        if (tableFound) {
            this.savedTables.set(tableIndex, this.currentTable);
        } else {
            this.savedTables.add(this.currentTable);
        }
    }

    private void exitApplication() {
        if (this.networkSender != null) {
            this.networkSender.setEndSending();
        }
        this.dispose();
        System.out.println("Application exited.");
    }

    private void platesQuantityTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platesQuantityTextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_platesQuantityTextfieldActionPerformed

    private void drinksAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drinksAddButtonActionPerformed
        this.orderDrinks();
    }//GEN-LAST:event_drinksAddButtonActionPerformed

    private void billCheckoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billCheckoutButtonActionPerformed
        BillGui billDialog = new BillGui(this, true);
        billDialog.setTable(this.tableCombobox.getSelectedItem().toString());
        billDialog.setBillAmount(this.billAmount);
        billDialog.setPlatesQuantity(this.currentTable.getEffectiveCovers());
        billDialog.setBillPaidState(this.currentTable.isBillPaid());
        billDialog.setVisible(true);
        if (billDialog.getPaymentDetails()) {
            this.currentTable.setBillPaid();
            this.billPaidStateLabel.setText("PAID");
        }
        billDialog.dispose();
    }//GEN-LAST:event_billCheckoutButtonActionPerformed

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
            if (this.currentTable.getEffectiveCovers() == 1) {
                drinkLine = "Drinks with plate";
            } else if (this.currentTable.getEffectiveCovers() > 1) {
                drinkLine = "Drinks with plates";
            } else {
                drinkLine = "Drinks without plate";
            }

            // Update model
            Drink drink = new Drink(drinkLine, PlateCategory.DRINK, drinksAmount);
            PlateOrder drinkOrder = new PlateOrder(drink, 1);
            this.currentTable.addOrder(drinkOrder);
            this.billAmount = this.billAmount.add(drinkOrder.getPrice());

            // Update UI
            this.servedPlatesListModel.addElement(
                drinkOrder.getQuantity() + " " +
                drink.getLabel() + " (" +
                drink.getPrice() + " " +
                this.currencySymbol + ")");
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
    private javax.swing.JButton billCheckoutButton;
    private javax.swing.JLabel billLabel;
    private javax.swing.JLabel billPaidStateLabel;
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
            return;
        }

        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            if (ke.getSource() == this.platesQuantityTextfield) {
                this.orderPlate();
            } else if (ke.getSource() == this.dessertsQuantityTextfield) {
                this.orderDessert();
            } else if (ke.getSource() == this.drinksAmountTextfield) {
                this.orderDrinks();
            }
            return;
        }

        if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
            this.removeFromOrdersToSend();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
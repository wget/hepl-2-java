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
import be.wget.inpres.java.restaurant.fileserializer.DefaultDessertsSerializer;
import be.wget.inpres.java.restaurant.fileserializer.DefaultMainCoursesSerializer;
import be.wget.inpres.java.restaurant.fileserializer.TablesSerializer;
import be.wget.inpres.java.restaurant.myutils.StringSlicer;
import be.wget.inpres.java.restaurant.orderprotocol.NetworkProtocolOrderSender;
import be.wget.inpres.java.restaurant.roommanager.OrderReadyNotifyThread;
import be.wget.inpres.java.restaurant.roommanager.TooManyCoversException;
import be.wget.inpres.java.restaurant.usersmanager.UsersManager;
import be.wget.inpres.java.restaurant.usersmanager.UsersManagerLoginDialogCancelled;
import be.wget.inpres.java.restaurant.usersmanager.UsersManagerPasswordInvalidException;
import be.wget.inpres.java.restaurant.usersmanager.UsersManagerSerializationException;
import be.wget.inpres.java.restaurant.usersmanager.UsersManagerUserNotFoundException;
import be.wget.inpres.java.restaurant.usersmanager.guis.AddNewUserGui;
import be.wget.inpres.java.restaurant.usersmanager.guis.ModifyPasswordGui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import network.NetworkBasicClient;
import network.NetworkBasicServer;

/**
 *
 * @author wget
 */
@SuppressWarnings("serial")
public class RestaurantRoomManagerGui
    extends JFrame
    implements KeyListener,
               ActionListener {

    private final String currencySymbol = "EUR";

    // Hashtable is deprecated, let's use a Hashmap instead.
    private HashMap<String, Table> tables;
    private ArrayList<MainCourse> defaultMainCourses;
    private ArrayList<Dessert> defaultDesserts;
    private ArrayList<PlateOrder> ordersReady;
    private Table currentTable;
    private int selectedTableIndex;
    private DefaultListModel<String> ordersToSendListModel;
    private DefaultListModel<String> servedPlatesListModel;
    private RestaurantConfig applicationConfig;
    private NetworkBasicClient networkSender;
    private NetworkBasicServer networkReceiver;
    private OrderReadyNotifyThread orderReadyNotifyThread;
    private Icon applicationIcon;
    private UsersManager usersManager;

    private JMenu settingsMenu;
    private JMenu helpMenu;
    private JMenuItem systemInfosMenuItem;
    private JMenuItem dateTimeSettingsMenuItem;
    private JMenuItem beginnerGuideMenuItem;
    private JMenuItem aboutMenuItem;

    /**
     * Creates new form Main
     */
    public RestaurantRoomManagerGui() {

        initComponents();

        // Additional GUI customizations
        this.initAdditionalComponents();

        this.initApplicationGui();
        
        // Init network
        this.startNetworkClientConnection();
        this.startNetworkServerTrapConnection();
        this.startOrderReadyNotifyThread();
        
        this.authenticateUser();
    }

    private void authenticateUser() {

        try {
            this.usersManager = new UsersManager(applicationConfig, this);
            String username = "";
            username = this.usersManager.authenticateUser();
            this.currentTable.setWaiterName(username);
            this.setTitle(this.applicationConfig.getRestaurantName()
                + ": " + this.currentTable.getWaiterName());
        } catch (UsersManagerUserNotFoundException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to find the user \""
                    + ex.getUsername()
                    + "\". Quitting...",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.ERROR_MESSAGE);
            this.exitApplication();
        } catch (UsersManagerPasswordInvalidException ex) {
            JOptionPane.showMessageDialog(
                this,
                "The password for the user \""
                    + ex.getUsername()
                    + "\" is incorrect. Quitting...",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.ERROR_MESSAGE);
            this.exitApplication();
        } catch (UsersManagerLoginDialogCancelled ex) {
            this.exitApplication();
        } catch (UsersManagerSerializationException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Users manager serialization impossible. Quitting...",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.ERROR_MESSAGE);
            this.exitApplication();            
        }
    }
    
    private void initAdditionalComponents() {
        this.applicationConfig = new RestaurantConfig();

        try {
            this.applicationIcon = new ImageIcon(ImageIO.read(
                RestaurantRoomManagerGui.class.getResourceAsStream(
                    "/be/wget/inpres/java/restaurant/assets/appIcon.png")));
        } catch (IOException ex) {
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
                    + this.applicationConfig.getSettingsFilename().substring(
                        this.applicationConfig.getSettingsFilename()
                        .lastIndexOf(File.separator)+1)
                    + "\". Using the defaults settings.",
                this.applicationConfig.getRestaurantName(),
                JOptionPane.WARNING_MESSAGE);
        }
        
        try {
            this.applicationConfig.saveFile();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to save the settings file to \""
                    + this.applicationConfig.getSettingsFilename()
                    + "\". Changing settings during this session might be lost "
                    + "when you will close the app.",
                "Serialization impossible",
                JOptionPane.WARNING_MESSAGE);
        }

        // Init model
        this.ordersToSendListModel = new DefaultListModel<>();
        this.servedPlatesListModel = new DefaultListModel<>();
        this.ordersReady = new ArrayList<>();

        this.settingsMenu = new JMenu("Settings");
        this.systemInfosMenuItem = new JMenuItem("System infos");
        this.dateTimeSettingsMenuItem = new JMenuItem("Date-time settings");
        this.helpMenu = new JMenu("Help");
        this.beginnerGuideMenuItem = new JMenuItem("Beginner guide");
        this.aboutMenuItem = new JMenuItem("About this application");

        this.ordersToSendList.setModel(this.ordersToSendListModel);
        this.servedPlatesList.setModel(this.servedPlatesListModel);
        this.selectedTableIndex = 0;
        this.ordersToSendList.addKeyListener(this);

        TablesSerializer serializer = new TablesSerializer(
            this.applicationConfig,
            this.tables);
        this.tables = serializer.loadTables();
        
        this.serializeTables();
        
        // Populate list of plates
        DefaultMainCoursesSerializer mainCoursesSerializer =
            new DefaultMainCoursesSerializer(
                this.applicationConfig,
                this.applicationConfig.getPlatesFilename());
        this.defaultMainCourses = mainCoursesSerializer.getDefaultPlates();
        try {
            mainCoursesSerializer.saveFile();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "An issue occurred when trying to save the file with the main courses",
                "Serialization impossible",
                JOptionPane.ERROR_MESSAGE); 
        }
        
        this.defaultDesserts = new DefaultDessertsSerializer(
            this.applicationConfig,
            "")
                .getDefaultPlates();

        // Init UI
        this.setTitle(this.applicationConfig.getRestaurantName());
        this.setLocationRelativeTo(null);
        this.ordersSentCheckbox.setEnabled(false);
        this.ordersReadyCheckbox.setEnabled(false);
        this.billCheckoutButton.setEnabled(false);

        // Menu
        // Add space to align the remaining parts of the menu to the right.
        this.menuBar.add(Box.createHorizontalGlue());
        this.menuBar.add(this.settingsMenu);
        this.menuBar.add(this.helpMenu);
        this.settingsMenu.add(this.systemInfosMenuItem);
        this.settingsMenu.add(this.dateTimeSettingsMenuItem);
        this.helpMenu.add(this.beginnerGuideMenuItem);
        this.helpMenu.add(this.aboutMenuItem);

        // Init UX listeners
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
        this.addWindowListener(new RoomManagerWindowAdapter(this));
        
        this.listTablesMenuItem.addActionListener(this);
        this.totalNumberClientsMenuItem.addActionListener(this);
        this.totalBillsMenuItem.addActionListener(this);
        
        this.listPlatesMenuItem.addActionListener(this);
        this.listDessertsMenuItem.addActionListener(this);
        this.createPlateMenuItem.addActionListener(this);
        this.removePlateMenuItem.addActionListener(this);
        
        this.systemInfosMenuItem.addActionListener(this);
        this.dateTimeSettingsMenuItem.addActionListener(this);
        this.beginnerGuideMenuItem.addActionListener(this);
        this.aboutMenuItem.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == this.listTablesMenuItem) {
            ListTablesGui listTablesGui = new ListTablesGui(this, this.tables);
            listTablesGui.setLocationRelativeTo(this);
            listTablesGui.setVisible(true);
            listTablesGui.dispose();
            return;
        }

        if (e.getSource() == this.totalNumberClientsMenuItem) {
            TotalNumberClientsGui totalNumberClientsGui =
                new TotalNumberClientsGui(this, this.tables);
            totalNumberClientsGui.setLocationRelativeTo(this);
            totalNumberClientsGui.setVisible(true);
            totalNumberClientsGui.dispose();
            return;
        }

        if (e.getSource() == this.totalBillsMenuItem) {
            TotalBillsGui totalBills =
                new TotalBillsGui(this, this.tables, this.currencySymbol);
            totalBills.setLocationRelativeTo(this);
            totalBills.setVisible(true);
            totalBills.dispose();
            return;
        }

        if (e.getSource() == this.listPlatesMenuItem) {
            ListPlatesGui listPlatesGui =
                new ListPlatesGui(this, this.defaultMainCourses, this.currencySymbol);
            listPlatesGui.setLocationRelativeTo(this);
            listPlatesGui.setVisible(true);
            listPlatesGui.dispose();
            return;
        }

        if (e.getSource() == this.listDessertsMenuItem) {
            ListDessertsGui listDessertsGui =
                new ListDessertsGui(this, this.defaultDesserts, this.currencySymbol);
            listDessertsGui.setLocationRelativeTo(this);
            listDessertsGui.setVisible(true);
            listDessertsGui.dispose();
            return;
        }
        
        if (e.getSource() == this.createPlateMenuItem) {
            AddNewPlateGui addNewPlateGui =
                new AddNewPlateGui(this, this.applicationConfig, this.defaultMainCourses);
            addNewPlateGui.setLocationRelativeTo(this);
            addNewPlateGui.setVisible(true);
            addNewPlateGui.dispose();
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
                    "An issue occurred when trying to save the file with the "
                        + "main courses. The new plate you added might get be "
                        + "lost when you will close this application.",
                    "Serialization impossible",
                    JOptionPane.WARNING_MESSAGE); 
            }
            
            // Add the new plate to the combobox
            this.updateMainCourseComboboxUi();
            return;
        }
        
        if (e.getSource() == this.removePlateMenuItem) {
            RemovePlateGui removePlateGui =
                new RemovePlateGui(this, this.applicationConfig,
                    this.defaultMainCourses, this.currencySymbol);
            removePlateGui.setLocationRelativeTo(this);
            removePlateGui.setVisible(true);
            removePlateGui.dispose();

            // Add the new plate to the combobox
            this.updateMainCourseComboboxUi();
            return;
        }

        if (e.getSource() == this.systemInfosMenuItem) {
            SystemInfosGui systemInfoGui = new SystemInfosGui(this, true);
            systemInfoGui.setLocationRelativeTo(this);
            systemInfoGui.setVisible(true);
            systemInfoGui.dispose();
            return;
        }

        if (e.getSource() == this.dateTimeSettingsMenuItem) {
            DateSettingsGui dateSettingsGui = new DateSettingsGui(
                this, this.applicationConfig);
            dateSettingsGui.setLocationRelativeTo(this);
            dateSettingsGui.setVisible(true);
            try {
                this.applicationConfig.setDateFormat(dateSettingsGui.getSelectedDateFormat());
                this.applicationConfig.setLocale(dateSettingsGui.getSelectedLocale());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Unable to save the settings file to \""
                        + this.applicationConfig.getSettingsFilename()
                        + "\". Changing settings during this session might be lost "
                        + "when you will close the app.",
                    "Serialization impossible",
                    JOptionPane.WARNING_MESSAGE);
            }
            dateSettingsGui.dispose();
            return;
        }

        if (e.getSource() == this.aboutMenuItem) {
            AboutGui aboutGui = new AboutGui(
                this,
                this.applicationConfig);
            aboutGui.setLocationRelativeTo(this);
            aboutGui.setVisible(true);
            aboutGui.dispose();
            return;
        }

        if (e.getSource() == this.beginnerGuideMenuItem) {
            BeginnerGuideGui beginnerGuide = new BeginnerGuideGui(
                this,
                this.applicationConfig);
            beginnerGuide.setLocationRelativeTo(this);
            beginnerGuide.setVisible(true);
            beginnerGuide.dispose();
            return;
        }
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
    
    private void updateUiFromCurrentTable() {
        
        // Merge orders if their state is identical
        this.currentTable.bulkCompress();
        
        // Clean GUI of values from previous table
        this.ordersToSendListModel.clear();
        this.servedPlatesListModel.clear();
        this.platesQuantityTextfield.setText("X");
        this.dessertsQuantityTextfield.setText("X");
        this.drinksAmountTextfield.setText("X");

        // Populate UI with the values of the new table
        this.maxCoversValueLabel.setText(
            String.valueOf(this.currentTable.getMaxCovers()));
        this.effectiveCoversValueLabel.setText(
            String.valueOf(this.currentTable.getEffectiveCovers()));
        
        for (PlateOrder order: this.currentTable.getOrders()) {
            String plateCode = "";
            if (order.getPlate() instanceof MainCourse) {
                plateCode = ((MainCourse)order.getPlate()).getCode();
            } else if (order.getPlate() instanceof Dessert) {
                plateCode = ((Dessert)order.getPlate()).getCode();
            }
            
            String orderLine;
            // This is a drink
            if (plateCode.isEmpty()) {
                orderLine = order.getQuantity() + " " +
                    order.getPlate().getLabel() + " (" +
                    order.getPrice().toString() + " " +
                    this.currencySymbol + ")";
            // This is a plate (main course or dessert)
            } else {
                orderLine = order.getQuantity() + " " +
                    plateCode + ": " +
                    order.getPlate().getLabel() + " (" +
                    order.getPrice().toString() + " " +
                    this.currencySymbol + ")";
            }
            
            // If this is a plate sent or a drink (no code by default)
            if (order.isSent() || plateCode.isEmpty()) {
                this.servedPlatesListModel.addElement(orderLine);
                this.billCheckoutButton.setEnabled(true);
            } else {
                this.ordersToSendListModel.addElement(orderLine);
            }
        }

        this.billAmountLabel.setText(
            this.currentTable.getBillAmount() +
            " " +
            this.currencySymbol);
        if (this.currentTable.isBillPaid()) {
            this.billPaidStateLabel.setText("PAID");
            this.ordersSendButton.setEnabled(false);
            this.platesOrderButton.setEnabled(false);
            this.dessertsOrderButton.setEnabled(false);
            this.drinksAddButton.setEnabled(false);
        } else {
            this.billPaidStateLabel.setText("NOT PAID");
            this.ordersSendButton.setEnabled(true);
            this.platesOrderButton.setEnabled(true);
            this.dessertsOrderButton.setEnabled(true);
            this.drinksAddButton.setEnabled(true);
        }
        System.out.println("UI updated from current table");
    }
    
    private void serializeTables() {
        try {
            TablesSerializer serializer = new TablesSerializer(
                this.applicationConfig, this.tables);
            serializer.saveTables();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "An error occurred when saving the tables. These might be lost "
                    + "if you click the applicaton now.",
                "Serialization impossible",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateDrinkModel() {
        // Update the type of drinks depending if there are other plates sent
        // or not.
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
            }
        }
    }
    
    private void updateMainCourseComboboxUi() {
        ArrayList<String> mainCourseNames = new ArrayList<>();
        // Use functional operator like in JS.
        // Equivalent to: for(MainCourse plate: this.defaultMainCourses)
        this.defaultMainCourses.forEach((plate) -> {
            mainCourseNames.add(plate.getCode() + ": " + plate.getLabel() +
                " (" + plate.getPrice() + " " + this.currencySymbol + ")");
        });
        this.platesCombobox.setModel(new DefaultComboBoxModel<>(
            mainCourseNames.toArray(new String[mainCourseNames.size()])));
    }
    
    private void initApplicationGui() {
        // Populate the combobox of restaurant tables
        // toArray() returns an array of objets, we need an array of strings
        // first in order to be able to sort the table alphabetically.
        Object[] tableKeys = this.tables.keySet().toArray();
        String[] tableNames = Arrays.copyOf(tableKeys, tableKeys.length, String[].class);
        Arrays.sort(tableNames);
        this.tableCombobox.setModel(new DefaultComboBoxModel<>(tableNames));
     
        this.updateMainCourseComboboxUi();
        
        ArrayList<String> dessertNames = new ArrayList<>();
        this.defaultDesserts.forEach((plate) -> {
            dessertNames.add(plate.getCode() + ": " + plate.getLabel() +
                " (" + plate.getPrice() + " " + this.currencySymbol + ")");
        });
        this.dessertsCombobox.setModel(new DefaultComboBoxModel<>(
            dessertNames.toArray(new String[dessertNames.size()])));
        
        this.currentTable = this.tables.get(this.tableCombobox.getSelectedItem().toString());
        this.maxCoversValueLabel.setText(String.valueOf(this.currentTable.getMaxCovers()));

        // The getSelection on combobox is only populated when there has been
        // previously a selection. By default, when the app is launched, while
        // the selected item is the first of the combobox, the app assumes
        // no selection has been made yet, which could lead to a crash.
        // Prevent this.
        this.tableCombobox.setSelectedIndex(0);
        this.platesCombobox.setSelectedIndex(0);
        this.dessertsCombobox.setSelectedIndex(0);
        
        this.updateUiFromCurrentTable();
    }

    private void startOrderReadyNotifyThread() {
        this.orderReadyNotifyThread = new OrderReadyNotifyThread(
            this.applicationConfig,
            this.defaultMainCourses,
            this.defaultDesserts,
            this.networkReceiver,
            this.ordersReady,
            this.ordersReadyCheckbox);
        this.orderReadyNotifyThread.start();
    }

    private void startNetworkClientConnection() {
        this.networkSender = new NetworkBasicClient(
            this.applicationConfig.getServerAddress(),
            this.applicationConfig.getServerPort());
    }

    private void startNetworkServerTrapConnection() {
        this.networkReceiver = new NetworkBasicServer(this.applicationConfig.getTrapServerPort(), null);
        System.out.println("DEBUG Server listening on " + this.applicationConfig.getTrapServerPort());
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
        menuBar = new javax.swing.JMenuBar();
        waitersMenu = new javax.swing.JMenu();
        modifyPasswordMenuItem = new javax.swing.JMenuItem();
        addNewWaiterMenuItem = new javax.swing.JMenuItem();
        tablesMenu = new javax.swing.JMenu();
        listTablesMenuItem = new javax.swing.JMenuItem();
        totalNumberClientsMenuItem = new javax.swing.JMenuItem();
        totalBillsMenuItem = new javax.swing.JMenuItem();
        platesMenu = new javax.swing.JMenu();
        listPlatesMenuItem = new javax.swing.JMenuItem();
        listDessertsMenuItem = new javax.swing.JMenuItem();
        platesMenuSeparator = new javax.swing.JPopupMenu.Separator();
        createPlateMenuItem = new javax.swing.JMenuItem();
        removePlateMenuItem = new javax.swing.JMenuItem();

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
        readAvailablePlatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readAvailablePlatesButtonActionPerformed(evt);
            }
        });

        billAmountLabel.setText("0 EUR");

        waitersMenu.setText("Waiters");

        modifyPasswordMenuItem.setText("Modify waiter password");
        modifyPasswordMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyPasswordMenuItemActionPerformed(evt);
            }
        });
        waitersMenu.add(modifyPasswordMenuItem);

        addNewWaiterMenuItem.setText("Add new waiter");
        addNewWaiterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewWaiterMenuItemActionPerformed(evt);
            }
        });
        waitersMenu.add(addNewWaiterMenuItem);

        menuBar.add(waitersMenu);

        tablesMenu.setText("Tables");

        listTablesMenuItem.setText("List tables");
        listTablesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listTablesMenuItemActionPerformed(evt);
            }
        });
        tablesMenu.add(listTablesMenuItem);

        totalNumberClientsMenuItem.setText("Total number clients");
        tablesMenu.add(totalNumberClientsMenuItem);

        totalBillsMenuItem.setText("Total bills");
        tablesMenu.add(totalBillsMenuItem);

        menuBar.add(tablesMenu);

        platesMenu.setText("Plates");

        listPlatesMenuItem.setText("List plates");
        platesMenu.add(listPlatesMenuItem);

        listDessertsMenuItem.setText("List desserts");
        platesMenu.add(listDessertsMenuItem);
        platesMenu.add(platesMenuSeparator);

        createPlateMenuItem.setText("Add new plate");
        platesMenu.add(createPlateMenuItem);

        removePlateMenuItem.setText("Remove existing plate");
        platesMenu.add(removePlateMenuItem);

        menuBar.add(platesMenu);

        setJMenuBar(menuBar);

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        String orderComment = this.platesCommentTextfield.getText();

        try {
            plateQuantityInput = Integer.parseInt(
                this.platesQuantityTextfield.getText());
            
            if (orderComment.toLowerCase().contains(
                applicationConfig.getOrderFieldDelimiter().toLowerCase()) ||
                orderComment.toLowerCase().contains(
                applicationConfig.getOrderLineDelimiter().toLowerCase()) ||
                orderComment.toLowerCase().contains("\\n") ||
                orderComment.toLowerCase().contains("\\r")) {
                JOptionPane.showMessageDialog(getParent(),
                    "The comment you typed cannot contain the following strings: \""
                        + this.applicationConfig.getOrderFieldDelimiter().toLowerCase()
                        + " \" and \""
                        + this.applicationConfig.getOrderLineDelimiter().toLowerCase()
                        + "\" nor new line characters.",
                    "Invalid characters",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (plateQuantityInput <= 0) {
                JOptionPane.showMessageDialog(this,
                    "The plate quantity must be positive",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }

            if (this.currentTable.getEffectiveCovers()
                + plateQuantityInput > this.currentTable.getMaxCovers()) {
                throw new TooManyCoversException(
                    plateQuantityInput,
                    this.currentTable.getMaxCovers());
            }
            
            this.orderCurrentPlate(selectedPlate, plateQuantityInput, orderComment);
        
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "The plate quantity must be a valid number",
                "Invalid quantity",
                JOptionPane.ERROR_MESSAGE); 
        } catch (TooManyCoversException ex) {
            String noButtonText;
            String yesButtonText;
            String questionText;
            switch (this.currentTable.getEffectiveCovers()) {
                case 0:
                    noButtonText = "No, keep no cover";
                    break;
                case 1:
                    noButtonText = "No, keep my existing cover";
                    break;
                default:
                    noButtonText = "No, keep my existing " +
                        this.currentTable.getEffectiveCovers() + " covers";
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

            this.orderCurrentPlate(selectedPlate, plateQuantityInput, orderComment);
        }
    }

    private void orderCurrentPlate(Plate plate, int quantity, String comment) {
        // Update model
        PlateOrder orderToAdd = new PlateOrder(plate, quantity);
        orderToAdd.setComment(comment);
        this.currentTable.addOrder(orderToAdd);

        // Update UI
        this.updateUiFromCurrentTable();
        this.serializeTables();

        this.ordersSentCheckbox.setSelected(false);
    }

    private void orderDessert() {
        Dessert dessert;
        int dessertQuantity;
        String orderComment;
        try {
            dessert = this.defaultDesserts.get(
                this.dessertsCombobox.getSelectedIndex());
            dessertQuantity = Integer.parseInt(
                this.dessertsQuantityTextfield.getText());
            
            orderComment = this.dessertsCommentsTextfield.getText();
            
            if (orderComment.toLowerCase().contains(
                applicationConfig.getOrderFieldDelimiter().toLowerCase()) ||
                orderComment.toLowerCase().contains(
                applicationConfig.getOrderLineDelimiter().toLowerCase()) ||
                orderComment.toLowerCase().contains("\\n") ||
                orderComment.toLowerCase().contains("\\r")) {
                JOptionPane.showMessageDialog(getParent(),
                    "The comment you typed cannot contain the following strings: \""
                        + this.applicationConfig.getOrderFieldDelimiter().toLowerCase()
                        + " \" and \""
                        + this.applicationConfig.getOrderLineDelimiter().toLowerCase()
                        + "\" nor new line characters.",
                    "Invalid characters",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            if (dessertQuantity <= 0) {
                JOptionPane.showMessageDialog(this,
                    "The dessert quantity must be positive",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
            
            this.orderCurrentPlate(dessert, dessertQuantity, orderComment);
        
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "The dessert quantity must be a valid number",
                "Invalid quantity",
                JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void removeFromOrdersToSend() {
        
        // Update model
        HashMap<String, Integer> ordersToSendListMapping = new HashMap<>();
        for (int i = 0; i < this.currentTable.getOrders().size(); i++) {
            PlateOrder order = this.currentTable.getOrders().get(i);
            if (order.isSent()) {
                continue;
            }
            String plateCode;
            if (order.getPlate() instanceof MainCourse) {
                plateCode = ((MainCourse)order.getPlate()).getCode();
            } else {
                plateCode = ((Dessert)order.getPlate()).getCode();
            }
            String orderLine = order.getQuantity() + " " +
                plateCode + ": " +
                order.getPlate().getLabel() + " (" +
                order.getPrice().toString() + " " +
                this.currencySymbol + ")";
            System.out.println("DEBUG ordersToSendListMapping: " + orderLine);
            ordersToSendListMapping.put(orderLine, i);
        }

        String selectedLine = this.ordersToSendList.getSelectedValue();
        if (selectedLine == null) {
            return;
        }
        
        int orderIndex = ordersToSendListMapping.get(selectedLine);
        PlateOrder orderSelected = this.currentTable.getOrders().get(orderIndex);
        // Forge a 1 item order to recompute properly the orders using the
        // same method.
        PlateOrder orderToRemove = new PlateOrder(orderSelected.getPlate(), 1);
                    System.out.println("debug quantity before: " + this.currentTable.getEffectiveCovers());
            this.currentTable.removeOrder(orderToRemove);

            System.out.println("debug quantity after: " + this.currentTable.getEffectiveCovers());
        
        this.updateUiFromCurrentTable();
    }

    private void dessertsOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dessertsOrderButtonActionPerformed
        this.orderDessert();
    }//GEN-LAST:event_dessertsOrderButtonActionPerformed

    private void ordersSendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ordersSendButtonActionPerformed
        ArrayList<PlateOrder> ordersToSend = new ArrayList<>();
        
        for (PlateOrder order: this.currentTable.getOrders()) {
            if (!order.isSent()) {
                ordersToSend.add(order);
                order.setSent();
            }
        }
        
        if (ordersToSend.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "There is no order to send.",
                "No orders to send",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        NetworkProtocolOrderSender encoder = new NetworkProtocolOrderSender(
            this.applicationConfig, this.currentTable, ordersToSend);
        String request = encoder.encodeRequest();

        boolean retryConnection = true;
        String networkAnswer = null;

        while (retryConnection) {
            retryConnection = false;

            if (this.networkSender != null) {
                try {
                    networkAnswer = this.networkSender.sendString(request);
                } catch (Exception e) {}
            }

            if (this.networkSender == null || networkAnswer == null) {
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
                this.startNetworkClientConnection();
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
                OrderDeclinedReasonGui orderDeclinedGui;
                if (answerFields.size() > 1) {
                    orderDeclinedGui = new OrderDeclinedReasonGui(
                        this, answerFields.get(1));
                } else {
                    orderDeclinedGui = new OrderDeclinedReasonGui(
                        this, null);
                }
                orderDeclinedGui.setLocationRelativeTo(this);
                orderDeclinedGui.setVisible(true);
                return;
            }
        }
        
        // Update model
        for (PlateOrder order: this.currentTable.getOrders()) {
            order.setSent();
        }
        this.updateDrinkModel();

        // Update UI
        this.updateUiFromCurrentTable();

        JOptionPane.showMessageDialog(
            this,
            "The order has been received properly by the kitchen.",
            "Order received",
            JOptionPane.INFORMATION_MESSAGE);
        this.ordersSentCheckbox.setSelected(true);

        this.serializeTables();
    }//GEN-LAST:event_ordersSendButtonActionPerformed

    private void tableComboboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tableComboboxItemStateChanged
        if (evt.getStateChange() != ItemEvent.SELECTED ||
            evt.getSource() != this.tableCombobox) {
            return;
        }

        // If this is the same table (because the user has cancelled the
        // table switch), there is no need to repopulate in-memory objects.
        if (this.tableCombobox.getSelectedIndex() == this.selectedTableIndex) {
            return;
        }

        this.tables.put(this.currentTable.getNumber(), this.currentTable);

        // Ask the user if he wants to change the current waiter
        WaiterChangeQuestionGui waiterChangeGui =
            new WaiterChangeQuestionGui(this, true);
        waiterChangeGui.setWaiterName(this.currentTable.getWaiterName());
        waiterChangeGui.setVisible(true);
        boolean isWaiterChanging = waiterChangeGui.isWaiterChanging();
        waiterChangeGui.dispose();

        // Used to backup the new waiter name further in the code, otherwise
        // we will lose access to it after the table has switched.
        // Saving the current waiter name here is also needed in order to save
        // the current waiter on table for which no plate has been sent.
        String newWaiterName = this.currentTable.getWaiterName();

        if (isWaiterChanging) {
            try {
                newWaiterName = this.usersManager.authenticateUser();
                this.currentTable.setWaiterName(newWaiterName);
                this.setTitle(this.applicationConfig.getRestaurantName()
                    + ": " + newWaiterName);
            } catch (UsersManagerUserNotFoundException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Unable to find the user \""
                        + ex.getUsername()
                        + "\". Cancelling table change...",
                    this.applicationConfig.getRestaurantName(),
                    JOptionPane.ERROR_MESSAGE);
                this.tableCombobox.setSelectedIndex(this.selectedTableIndex);
                return;
            } catch (UsersManagerPasswordInvalidException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "The password for the user \""
                        + ex.getUsername()
                        + "\". Cancelling table change...",
                    this.applicationConfig.getRestaurantName(),
                    JOptionPane.ERROR_MESSAGE);
                this.tableCombobox.setSelectedIndex(this.selectedTableIndex);
                return;
            } catch (UsersManagerLoginDialogCancelled ex) {
                this.tableCombobox.setSelectedIndex(this.selectedTableIndex);
                return;
            }
        }

        // Recover the previous state of the table if any
        String newTableNumber = this.tableCombobox.getSelectedItem().toString();
        this.currentTable = this.tables.get(newTableNumber);

        // Update model with new table info
        this.currentTable.setWaiterName(newWaiterName);
        this.selectedTableIndex = this.tableCombobox.getSelectedIndex();

        // Update UI
        this.updateUiFromCurrentTable();
    }//GEN-LAST:event_tableComboboxItemStateChanged

    private void exitApplication() {
        try {
            if (this.networkSender != null) {
                this.networkSender.setEndSending();
            }
            if (this.networkReceiver != null) {
                this.networkReceiver.setEndReceiving();
            }
        } catch (Exception e) {}
        this.dispose();
        System.exit(0);
    }

    private void platesQuantityTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platesQuantityTextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_platesQuantityTextfieldActionPerformed

    private void drinksAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drinksAddButtonActionPerformed
        this.orderDrinks();
    }//GEN-LAST:event_drinksAddButtonActionPerformed

    private void billCheckoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billCheckoutButtonActionPerformed
        BillGui billDialog = new BillGui(
            this,
            this.applicationConfig,
            this.currentTable);
        billDialog.setVisible(true);
        billDialog.dispose();
        this.updateUiFromCurrentTable();
        this.serializeTables();
    }//GEN-LAST:event_billCheckoutButtonActionPerformed

    private void readAvailablePlatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readAvailablePlatesButtonActionPerformed

        if (this.ordersReady.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "There is no new plate to read.",
                "No the plates ready",
                JOptionPane.ERROR_MESSAGE);
            return;
        } 

        ArrayList<PlateOrder> orderReadyFetched = new ArrayList<>(
            this.orderReadyNotifyThread.getOrdersReady());
        StringBuilder message = new StringBuilder();
        String messageTitle;
        if (orderReadyFetched.size() > 1) {
            messageTitle = "New plates ready";
            message.append("The following plates are ready to be served: ");
            int number = 1;
            for (PlateOrder order: orderReadyFetched) {
                message.append(" (")
                       .append(number)
                       .append(") ")
                       .append(order.getQuantity())
                       .append("x ")
                       .append(order.getPlate().getLabel());
                number++;
            }
        } else {
            messageTitle = "New plate ready";
            message.append("The following plate is ready to be served:")
            .append(orderReadyFetched.get(0).getQuantity())
            .append("x ")
            .append(orderReadyFetched.get(0).getPlate().getLabel());
        }

        JOptionPane.showMessageDialog(this,
            message.toString(),
            messageTitle,
            JOptionPane.INFORMATION_MESSAGE);

        this.ordersReadyCheckbox.setSelected(false);
    }//GEN-LAST:event_readAvailablePlatesButtonActionPerformed

    private void modifyPasswordMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyPasswordMenuItemActionPerformed
        ModifyPasswordGui modifyPasswordGui = new ModifyPasswordGui(
            this,
            true,
            this.applicationConfig,
            this.usersManager,
            this.currentTable.getWaiterName());
        modifyPasswordGui.setLocationRelativeTo(this);
        modifyPasswordGui.setVisible(true);
        modifyPasswordGui.dispose();
    }//GEN-LAST:event_modifyPasswordMenuItemActionPerformed

    private void addNewWaiterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewWaiterMenuItemActionPerformed
        AddNewUserGui addNewUserGui = new AddNewUserGui(
            this,
            true,
            this.applicationConfig,
            this.usersManager);
        addNewUserGui.setLocationRelativeTo(this);
        addNewUserGui.setVisible(true);
        addNewUserGui.dispose();
    }//GEN-LAST:event_addNewWaiterMenuItemActionPerformed

    private void listTablesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listTablesMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listTablesMenuItemActionPerformed

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
  
            // Update UI
            this.updateUiFromCurrentTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "The drinks amount must be a valid number",
                "Invalid drinks amount",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addNewWaiterMenuItem;
    private javax.swing.JLabel billAmountLabel;
    private javax.swing.JButton billCheckoutButton;
    private javax.swing.JLabel billLabel;
    private javax.swing.JLabel billPaidStateLabel;
    private javax.swing.JMenuItem createPlateMenuItem;
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
    private javax.swing.JMenuItem listDessertsMenuItem;
    private javax.swing.JMenuItem listPlatesMenuItem;
    private javax.swing.JMenuItem listTablesMenuItem;
    private javax.swing.JLabel maxCoversLabel;
    private javax.swing.JLabel maxCoversValueLabel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem modifyPasswordMenuItem;
    private javax.swing.JCheckBox ordersReadyCheckbox;
    private javax.swing.JButton ordersSendButton;
    private javax.swing.JCheckBox ordersSentCheckbox;
    private javax.swing.JLabel ordersToSendLabel;
    private javax.swing.JList<String> ordersToSendList;
    private javax.swing.JComboBox<String> platesCombobox;
    private javax.swing.JTextField platesCommentTextfield;
    private javax.swing.JLabel platesCommentsLabel;
    private javax.swing.JLabel platesLabel;
    private javax.swing.JMenu platesMenu;
    private javax.swing.JPopupMenu.Separator platesMenuSeparator;
    private javax.swing.JButton platesOrderButton;
    private javax.swing.JLabel platesOrdersLabel;
    private javax.swing.JLabel platesQuantityLabel;
    private javax.swing.JTextField platesQuantityTextfield;
    private javax.swing.JButton readAvailablePlatesButton;
    private javax.swing.JMenuItem removePlateMenuItem;
    private javax.swing.JLabel servedPlatesLabel;
    private javax.swing.JList<String> servedPlatesList;
    private javax.swing.JComboBox<String> tableCombobox;
    private javax.swing.JLabel tableLabel;
    private javax.swing.JMenu tablesMenu;
    private javax.swing.JMenuItem totalBillsMenuItem;
    private javax.swing.JMenuItem totalNumberClientsMenuItem;
    private javax.swing.JMenu waitersMenu;
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

        if (ke.getKeyCode() == KeyEvent.VK_DELETE &&
            this.getFocusOwner() == this.ordersToSendList) {
            this.removeFromOrdersToSend();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
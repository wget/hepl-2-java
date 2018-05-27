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
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;

/**
 *
 * @author wget
 */
@SuppressWarnings("serial")
public class DateSettingsGui extends JDialog implements ItemListener, KeyListener {

    private ArrayList<ArrayList<Object>> dateFormatValues;
    private ArrayList<Locale> localeValues;
    private int selectedDateFormat;
    private Locale selectedLocale;
    private boolean isCancelled = false;
    RestaurantConfig applicationConfig;

    /**
     * Creates new form AboutGui
     */
    public DateSettingsGui(Frame parent, RestaurantConfig applicationConfig) {
        super(parent, true);
        initComponents();
        this.setTitle("Date settings");
        this.applicationConfig = applicationConfig;
        
        // Set default values
        this.selectedDateFormat = DateFormat.DEFAULT;
        this.selectedLocale = Locale.ENGLISH;
        
        this.dateFormatValues = new ArrayList<>();
        ArrayList<Object> value = new ArrayList<>();
        value.add(DateFormat.DEFAULT);
        value.add("Default");
        dateFormatValues.add(value);
        value = new ArrayList<>();
        value.add(DateFormat.FULL);
        value.add("Full");
        dateFormatValues.add(value);
        value = new ArrayList<>();
        value.add(DateFormat.LONG);
        value.add("Long");
        dateFormatValues.add(value);
        value = new ArrayList<>();
        value.add(DateFormat.MEDIUM);
        value.add("Medium");
        dateFormatValues.add(value);
        value = new ArrayList<>();
        value.add(DateFormat.SHORT);
        value.add("Short");
        dateFormatValues.add(value);
        
        this.localeValues = new ArrayList<>();
        ArrayList<Locale> availableLocales = new ArrayList<>(
            Arrays.asList(Locale.getAvailableLocales()));
        // Remove locales with empty name. Maybe they are empty because they
        // are not displayable because of missing font to display non European
        // alphabets?
        for (Locale locale: availableLocales) {
            if (locale.getDisplayName().isEmpty()) {
                continue;
            }
            
            // Locale specific to a country do not work when configuring a date
            // format.
            if (locale.getDisplayName().contains("(") ||
                locale.getDisplayName().contains(")")) {
                continue;
            }
            this.localeValues.add(locale);
        }
        
        // Sort list of locales by display names. By default, it isn't even
        // sorted.
        Collections.sort(this.localeValues, new Comparator<Locale>() {
            @Override
            public int compare(Locale locale1, Locale locale2) {
                return  locale1.getDisplayName().compareTo(
                    locale2.getDisplayName());
            }
        });

        this.dateFormatCombobox.addItemListener(this);
        this.dateLanguageCombobox.addItemListener(this);
        
        DefaultComboBoxModel<String> dateFormatComboboxModel =
            (DefaultComboBoxModel<String>)this.dateFormatCombobox.getModel();
        for (int i = 0; i < this.dateFormatValues.size(); i++) {
            dateFormatComboboxModel.addElement(
                this.dateFormatValues.get(i).get(1).toString());
        }
        
        DefaultComboBoxModel<String> dateLanguageComboboxModel =
            (DefaultComboBoxModel<String>)this.dateLanguageCombobox.getModel();
        for (Locale locale: this.localeValues) {
            dateLanguageComboboxModel.addElement(locale.getDisplayName());
        }
        
        // Preselect selected date format in the combobox
        for (int i = 0; i < this.dateFormatValues.size(); i++) {
            if ((int)this.dateFormatValues.get(i).get(0) ==
                this.applicationConfig.getDateFormat()) {
                this.dateFormatCombobox.setSelectedIndex(i);
                System.out.println("Date format detected: " + this.dateFormatValues.get(i).get(1));
                break;
            }
        }
        // Preselect selected language in the combobox
        for (int i = 0; i < this.localeValues.size(); i++) {
            if (this.localeValues.get(i).equals(
                this.applicationConfig.getLocale())) {
                this.dateLanguageCombobox.setSelectedIndex(i);
                System.out.println("Locale detected: " + this.localeValues.get(i));
                break;
            }
        }

        this.dateFormatCombobox.addKeyListener(this);
        this.dateLanguageCombobox.addKeyListener(this);
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
        dateFormatLabel = new javax.swing.JLabel();
        dateFormatCombobox = new javax.swing.JComboBox<>();
        dateLanguageLabel = new javax.swing.JLabel();
        dateLanguageCombobox = new javax.swing.JComboBox<>();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        dateFormatLabel.setText("Date format:");

        dateLanguageLabel.setText("Date language:");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dateFormatLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateFormatCombobox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateLanguageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                    .addComponent(dateLanguageCombobox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dateFormatLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dateFormatCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(dateLanguageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dateLanguageCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.isCancelled = true;
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> dateFormatCombobox;
    private javax.swing.JLabel dateFormatLabel;
    private javax.swing.JComboBox<String> dateLanguageCombobox;
    private javax.swing.JLabel dateLanguageLabel;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        
        if (e.getSource() == this.dateFormatCombobox) {
            this.selectedDateFormat = (int)this.dateFormatValues.get(
                this.dateFormatCombobox.getSelectedIndex()).get(0);
            return;
        }
        
        if (e.getSource() == this.dateLanguageCombobox) {
            this.selectedLocale = this.localeValues.get(
                this.dateLanguageCombobox.getSelectedIndex());
        }
    }
    
    public int getSelectedDateFormat() {
        return this.selectedDateFormat;
    }
    
    public Locale getSelectedLocale() {
        return this.selectedLocale;
    }
    
    public boolean isGuiCancelled() {
        return this.isCancelled;
    }

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

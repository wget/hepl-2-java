/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager;

import be.wget.inpres.java.restaurant.kitchenmanager.guis.RestaurantKitchenManagerGui;
import javax.swing.UIManager;

/**
 *
 * @author wget
 */
public class RestaurantKitchenManager {
        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.setProperty("awt.useSystemAAFontSettings","lcd");
        System.setProperty("swing.aatext", "true");
        
        // Set the GTK look and feel only if it exists.
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (Exception e) {}
                break;
            }
        }
        RestaurantKitchenManagerGui app = new RestaurantKitchenManagerGui();
        app.setVisible(true);
    }
}

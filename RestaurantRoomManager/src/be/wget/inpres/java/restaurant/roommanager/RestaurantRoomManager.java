/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.roommanager;
import be.wget.inpres.java.restaurant.roommanager.guis.RestaurantRoomManagerGui;

/**
 *
 * @author wget
 */
public class RestaurantRoomManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Enable anti-aliased text. Specifying "on" seems to break
        // antialiasing and True-Type usage on Windows. It seems we need to
        // use the "lcd" argument on that platform.
        System.setProperty("awt.useSystemAAFontSettings","lcd");
        System.setProperty("swing.aatext", "true");
        
        RestaurantRoomManagerGui app = new RestaurantRoomManagerGui();
        app.setVisible(true);
    }
    
}
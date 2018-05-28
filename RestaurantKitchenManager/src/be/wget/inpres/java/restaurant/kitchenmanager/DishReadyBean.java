/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author wget
 */
public class DishReadyBean implements PropertyChangeListener {

    JFrame parentFrame;
    RestaurantConfig applicationConfig;

    public DishReadyBean() {
    }

    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setApplicationConfig(RestaurantConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        System.out.println("DishReadyBean: Property changed");
        PlateToBePrepared plateToBePrepared = (PlateToBePrepared)pce.getNewValue();
        JOptionPane.showMessageDialog(
            this.parentFrame,
            "The plate preparation for " +
                plateToBePrepared.getPlateCode() +
                " has launched and should be finished within " +
                plateToBePrepared.getComputedTime() +
                " seconds",
            this.applicationConfig.getRestaurantName(),
            JOptionPane.INFORMATION_MESSAGE);
    }
}

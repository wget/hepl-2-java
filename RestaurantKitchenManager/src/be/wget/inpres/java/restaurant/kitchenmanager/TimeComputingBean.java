/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author wget
 */
public class TimeComputingBean implements IngredientsListener {
    
    private PlateToBePrepared plateToBePrepared;
    private PropertyChangeSupport propertyChangeSupport;

    public TimeComputingBean() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    @Override
    public void ingredientsReceived(IngredientsEvent e) {
        System.out.println("TimeComputingBean: ingredientsReceived");
        this.setPlateToBePrepared(new PlateToBePrepared(
            e.getPlateCode(),
            (e.getIngredients().size() / 3) * 10));
    }

    public PlateToBePrepared getPlateToBePrepared() {
        return plateToBePrepared;
    }

    public void setPlateToBePrepared(PlateToBePrepared plateToBePrepared) {
        System.out.println("TimeComputingBean: setPlateToBePrepared");
        PlateToBePrepared oldPlateToBePrepared = this.plateToBePrepared;
        this.plateToBePrepared = plateToBePrepared;
        this.propertyChangeSupport.firePropertyChange(
            "plateToBePrepared",
            oldPlateToBePrepared,
            this.plateToBePrepared);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.propertyChangeSupport.addPropertyChangeListener(l);
    }
    public void removePropertyChangeListener(PropertyChangeListener l) {
        this.propertyChangeSupport.removePropertyChangeListener(l);
    }
}

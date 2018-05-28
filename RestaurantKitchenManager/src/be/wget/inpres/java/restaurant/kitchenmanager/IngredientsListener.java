/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.kitchenmanager;

import java.util.EventListener;

/**
 *
 * @author wget
 */
public interface IngredientsListener extends EventListener {
    public void ingredientsReceived(IngredientsEvent e);

}

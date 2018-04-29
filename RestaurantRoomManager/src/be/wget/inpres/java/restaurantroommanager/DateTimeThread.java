/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurantroommanager;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author wget
 */
public class DateTimeThread extends Thread {
    
    private JLabel dateTimeLabel;
    private boolean continueExecution;
    
    public DateTimeThread(JLabel dateTimeLabel) {
        this.dateTimeLabel = dateTimeLabel;
        this.continueExecution = true;
    }
    
    public void run() {
        while (continueExecution) {
            Date now = new Date();
            // The locale can also be defined like this:
            // new Locale("EN", "BE");
            dateTimeLabel.setText(DateFormat.getDateTimeInstance(
                DateFormat.FULL, DateFormat.FULL, Locale.US).format(now));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DateTimeThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void stopExecution() {
        this.continueExecution = false;
    }
    
}

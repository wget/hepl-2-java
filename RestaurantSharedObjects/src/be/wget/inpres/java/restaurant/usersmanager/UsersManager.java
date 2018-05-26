/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.usersmanager;

import be.wget.inpres.java.restaurant.config.RestaurantConfig;
import be.wget.inpres.java.restaurant.usersmanager.guis.LoginGui;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import javax.swing.JFrame;

/**
 *
 * @author wget
 */
public class UsersManager {
    
    private RestaurantConfig applicationConfig;
    private JFrame parent;
    private File usersFile;
    
    private Properties credentials;
    boolean isFileDefined = false;
    
    public UsersManager(RestaurantConfig applicationConfig, JFrame parent)
        throws UsersManagerSerializationException {
        this.applicationConfig = applicationConfig;
        this.parent = parent;
        try {        
            this.credentials = new Properties();
            this.usersFile = new File(this.applicationConfig.getUsersFilename());
            FileInputStream fis = new FileInputStream(this.usersFile);
            this.credentials.load(fis);
            fis.close();
            if (!this.credentials.propertyNames().hasMoreElements()) {
                this.populateDefaultUsers();
            }
            this.isFileDefined = true;
        } catch (IOException ex) {
            this.populateDefaultUsers();
        }
    }
    
    private void populateDefaultUsers()
        throws UsersManagerSerializationException {
        try {
            this.credentials.put("wget", this.getSha512FromPassword("12345"));
            this.credentials.put("wagner", this.getSha512FromPassword("vilvens"));
            this.usersFile = new File(this.applicationConfig.getUsersFilename());
            FileInputStream fis = new FileInputStream(this.usersFile);
            this.credentials.load(fis);
            fis.close();
            this.isFileDefined = true;
            FileOutputStream fos = new FileOutputStream(this.usersFile);
            this.credentials.store(fos, null);
            fos.close();
        } catch (IOException ex1) {
            throw new UsersManagerSerializationException();
        }
    }
    
    public String authenticateUser()
            throws UsersManagerUserNotFoundException,
                   UsersManagerPasswordInvalidException,
                   UsersManagerLoginDialogCancelled {
        LoginGui login = new LoginGui(this.parent, true);
        login.setVisible(true);
        if (login.isDialogCancelled()) {
            throw new UsersManagerLoginDialogCancelled();
        }

        String username = login.getUsername();

        String hashedPassword = this.credentials.getProperty(login.getUsername());
        if (hashedPassword == null) {
            throw new UsersManagerUserNotFoundException(username);
        }

        if (!hashedPassword.equals(
            this.getSha512FromPassword(login.getPassword()))) {
            throw new UsersManagerPasswordInvalidException(username);
        }
        login.dispose();
        return username;
    }
    
    public void changeUserPassword(
            String username,
            String oldPassword,
            String newPassword)
        throws IOException,
               UsersManagerUserNotFoundException,
               UsersManagerPasswordInvalidException {
        
        String hashedPassword = this.credentials.getProperty(username);
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new UsersManagerUserNotFoundException(username);
        }

        if (!hashedPassword.equals(
            this.getSha512FromPassword(oldPassword.toString()))) {
            throw new UsersManagerPasswordInvalidException(username);
        }        
        
        if (newPassword.isEmpty()) {
            throw new UsersManagerPasswordInvalidException(username);
        }
        
        this.credentials.setProperty(
            username,
            this.getSha512FromPassword(newPassword));
        
        if (this.isFileDefined) {
            FileOutputStream fos = new FileOutputStream(this.usersFile);
            this.credentials.store(fos, null);
            fos.close();
        }
    }
    
    public void addUser(String user, String newPassword)
        throws IOException,
               UsersManagerPasswordInvalidException,
               UsersManagerUserAlreadyExistsException {
        String hashedPassword = this.credentials.getProperty(user);
        if (hashedPassword != null) {
            throw new UsersManagerUserAlreadyExistsException(user);
        }
        
        if (newPassword.isEmpty()) {
            throw new UsersManagerPasswordInvalidException(user);
        }
        
        this.credentials.setProperty(user, this.getSha512FromPassword(newPassword));
        
        if (this.isFileDefined) {
            FileOutputStream fos = new FileOutputStream(this.usersFile);
            this.credentials.store(fos, null);
            fos.close();
        }
    }

    private String getSha512FromPassword(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());
            
            byte byteData[] = md.digest();
            
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {}
        return null;
    }
}

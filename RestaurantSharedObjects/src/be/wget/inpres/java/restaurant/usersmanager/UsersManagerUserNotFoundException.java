/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.inpres.java.restaurant.usersmanager;

/**
 *
 * @author wget
 */
public class UsersManagerUserNotFoundException extends Exception {
    String username;

    public UsersManagerUserNotFoundException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    } 
}

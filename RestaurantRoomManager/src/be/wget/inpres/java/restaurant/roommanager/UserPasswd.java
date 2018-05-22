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
package be.wget.inpres.java.restaurant.roommanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author wget
 */
public class UserPasswd {
    
    private Properties userPasswd;

    public UserPasswd() {
        
        this.userPasswd = new Properties();
	InputStream input = null;

	try {
            input = new FileInputStream("passwd.conf");

            // load a properties file
            this.userPasswd.load(input);

	} catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getPasswordHash(String username) {
        return this.userPasswd.getProperty(username);
    }
}

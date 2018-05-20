package be.wget.inpres.java.restaurant.myutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
* @author William Gathoye
* @version First release
*/
public class StringSlicer {

    String[] splitString;

    public StringSlicer(String string, String delimiter) {
        this.splitString = string.split(delimiter);
    }

    /**
     * Return the number of components and display them on the console if the
     * display parameter is set to true.
     * @param display Display the number of components on the console if set to true
     * @return Return the number of components
     */
    public int getComponents(boolean display) {
        if (display) {
            System.out.println(this.splitString.length);
        }
        return this.splitString.length;
    }

    /**
     * Return an ArrayList (not Vector - SLRY who is still using Vectors in
     * 2018? These have been deprecated for ages) with the detected
     * components.
     * @return An ArrayList with the detected components.
     */
    public ArrayList<String> listComponents() {
        return new ArrayList<>(Arrays.asList(this.splitString));
    }

    /**
     * Return an ArrayList (not Vector - SLRY who is still using Vectors in
     * 2018? These have been deprecated for ages) with the detected
     * components but without repetition and in the order they appear.
     * @return An ArrayList with the detected components without repetition and
     * in the same order they appear.
     */
    public LinkedHashSet listUniqueComponents() {
        return new LinkedHashSet<>(this.listComponents());
    }
    
}

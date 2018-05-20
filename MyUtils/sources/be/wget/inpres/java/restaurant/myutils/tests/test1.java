package be.wget.inpres.java.restaurant.myutils.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import be.wget.inpres.java.restaurant.myutils.StringSlicer;
import java.util.Iterator;

class StringSlicerTest1 {
    public static void main(String[] args) {
        String string = new String("hello--world--this--is--a--world--by--wget");
        String delimiter = new String("--");
        StringSlicer explode = new StringSlicer(string, delimiter);
        System.out.println("1. getComponents");
        explode.getComponents(true);
        System.out.println("2. listComponents");
        ArrayList<String> array = explode.listComponents();
        for (String s: array) {
            System.out.println(s);
        }
        System.out.println("3. listUniqueComponents");
        LinkedHashSet hashSet = explode.listUniqueComponents();
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}


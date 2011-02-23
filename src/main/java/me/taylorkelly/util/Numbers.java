/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.taylorkelly.util;

public class Numbers {
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

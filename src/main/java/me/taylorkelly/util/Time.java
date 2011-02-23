

package me.taylorkelly.util;

public class Time {

    public static long ago(long cleanseAge) {
        return System.currentTimeMillis()/1000 - cleanseAge;
    }

}

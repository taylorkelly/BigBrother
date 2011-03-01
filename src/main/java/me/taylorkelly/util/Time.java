

package me.taylorkelly.util;

public class Time {

    public static long ago(long cleanseAge) {
        return System.currentTimeMillis()/1000 - cleanseAge;
    }
    public static String formatDuration(long s)
    {
    	return String.format("%dh%02dm%02ds", s/3600, (s%3600)/60, (s%60));
    }
}

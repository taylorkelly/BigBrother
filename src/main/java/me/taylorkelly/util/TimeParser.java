/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.taylorkelly.util;

import java.util.Calendar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TimeParser {

    /**
     * Returns the Calendar of the date represented by now subtracted by
     * a given time formatted with:
     * #d - number of days
     * #h - number of hours
     * #m - number of minutes
     * #s - number of seconds
     * @param strTime The string
     * @param player The player to report to
     * @return The Calendar of the specified date
     */
    public static Calendar parseTime(String strTime, Player player) {
        Calendar dateSearch;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        int lastIndex = 0;
        int currIndex = 1;
        while (currIndex <= strTime.length()) {
            while (currIndex <= strTime.length() && Numbers.isInteger(strTime.substring(lastIndex, currIndex))) {
                currIndex++;
            }
            if (currIndex - 1 == lastIndex) {
                player.sendMessage(ChatColor.RED + "Ignoring time quantifier with no time value: " + strTime.substring(currIndex - 1, currIndex));
                return null;
            } else {
                if (strTime.substring(currIndex - 1, currIndex).equalsIgnoreCase("d")) {
                    if (days != 0) {
                        player.sendMessage(ChatColor.RED + "Two day keys have been set. Ignoring: " + strTime.substring(lastIndex, currIndex));
                    } else {
                        days = Integer.parseInt(strTime.substring(lastIndex, currIndex - 1));
                    }
                } else if (strTime.substring(currIndex - 1, currIndex).equalsIgnoreCase("h")) {
                    if (hours != 0) {
                        player.sendMessage(ChatColor.RED + "Two hour keys have been set. Ignoring: " + strTime.substring(lastIndex, currIndex));
                    } else {
                        hours = Integer.parseInt(strTime.substring(lastIndex, currIndex - 1));
                    }
                } else if (strTime.substring(currIndex - 1, currIndex).equalsIgnoreCase("m")) {
                    if (minutes != 0) {
                        player.sendMessage(ChatColor.RED + "Two minute keys have been set. Ignoring: " + strTime.substring(lastIndex, currIndex));
                    } else {
                        minutes = Integer.parseInt(strTime.substring(lastIndex, currIndex - 1));
                    }
                } else if (strTime.substring(currIndex - 1, currIndex).equalsIgnoreCase("s")) {
                    if (seconds != 0) {
                        player.sendMessage(ChatColor.RED + "Two second keys have been set. Ignoring: " + strTime.substring(lastIndex, currIndex));
                    } else {
                        seconds = Integer.parseInt(strTime.substring(lastIndex, currIndex - 1));
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Ignoring time quantifier with invalid key: " + strTime.substring(currIndex - 1, currIndex));
                    return null;
                }
            }
            lastIndex = currIndex;
            currIndex += 1;
        }

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            player.sendMessage(ChatColor.RED + "No change in time was set.");
            return null;
        } else {
            dateSearch = Calendar.getInstance();
            dateSearch.add(Calendar.DAY_OF_MONTH, -days);
            dateSearch.add(Calendar.HOUR, -hours);
            dateSearch.add(Calendar.MINUTE, -minutes);
            dateSearch.add(Calendar.SECOND, -seconds);
            return dateSearch;
        }
    }

    public static long parseInterval(String strTime) {
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        int lastIndex = 0;
        int currIndex = 1;
        try {
            while (currIndex <= strTime.length()) {
                while (Numbers.isInteger(strTime.substring(lastIndex, currIndex)) && currIndex <= strTime.length()) {
                    currIndex++;
                }
                if (currIndex - 1 == lastIndex) {
                    System.out.println("Invalid time quanitifier, all items will be kept");
                    return -1;
                } else {
                    if (strTime.substring(currIndex - 1, currIndex).equalsIgnoreCase("d")) {
                        if (days != 0) {
                            System.out.println("Two day keys have been set. Ignoring: " + strTime.substring(lastIndex, currIndex));
                        } else {
                            days = Integer.parseInt(strTime.substring(lastIndex, currIndex - 1));
                        }
                    } else if (strTime.substring(currIndex - 1, currIndex).equalsIgnoreCase("h")) {
                        if (hours != 0) {
                            System.out.println("Two hour keys have been set. Ignoring: " + strTime.substring(lastIndex, currIndex));
                        } else {
                            hours = Integer.parseInt(strTime.substring(lastIndex, currIndex - 1));
                        }
                    } else if (strTime.substring(currIndex - 1, currIndex).equalsIgnoreCase("m")) {
                        if (minutes != 0) {
                            System.out.println("Two minute keys have been set. Ignoring: " + strTime.substring(lastIndex, currIndex));
                        } else {
                            minutes = Integer.parseInt(strTime.substring(lastIndex, currIndex - 1));
                        }
                    } else if (strTime.substring(currIndex - 1, currIndex).equalsIgnoreCase("s")) {
                        if (seconds != 0) {
                            System.out.println("Two second keys have been set. Ignoring: " + strTime.substring(lastIndex, currIndex));
                        } else {
                            seconds = Integer.parseInt(strTime.substring(lastIndex, currIndex - 1));
                        }
                    } else {
                        System.out.println("Ignoring invalid key time: " + strTime.substring(currIndex - 1, currIndex));
                    }
                }
                lastIndex = currIndex;
                currIndex += 1;
            }
        } catch (Exception e) {
            return -1;
        }

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            System.out.println("All items will be kept");
            return -1;
        } else {
            return seconds + (((days * 24 + hours) * 60) + minutes) * 60;
        }
    }
}

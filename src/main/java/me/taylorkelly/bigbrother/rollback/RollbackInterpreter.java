package me.taylorkelly.bigbrother.rollback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import me.taylorkelly.bigbrother.WorldManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Server;

public class RollbackInterpreter {

    private Rollback rollback;
    private Calendar dateSearch;
    private ArrayList<Integer> blockTypes;
    private ArrayList<String> playerList;
    private boolean all = false;
    private Server server;
    private Player player;
    private WorldManager manager;
    private int radius = 0;

    public RollbackInterpreter(Player player, String[] split, Server server, WorldManager manager) {
        this.manager = manager;
        this.player = player;
        this.server = server;
        playerList = new ArrayList<String>();
        blockTypes = new ArrayList<Integer>();
        for (int i = 1; i < split.length; i++) {
            String argument = split[i].trim();
            if (argument.equals("") || argument.equals(" "))
                continue;
            if (argument.length() > 2 && argument.substring(0, 2).equalsIgnoreCase("t:")) {
                parseTime(argument.substring(2));
            } else if (argument.length() > 3 && argument.substring(0, 3).equalsIgnoreCase("id:")) {
                parseId(argument.substring(3));
            } else if (argument.length() > 2 && argument.substring(0, 2).equalsIgnoreCase("r:")) {
                parseRadius(argument.substring(2));
            } else if (argument.equalsIgnoreCase("*")) {
                all = true;
            } else {
                List<Player> targets = server.matchPlayer(argument);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                playerList.add((findee == null) ? argument : findee.getName());
            }
        }
    }

    private void parseRadius(String radius) {
        try {
            int radInt = Integer.parseInt(radius);
            if (radInt <= 0) {
                player.sendMessage(ChatColor.RED + "Ignoring invalid radius: " + radius);
            } else {
                this.radius = radInt;
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Ignoring invalid radius: " + radius);
        }
    }

    private void parseId(String id) {
        if (id.contains(",")) {
            String[] ids = id.split(",");
            for (String actId : ids) {
                if (actId.equals(""))
                    continue;
                try {
                    blockTypes.add(Integer.parseInt(actId));
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Ignoring invalid block id: " + actId);
                }
            }
        } else {
            try {
                blockTypes.add(Integer.parseInt(id));
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Ignoring invalid block id: " + id);
            }
        }
    }

    private void parseTime(String strTime) {
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        int lastIndex = 0;
        int currIndex = 1;
        while (currIndex <= strTime.length()) {
            while (isInteger(strTime.substring(lastIndex, currIndex)) && currIndex <= strTime.length()) {
                currIndex++;
            }
            if (currIndex - 1 == lastIndex) {
                player.sendMessage(ChatColor.RED + "Ignoring time quantifier with no time value: " + strTime.substring(currIndex - 1, currIndex));
                return;
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
                    return;
                }
            }
            lastIndex = currIndex;
            currIndex += 1;
        }

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            player.sendMessage(ChatColor.RED + "No change in time was set.");
        } else {
            dateSearch = Calendar.getInstance();
            dateSearch.add(Calendar.DAY_OF_MONTH, -days);
            dateSearch.add(Calendar.HOUR, -hours);
            dateSearch.add(Calendar.MINUTE, -minutes);
            dateSearch.add(Calendar.SECOND, -seconds);
        }
    }

    public Boolean interpret() {
        rollback = new Rollback(server, manager);
        rollback.addReciever(player);
        if (all) {
            rollback.rollbackAll();
        } else {
            if (playerList.size() == 0) {
                player.sendMessage(ChatColor.RED + "No players marked for rollback. Cancelling rollback.");
                player.sendMessage(ChatColor.RED + "Use * for all players");
                return null;
            }
            rollback.addPlayers(playerList);
        }
        if (dateSearch != null) {
            rollback.setTime(dateSearch.getTimeInMillis() / 1000);
        }
        if (blockTypes.size() != 0) {
            rollback.addTypes(blockTypes);
        }
        rollback.setRadius(radius, player.getLocation());
        if (radius == 0 && dateSearch == null) {
            return false;
        } else {
            return true;
        }
    }
    
    public void send() {
        rollback.rollback();
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

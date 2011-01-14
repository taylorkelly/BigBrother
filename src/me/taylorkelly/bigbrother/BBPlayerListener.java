package me.taylorkelly.bigbrother;

import java.util.List;

import me.taylorkelly.bigbrother.datablock.*;
import me.taylorkelly.bigbrother.rollback.Rollback;
import me.taylorkelly.bigbrother.rollback.RollbackInterpreter;

import org.bukkit.*;
import org.bukkit.event.player.*;

public class BBPlayerListener extends PlayerListener {
    private BigBrother plugin;

    public BBPlayerListener(BigBrother plugin) {
        this.plugin = plugin;
    }

    public void onPlayerCommand(PlayerChatEvent event) {
        String[] split = event.getMessage().split(" ");
        Player player = event.getPlayer();
        // TODO permissions!
        if (split[0].equalsIgnoreCase("/bb")) {
            if (split.length == 1 || split[1].equalsIgnoreCase("help") && split.length == 2) {
                player.sendMessage(BigBrother.premessage + "Hello.");
                // TODO HELP
                event.setCancelled(true);
            } else if (split[1].equalsIgnoreCase("watch")) {
                if (split.length == 3) {
                    List<Player> targets = plugin.getServer().matchPlayer(split[2]);
                    Player watchee = null;
                    if (targets.size() == 1) {
                        watchee = targets.get(0);
                    }
                    String playerName = (watchee == null) ? split[2] : watchee.getName();

                    if (plugin.toggleWatch(playerName)) {
                        String status = (watchee == null) ? " (offline)" : " (online)";
                        player.sendMessage(BigBrother.premessage + "Now watching " + playerName + status);
                    } else {
                        String status = (watchee == null) ? " (offline)" : " (online)";
                        player.sendMessage(BigBrother.premessage + "No longer watching " + playerName + status);
                    }
                } else {
                    player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb watch <player>");
                }
                event.setCancelled(true);
            } else if (split[1].equalsIgnoreCase("watched")) {
                String watchedPlayers = plugin.getWatchedPlayers();
                if (watchedPlayers.equals("")) {
                    player.sendMessage(BigBrother.premessage + "Not currently watching anyone.");
                } else {
                    player.sendMessage(BigBrother.premessage + "Now watching:");
                    player.sendMessage(watchedPlayers);
                }
                event.setCancelled(true);
            } else if (split[1].equalsIgnoreCase("unwatched")) {
                String unwatchedPlayers = plugin.getUnwatchedPlayers();
                if (unwatchedPlayers.equals("")) {
                    player.sendMessage(BigBrother.premessage + "Everyone on is being watched.");
                } else {
                    player.sendMessage(BigBrother.premessage + "Not currently watching:");
                    player.sendMessage(unwatchedPlayers);
                }
                event.setCancelled(true);
            } else if (split[1].equalsIgnoreCase("rollback")) {
                if (split.length > 2) {
                    RollbackInterpreter interpreter = new RollbackInterpreter(player, split, plugin.getServer());
                    interpreter.interpret();
                } else {
                    // TODO Better help
                    player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb rollback <player>");
                    player.sendMessage(" or " + ChatColor.RED + "/bb rollback <player> <player1> <player2> ...");
                }
                event.setCancelled(true);
            } else if (split[1].equalsIgnoreCase("undo")) {
                if (split.length == 2) {
                    if (Rollback.canUndo()) {
                        int size = Rollback.undoSize();
                        player.sendMessage(BigBrother.premessage + "Undo-ing last rollback of " + size + " blocks");
                        Rollback.undo(plugin.getServer(), player);
                        player.sendMessage(BigBrother.premessage + "Undo successful");
                    } else {
                        player.sendMessage(BigBrother.premessage + "No rollback to undo");
                    }
                } else {
                    player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb undo");
                }
                event.setCancelled(true);
            } else if (split[1].equalsIgnoreCase("here")) {
                if (split.length == 2) {
                    Finder finder = new Finder(player.getLocation());
                    finder.addReciever(player);
                    finder.find();
                } else if (isNumber(split[2]) && split.length == 3) {
                    Finder finder = new Finder(player.getLocation());
                    finder.setRadius(Double.parseDouble(split[2]));
                    finder.addReciever(player);
                    finder.find();
                } else if (split.length == 3) {
                    Finder finder = new Finder(player.getLocation());
                    finder.addReciever(player);
                    List<Player> targets = plugin.getServer().matchPlayer(split[2]);
                    Player findee = null;
                    if (targets.size() == 1) {
                        findee = targets.get(0);
                    }
                    finder.find((findee == null) ? split[2] : findee.getName());
                } else if (isNumber(split[3]) && split.length == 4) {
                    Finder finder = new Finder(player.getLocation());
                    finder.setRadius(Double.parseDouble(split[3]));
                    finder.addReciever(player);
                    List<Player> targets = plugin.getServer().matchPlayer(split[2]);
                    Player findee = null;
                    if (targets.size() == 1) {
                        findee = targets.get(0);
                    }
                    finder.find((findee == null) ? split[2] : findee.getName());
                } else {
                    player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb here");
                    player.sendMessage("or " + ChatColor.RED + "/bb here <radius>");
                    player.sendMessage("or " + ChatColor.RED + "/bb here <name>");
                    player.sendMessage("or " + ChatColor.RED + "/bb here <name> <radius>");
                }
                event.setCancelled(true);
            } else if (split[1].equalsIgnoreCase("find")) {
                if (split.length == 5 && isNumber(split[2]) && isNumber(split[3]) && isNumber(split[4])) {
                    World currentWorld = player.getWorld();
                    Location loc = new Location(currentWorld, Double.parseDouble(split[2]), Double.parseDouble(split[3]), Double.parseDouble(split[4]));
                    Finder finder = new Finder(loc);
                    finder.addReciever(player);
                    finder.find();
                } else if (split.length == 6 && isNumber(split[2]) && isNumber(split[3]) && isNumber(split[4]) && isNumber(split[5])) {
                    World currentWorld = player.getWorld();
                    Location loc = new Location(currentWorld, Double.parseDouble(split[2]), Double.parseDouble(split[3]), Double.parseDouble(split[4]));
                    Finder finder = new Finder(loc);
                    finder.setRadius(Double.parseDouble(split[5]));
                    finder.addReciever(player);
                    finder.find();
                } else if (split.length == 6 && isNumber(split[2]) && isNumber(split[3]) && isNumber(split[4])) {
                    World currentWorld = player.getWorld();
                    Location loc = new Location(currentWorld, Double.parseDouble(split[2]), Double.parseDouble(split[3]), Double.parseDouble(split[4]));
                    Finder finder = new Finder(loc);
                    finder.addReciever(player);
                    List<Player> targets = plugin.getServer().matchPlayer(split[5]);
                    Player findee = null;
                    if (targets.size() == 1) {
                        findee = targets.get(0);
                    }
                    finder.find((findee == null) ? split[5] : findee.getName());
                } else if (split.length == 7 && isNumber(split[2]) && isNumber(split[3]) && isNumber(split[4]) && isNumber(split[6])) {
                    World currentWorld = player.getWorld();
                    Location loc = new Location(currentWorld, Double.parseDouble(split[2]), Double.parseDouble(split[3]), Double.parseDouble(split[4]));
                    Finder finder = new Finder(loc);
                    finder.setRadius(Double.parseDouble(split[6]));
                    finder.addReciever(player);
                    List<Player> targets = plugin.getServer().matchPlayer(split[5]);
                    Player findee = null;
                    if (targets.size() == 1) {
                        findee = targets.get(0);
                    }
                    finder.find((findee == null) ? split[5] : findee.getName());
                } else {
                    player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb find <x> <y> <z>");
                    player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <radius>");
                    player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <name>");
                    player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <name> <radius>");
                }
                event.setCancelled(true);
            }
        }
        if (BBSettings.commands && plugin.watching(player)) {
            Command dataBlock = new Command(player, event.getMessage());
            dataBlock.send();
        }
    }

    public void onPlayerJoin(PlayerEvent event) {
        if (!plugin.haveSeen(event.getPlayer())) {
            plugin.markSeen(event.getPlayer());
            if (BBSettings.autoWatch) {
                plugin.watchPlayer(event.getPlayer());
            }
        }

        if (BBSettings.login && plugin.watching(event.getPlayer())) {
            Login dataBlock = new Login(event.getPlayer());
            dataBlock.send();
        }
    }

    public void onPlayerQuit(PlayerEvent event) {
        if (BBSettings.disconnect && plugin.watching(event.getPlayer())) {
            Disconnect dataBlock = new Disconnect(event.getPlayer());
            dataBlock.send();
        }
    }

    public void onPlayerTeleport(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (BBSettings.teleport && plugin.watching(event.getPlayer()) && distance(from, to) > 5) {
            Teleport dataBlock = new Teleport(event.getPlayer(), event.getTo());
            dataBlock.send();
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        if (BBSettings.chat && plugin.watching(event.getPlayer())) {
            Chat dataBlock = new Chat(event.getPlayer(), event.getMessage());
            dataBlock.send();
        }
    }

    public void onPlayerItem(PlayerItemEvent event) {
        if (BBSettings.blockPlace && plugin.watching(event.getPlayer()) && !event.isCancelled()) {
            int x;
            int y;
            int z;
            int type;
            PlacedBlock dataBlock;
            switch (event.getMaterial()) {
            case LAVA_BUCKET:
                x = event.getBlockClicked().getX() + event.getBlockFace().getModX();
                y = event.getBlockClicked().getY() + event.getBlockFace().getModY();
                z = event.getBlockClicked().getZ() + event.getBlockFace().getModZ();
                type = Material.LAVA.getID();
                dataBlock = new PlacedBlock(event.getPlayer(), x, y, z, type, 0);
                dataBlock.send();
                break;
            case WATER_BUCKET:
                x = event.getBlockClicked().getX() + event.getBlockFace().getModX();
                y = event.getBlockClicked().getY() + event.getBlockFace().getModY();
                z = event.getBlockClicked().getZ() + event.getBlockFace().getModZ();
                type = Material.WATER.getID();
                dataBlock = new PlacedBlock(event.getPlayer(), x, y, z, type, 0);
                dataBlock.send();
                break;
            case BUCKET:
                BrokenBlock dataBlock2;

                switch (event.getBlockClicked().getType()) {
                case STATIONARY_LAVA:
                case LAVA:
                    x = event.getBlockClicked().getX();
                    y = event.getBlockClicked().getY();
                    z = event.getBlockClicked().getZ();
                    type = Material.LAVA.getID();
                    dataBlock2 = new BrokenBlock(event.getPlayer(), x, y, z, type, 0);
                    dataBlock2.send();
                    break;
                case STATIONARY_WATER:
                case WATER:
                    x = event.getBlockClicked().getX();
                    y = event.getBlockClicked().getY();
                    z = event.getBlockClicked().getZ();
                    type = Material.WATER.getID();
                    dataBlock2 = new BrokenBlock(event.getPlayer(), x, y, z, type, 0);
                    dataBlock2.send();
                }
                break;
            }
        }
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isNumber(String string) {
        try {
            Double.parseDouble(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private double distance(Location from, Location to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }

}

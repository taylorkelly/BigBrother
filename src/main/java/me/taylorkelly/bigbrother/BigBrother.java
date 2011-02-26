/*
 * BigBrother (http://github.com/tkelly910/BigBrother)
 * Copyright (C) 2010 Taylor Kelly (tkelly), OniTux, N3X15
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.taylorkelly.bigbrother;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.bigbrother.datasource.DataBlockSender;
import me.taylorkelly.bigbrother.finder.Finder;
import me.taylorkelly.bigbrother.finder.Sticker;
import me.taylorkelly.bigbrother.fixes.Fix;
import me.taylorkelly.bigbrother.fixes.Fix13;
import me.taylorkelly.bigbrother.fixes.Fix14;
import me.taylorkelly.bigbrother.fixes.Fix3;
import me.taylorkelly.bigbrother.griefcraft.util.Updater;
import me.taylorkelly.bigbrother.listeners.BBBlockListener;
import me.taylorkelly.bigbrother.listeners.BBEntityListener;
import me.taylorkelly.bigbrother.listeners.BBPlayerListener;
import me.taylorkelly.bigbrother.rollback.Rollback;
import me.taylorkelly.bigbrother.rollback.RollbackConfirmation;
import me.taylorkelly.bigbrother.rollback.RollbackInterpreter;
import me.taylorkelly.util.Numbers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BigBrother extends JavaPlugin {

    private BBPlayerListener playerListener;
    private BBBlockListener blockListener;
    private BBEntityListener entityListener;
    private StickListener stickListener;
    private Watcher watcher;
    private Sticker sticker;
    private WorldManager worldManager;
    public String name;
    public String version;
    public final static String premessage = ChatColor.AQUA + "[BBROTHER]: " + ChatColor.WHITE;
    private Updater updater;
    // Disabled until we can get a better way
    //private Rollback currentRollback = null;

    @Override
    public void onDisable() {
        DataBlockSender.disable();
    }

    @Override
    public void onEnable() {
        // Stuff that was in Constructor
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

        // Initialize Settings - Needs to come pretty much first
        BBSettings.initialize(getDataFolder());

        // Download dependencies...
        updater = new Updater();
        try {
            updater.check();
            updater.update();
        } catch (Throwable e) {
            BBLogging.severe("Could not download dependencies", e);
        }

        // Create Connection
        Connection conn = ConnectionManager.createConnection();
        if (conn == null) {
            BBLogging.severe("Could not establish SQL connection. Disabling BigBrother");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            try {
                conn.close();
            } catch (SQLException e) {
                BBLogging.severe("Could not close connection", e);
            }
        }

        // Initialize tables
        BBDataTable.initialize();
        worldManager = new WorldManager();

        //Check for old records
        if (Cleanser.needsCleaning()) {
            Cleanser.clean();
        }

        // Initialize Listeners
        playerListener = new BBPlayerListener(this);
        blockListener = new BBBlockListener(this);
        entityListener = new BBEntityListener(this);
        stickListener = new StickListener(this);
        sticker = new Sticker(getServer(), worldManager);

        // Update settings from old versions of BB
        if (new File("BigBrother").exists()) {
            updateSettings(getDataFolder());
        } else if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Apply fixes to DB for old BB
        Fix fix = new Fix13(getDataFolder());
        fix.apply();
        Fix fix2 = new Fix14(getDataFolder());
        fix2.apply();
        Fix fix3 = new Fix3(getDataFolder());
        fix3.apply();

        // Initialize Permissions, Stats
        BBPermissions.initialize(getServer());
        Stats.initialize();

        // Register Events
        registerEvents();

        // Initialize Player Watching
        watcher = BBSettings.getWatcher(getServer(), getDataFolder());

        // Initialize DataBlockSender
        DataBlockSender.initialize(getDataFolder(), worldManager);

        // Done!
        BBLogging.info(name + " " + version + " enabled");
    }

    private void updateSettings(File dataFolder) {
        File oldDirectory = new File("BigBrother");
        dataFolder.getParentFile().mkdirs();
        oldDirectory.renameTo(dataFolder);
    }

    private void registerEvents() {
        // TODO Only register events that are being listened to
        // Movement used for lag avoidance when rolling back. Disabled until we can get a better way -tkelly
        //getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);

        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.LEAVES_DECAY, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_FLOW, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Monitor, this);

        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Monitor, this);

        // These events are used for Super Sticks
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICKED, stickListener, Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, stickListener, Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_INTERACT, stickListener, Priority.Low, this);
    }

    public boolean watching(Player player) {
        return watcher.watching(player);
    }

    public boolean toggleWatch(String player) {
        return watcher.toggleWatch(player);
    }

    /**
     * On ANY event we can get our grubby little hands on, try and process a chunk of rollbacks at a time.
     */
    //Disabled until we can get a better way
//    public void processPsuedotick() {
//        // Anything in queue?
//        if (currentRollback != null) {
//            // Deal with it.
//            if (currentRollback.nextPass()) {
//                getServer().broadcastMessage(BigBrother.premessage + "Rollback complete!");
//
//                // Now that we're all done, make room for the next one!
//                currentRollback = null;
//            }
//        }
//    }
    public String getWatchedPlayers() {
        return watcher.getWatchedPlayers();
    }

    public boolean haveSeen(Player player) {
        return watcher.haveSeen(player);
    }

    public void markSeen(Player player) {
        watcher.markSeen(player);
    }

    public void watchPlayer(Player player) {
        watcher.watchPlayer(player);
    }

    public String getUnwatchedPlayers() {
        return watcher.getUnwatchedPlayers();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        try {
            String[] split = args;
            String commandName = command.getName().toLowerCase();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                // TODO: Make this more modular.  If-trees drive me nuts. - N3X
                if (commandName.equals("bb")) {
                    if (split.length == 0) {
                        return false;
                    }
                    if (split[0].equalsIgnoreCase("watch") && BBPermissions.watch(player)) {
                        if (split.length == 2) {
                            List<Player> targets = getServer().matchPlayer(split[1]);
                            Player watchee = null;
                            if (targets.size() == 1) {
                                watchee = targets.get(0);
                            }
                            String playerName = (watchee == null) ? split[1] : watchee.getName();

                            if (toggleWatch(playerName)) {
                                String status = (watchee == null) ? " (offline)" : " (online)";
                                player.sendMessage(BigBrother.premessage + "Now watching " + playerName + status);
                            } else {
                                String status = (watchee == null) ? " (offline)" : " (online)";
                                player.sendMessage(BigBrother.premessage + "No longer watching " + playerName + status);
                            }
                        } else {
                            player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb watch <player>");
                        }
                    } else if (split[0].equalsIgnoreCase("watched") && BBPermissions.info(player)) {
                        String watchedPlayers = getWatchedPlayers();
                        if (watchedPlayers.equals("")) {
                            player.sendMessage(BigBrother.premessage + "Not watching anyone.");
                        } else {
                            player.sendMessage(BigBrother.premessage + "Now watching:");
                            player.sendMessage(watchedPlayers);
                        }
                    } else if (split[0].equalsIgnoreCase("stats") && BBPermissions.info(player)) {
                        Stats.report(player);
                    } else if (split[0].equalsIgnoreCase("unwatched") && BBPermissions.info(player)) {
                        String unwatchedPlayers = getUnwatchedPlayers();
                        if (unwatchedPlayers.equals("")) {
                            player.sendMessage(BigBrother.premessage + "Everyone on is being watched.");
                        } else {
                            player.sendMessage(BigBrother.premessage + "Currently not watching:");
                            player.sendMessage(unwatchedPlayers);
                        }
                    } else if (split[0].equalsIgnoreCase("cleanse") && BBPermissions.cleanse(player)) {
                        if (Cleanser.needsCleaning()) {
                            Cleanser.clean(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "No need to cleanse. Check your settings.");
                        }
                    } else if (split[0].equalsIgnoreCase("rollback") && BBPermissions.rollback(player)) {
                        if (split.length > 1) {
                            RollbackInterpreter interpreter = new RollbackInterpreter(player, split, getServer(), worldManager);
                            Boolean passed = interpreter.interpret();
                            if (passed != null) {
                                if (passed) {
                                    interpreter.send(); //Readded
                                    //if (currentRollback == null) {
                                    //    currentRollback = interpreter.getAndInitializeRollback();
                                    //} else {
                                    //    player.sendMessage(BigBrother.premessage + "Rollback already in progress, please wait for it to complete!");
                                    //}
                                } else {
                                    //if (currentRollback != null) {
                                    //    player.sendMessage(BigBrother.premessage + "Rollback already in progress, please wait for it to complete!");
                                    //} else {
                                    player.sendMessage(BigBrother.premessage + ChatColor.RED + "Warning: " + ChatColor.WHITE + "You are rolling back without a time or radius argument.");
                                    player.sendMessage("Use " + ChatColor.RED + "/bb confirm" + ChatColor.WHITE + " to confirm the rollback.");
                                    player.sendMessage("Use " + ChatColor.RED + "/bb delete" + ChatColor.WHITE + " to delete it.");
                                    RollbackConfirmation.setRI(player, interpreter);
                                    //}
                                }
                            }
                        } else {
                            player.sendMessage(BigBrother.premessage + "Usage is: " + ChatColor.RED + "/bb rollback name1 [name2] [options]");
                            player.sendMessage(BigBrother.premessage + "Please read the full command documentation at https://github.com/tkelly910/BigBrother/wiki/Commands");
                        }
                    } else if (split[0].equalsIgnoreCase("confirm") && BBPermissions.rollback(player)) {
                        if (split.length == 1) {
                            if (RollbackConfirmation.hasRI(player)) {
                                RollbackInterpreter interpret = RollbackConfirmation.getRI(player);
                                interpret.send(); //Readded
                                //currentRollback = interpret.getAndInitializeRollback();
                            } else {
                                player.sendMessage(BigBrother.premessage + "You have no rollback to confirm.");
                            }
                        } else {
                            player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb confirm");
                        }

                    } else if (split[0].equalsIgnoreCase("delete") && BBPermissions.rollback(player)) {
                        if (split.length == 1) {
                            if (RollbackConfirmation.hasRI(player)) {
                                RollbackConfirmation.deleteRI(player);
                                player.sendMessage(BigBrother.premessage + "You have deleted your rollback.");
                            } else {
                                player.sendMessage(BigBrother.premessage + "You have no rollback to delete.");
                            }
                        } else {
                            player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb delete");
                        }
                    } else if (split[0].equalsIgnoreCase("undo") && BBPermissions.rollback(player)) {
                        if (split.length == 1) {
                            if (Rollback.canUndo()) {
                                int size = Rollback.undoSize();
                                player.sendMessage(BigBrother.premessage + "Undo-ing last rollback of " + size + " blocks");
                                Rollback.undo(getServer(), player);
                                player.sendMessage(BigBrother.premessage + "Undo successful");
                            } else {
                                player.sendMessage(BigBrother.premessage + "No rollback to undo");
                            }
                        } else {
                            player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb undo");
                        }
                    } else if (split[0].equalsIgnoreCase("stick") && BBPermissions.info(player)) {
                        if (split.length == 1) {
                            sticker.setMode(player, 1);
                            player.sendMessage(BigBrother.premessage + "Your current stick mode is " + sticker.descMode(player));
                            player.sendMessage("Use " + ChatColor.RED + "/bb stick 0" + ChatColor.WHITE + " to turn it off");
                        } else if (split.length == 2 && Numbers.isInteger(split[1])) {
                            sticker.setMode(player, Integer.parseInt(split[1]));
                            if (Integer.parseInt(split[1]) > 0) {
                                player.sendMessage(BigBrother.premessage + "Your current stick mode is " + sticker.descMode(player));
                                player.sendMessage("Use " + ChatColor.RED + "/bb stick 0" + ChatColor.WHITE + " to turn it off");
                            }
                        } else {
                            player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb stick (#)");
                        }
                    } else if (split[0].equalsIgnoreCase("here") && BBPermissions.info(player)) {
                        if (split.length == 1) {
                            Finder finder = new Finder(player.getLocation(), getServer().getWorlds(), worldManager);
                            finder.addReciever(player);
                            finder.find();
                        } else if (Numbers.isNumber(split[1]) && split.length == 2) {
                            Finder finder = new Finder(player.getLocation(), getServer().getWorlds(), worldManager);
                            finder.setRadius(Double.parseDouble(split[1]));
                            finder.addReciever(player);
                            finder.find();
                        } else if (split.length == 2) {
                            Finder finder = new Finder(player.getLocation(), getServer().getWorlds(), worldManager);
                            finder.addReciever(player);
                            List<Player> targets = getServer().matchPlayer(split[1]);
                            Player findee = null;
                            if (targets.size() == 1) {
                                findee = targets.get(0);
                            }
                            finder.find((findee == null) ? split[1] : findee.getName());
                        } else if (Numbers.isNumber(split[2]) && split.length == 3) {
                            Finder finder = new Finder(player.getLocation(), getServer().getWorlds(), worldManager);
                            finder.setRadius(Double.parseDouble(split[2]));
                            finder.addReciever(player);
                            List<Player> targets = getServer().matchPlayer(split[1]);
                            Player findee = null;
                            if (targets.size() == 1) {
                                findee = targets.get(0);
                            }
                            finder.find((findee == null) ? split[1] : findee.getName());
                        } else {
                            player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb here");
                            player.sendMessage("or " + ChatColor.RED + "/bb here <radius>");
                            player.sendMessage("or " + ChatColor.RED + "/bb here <name>");
                            player.sendMessage("or " + ChatColor.RED + "/bb here <name> <radius>");
                        }
                    } else if (split[0].equalsIgnoreCase("find") && BBPermissions.info(player)) {
                        if (split.length == 4 && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3])) {
                            World currentWorld = player.getWorld();
                            Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                            Finder finder = new Finder(loc, getServer().getWorlds(), worldManager);
                            finder.addReciever(player);
                            finder.find();
                        } else if (split.length == 5 && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3]) && Numbers.isNumber(split[4])) {
                            World currentWorld = player.getWorld();
                            Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                            Finder finder = new Finder(loc, getServer().getWorlds(), worldManager);
                            finder.setRadius(Double.parseDouble(split[4]));
                            finder.addReciever(player);
                            finder.find();
                        } else if (split.length == 5 && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3])) {
                            World currentWorld = player.getWorld();
                            Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                            Finder finder = new Finder(loc, getServer().getWorlds(), worldManager);
                            finder.addReciever(player);
                            List<Player> targets = getServer().matchPlayer(split[4]);
                            Player findee = null;
                            if (targets.size() == 1) {
                                findee = targets.get(0);
                            }
                            finder.find((findee == null) ? split[4] : findee.getName());
                        } else if (split.length == 6 && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3]) && Numbers.isNumber(split[5])) {
                            World currentWorld = player.getWorld();
                            Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                            Finder finder = new Finder(loc, getServer().getWorlds(), worldManager);
                            finder.setRadius(Double.parseDouble(split[5]));
                            finder.addReciever(player);
                            List<Player> targets = getServer().matchPlayer(split[4]);
                            Player findee = null;
                            if (targets.size() == 1) {
                                findee = targets.get(0);
                            }
                            finder.find((findee == null) ? split[4] : findee.getName());
                        } else {
                            player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb find <x> <y> <z>");
                            player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <radius>");
                            player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <name>");
                            player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <name> <radius>");
                        }
                    } else if (split[0].equalsIgnoreCase("help")) {
                        // TODO: Modular help system, prereq: modular commands
                        player.sendMessage(BigBrother.premessage + "BigBrother version " + version + " help");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb watch (name)" + ChatColor.WHITE + " - Toggles the whether BB is watching (name)");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb watched" + ChatColor.WHITE + " - Shows which users are being watched by BB");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb unwatched" + ChatColor.WHITE + " - Shows which users that are logged in are unwatched");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb stick (0|1|2)" + ChatColor.WHITE + " - Gives you a stick (1), a log you can place (2), or disables either (0).");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb here" + ChatColor.WHITE + " - See changes that took place in the area you are standing in.");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb find x y z" + ChatColor.WHITE + " - Get the history of an area at a specific coordinate");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb rollback (name1) [name2] [options]" + ChatColor.WHITE + " - A command you should study in length via our helpful online wiki.");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb undo" + ChatColor.WHITE + " - Great for fixing bad rollbacks. It's like it never happened!");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb confirm" + ChatColor.WHITE + " - Confirms your rollback (if applicable).");
                        player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb delete" + ChatColor.WHITE + " - Delete your rollback (if applicable).");
                    } else {
                        return false;
                    }
                    return true;
                }
            }
            return false;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return true;
        }
    }

    public boolean hasStick(Player player, ItemStack itemStack) {
        return sticker.hasStick(player, itemStack);
    }

    public void stick(Player player, Block block) {
        sticker.stick(player, block);
    }

    public boolean rightClickStick(Player player) {
        return sticker.rightClickStick(player);
    }
}

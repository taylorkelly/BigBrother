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
import java.util.List;

import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.bigbrother.datasource.DataBlockSender;
import me.taylorkelly.bigbrother.finder.Finder;
import me.taylorkelly.bigbrother.finder.Sticker;
import me.taylorkelly.bigbrother.fixes.Fix;
import me.taylorkelly.bigbrother.fixes.Fix1;
import me.taylorkelly.bigbrother.fixes.Fix2;
import me.taylorkelly.bigbrother.fixes.Fix3;
import me.taylorkelly.bigbrother.fixes.Fix4;
import me.taylorkelly.bigbrother.griefcraft.util.Updater;
import me.taylorkelly.bigbrother.listeners.BBBlockListener;
import me.taylorkelly.bigbrother.listeners.BBEntityListener;
import me.taylorkelly.bigbrother.listeners.BBPlayerListener;
import me.taylorkelly.bigbrother.rollback.Rollback;
import me.taylorkelly.bigbrother.rollback.RollbackConfirmation;
import me.taylorkelly.bigbrother.rollback.RollbackInterpreter;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.util.Numbers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
    public static String name;
    public static String version;
    public final static String premessage = ChatColor.AQUA + "[BBROTHER]: " + ChatColor.WHITE;
    private Updater updater;

    @Override
    public void onDisable() {
        DataBlockSender.disable(this);
    }

    @Override
    public void onEnable() {
        BBLogging.debug("Debug Mode enabled");
        // Stuff that was in Constructor
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

        // Initialize Settings - Needs to come pretty much first
        BBSettings.initialize(getDataFolder());

        // Download dependencies...
        if (BBSettings.libraryAutoDownload) {
            updater = new Updater();
            try {
                updater.check();
                updater.update();
            } catch (Throwable e) {
                BBLogging.severe("Could not download dependencies", e);
            }
        } else {
            BBLogging.debug("Downloading libraries was skipped");
        }

        // Create Connection
        if (!ConnectionManager.createConnection(this)) {
            BBLogging.severe("Error getting a connection, disabling BigBrother...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Connection conn = ConnectionManager.getConnection();
        if (conn == null) {
            BBLogging.severe("Could not establish SQL connection. Disabling BigBrother");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            ConnectionManager.cleanup("onEnable", conn, null, null);
        }

        // Initialize tables
        BBDataTable.initialize();
        worldManager = new WorldManager();

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
        Fix fix = new Fix1(getDataFolder());
        fix.apply();
        Fix fix2 = new Fix2(getDataFolder());
        fix2.apply();
        Fix fix3 = new Fix3(getDataFolder()); // 26 Feb
        fix3.apply();
        Fix fix4 = new Fix4(getDataFolder()); // 5 March, 2011 - N3X
        fix4.apply();

        // Initialize Permissions, Help
        BBPermissions.initialize(getServer());
        BBHelp.initialize(this);

        // Register Events
        registerEvents();

        // Initialize Player Watching
        watcher = BBSettings.getWatcher(getServer(), getDataFolder());

        // Initialize DataBlockSender
        DataBlockSender.initialize(this, getDataFolder(), worldManager);

        // Initialize Cleanser
        Cleanser.initialize(this);

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
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
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
        String[] split = args;
        String commandName = command.getName().toLowerCase();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // TODO: Make this more modular.  If-trees drive me nuts. - N3X
            if (commandName.equals("bb")) {
                if (split.length == 0) {
                    return false;
                } else if (split[0].equalsIgnoreCase("version")) {
                    player.sendMessage("You're running: " + ChatColor.AQUA.toString() + name + " " + version);
                } else if (split[0].equalsIgnoreCase("update")) {
                    Updatr.updateAvailable(player);
                } else if (split[0].equalsIgnoreCase("watch") && BBPermissions.watch(player)) {
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
                        RollbackInterpreter interpreter = new RollbackInterpreter(player, split, getServer(), worldManager, this);
                        Boolean passed = interpreter.interpret();
                        if (passed != null) {
                            if (passed) {
                                interpreter.send();
                            } else {
                                player.sendMessage(BigBrother.premessage + ChatColor.RED + "Warning: " + ChatColor.WHITE + "You are rolling back without a time or radius argument.");
                                player.sendMessage("Use " + ChatColor.RED + "/bb confirm" + ChatColor.WHITE + " to confirm the rollback.");
                                player.sendMessage("Use " + ChatColor.RED + "/bb delete" + ChatColor.WHITE + " to delete it.");
                                RollbackConfirmation.setRI(player, interpreter);
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
                            interpret.send();
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
                    // Undo rollback.
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
                        player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb undo");
                    }
                    // Report changes made to solid block.
                    // /bb stick [1]
                } else if (split[0].equalsIgnoreCase("stick") && BBPermissions.info(player)) {
                    if (split.length == 1) {
                        sticker.setMode(player, 1);
                        reportStickMode(player, 1);
                    } else if (split.length == 2 && Numbers.isInteger(split[1])) {
                        sticker.setMode(player, Integer.parseInt(split[1]));
                        reportStickMode(player, Integer.parseInt(split[1]));
                    } else {
                        player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb stick [#]");
                    }
                    // Report changes made to non-solid block
                    // /bb log == /bb stick 2
                } else if (split[0].equalsIgnoreCase("log") && BBPermissions.info(player)) {
                    if (split.length == 1) {
                        sticker.setMode(player, 2);
                        reportStickMode(player, 2);
                    } else {
                        player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb log");
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
        } else if (sender instanceof ConsoleCommandSender) {
            if (commandName.equals("bb")) {
                ConsoleCommandSender console = (ConsoleCommandSender) sender;
                if (split.length == 0) {
                    return false;
                } else if (split[0].equalsIgnoreCase("version")) {
                    console.sendMessage("You're running: " + ChatColor.AQUA.toString() + name + " " + version);
                } else if (split[0].equalsIgnoreCase("update")) {
                    Updatr.updateAvailable(console);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Tell the user what mode their stick is.
     *
     * Better than having this copypasted 8 times
     * @param player Player to talk to about their stick/log
     * @author N3X15
     */
    private void reportStickMode(Player player, int stickLevel) {
        if (stickLevel > 0) {
            player.sendMessage(BigBrother.premessage + "Your current stick mode is " + sticker.descMode(player));
            player.sendMessage("Use " + ChatColor.RED + "/bb stick 0" + ChatColor.WHITE + " to turn it off");
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

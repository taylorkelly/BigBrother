package me.taylorkelly.bigbrother;

import java.io.*;
import java.util.List;
import java.util.logging.*;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.bigbrother.datasource.DataBlockSender;
import me.taylorkelly.bigbrother.finder.Finder;
import me.taylorkelly.bigbrother.fixes.Fix;
import me.taylorkelly.bigbrother.fixes.Fix13;
import me.taylorkelly.bigbrother.fixes.Fix14;
import me.taylorkelly.bigbrother.listeners.BBBlockListener;
import me.taylorkelly.bigbrother.listeners.BBEntityListener;
import me.taylorkelly.bigbrother.listeners.BBPlayerListener;
import me.taylorkelly.bigbrother.rollback.Rollback;
import me.taylorkelly.bigbrother.rollback.RollbackInterpreter;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.*;

import com.griefcraft.util.Updater;


public class BigBrother extends JavaPlugin {
    private BBPlayerListener playerListener;
    private BBBlockListener blockListener;
    private BBEntityListener entityListener;
    private Watcher watcher;

    public static Logger log;
    public final String name = this.getDescription().getName();
    public final String version = this.getDescription().getVersion();
    public final static String premessage = ChatColor.AQUA + "[BBROTHER]: " + ChatColor.WHITE;

    private Updater updater;

    public BigBrother(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        updater = new Updater();
        playerListener = new BBPlayerListener(this);
        blockListener = new BBBlockListener(this);
        entityListener = new BBEntityListener(this);

    }

    public void onDisable() {
        DataBlockSender.disable();
        ConnectionManager.freeConnection();
    }

    public void onEnable() {
        log = Logger.getLogger("Minecraft");

        try {
            updater.check();
            updater.update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (new File("BigBrother").exists()) {
            updateSettings(getDataFolder());
        } else if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        registerEvents();
        BBSettings.initialize(getDataFolder());
        watcher = BBSettings.getWatcher(getServer(), getDataFolder());
        BBDataBlock.initialize();
        DataBlockSender.initialize(getDataFolder());
        Fix fix = new Fix13(getDataFolder());
        fix.apply();
        Fix fix2 = new Fix14(getDataFolder());
        fix2.apply();
        log.info(name + " " + version + " initialized");
    }

    private void updateSettings(File dataFolder) {
        File oldDirectory = new File("BigBrother");
        dataFolder.getParentFile().mkdirs();
        oldDirectory.renameTo(dataFolder);
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);

        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.LEAVES_DECAY, blockListener, Priority.Monitor, this);
        
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Monitor, this);

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

    public boolean onCommand(Player player, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();

        // TODO permissions
        if (commandName.equals("bb") && player.isOp()) {
            if (split[0].equalsIgnoreCase("watch")) {
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
            } else if (split[0].equalsIgnoreCase("watched")) {
                String watchedPlayers = getWatchedPlayers();
                if (watchedPlayers.equals("")) {
                    player.sendMessage(BigBrother.premessage + "Not currently watching anyone.");
                } else {
                    player.sendMessage(BigBrother.premessage + "Now watching:");
                    player.sendMessage(watchedPlayers);
                }
            } else if (split[0].equalsIgnoreCase("unwatched")) {
                String unwatchedPlayers = getUnwatchedPlayers();
                if (unwatchedPlayers.equals("")) {
                    player.sendMessage(BigBrother.premessage + "Everyone on is being watched.");
                } else {
                    player.sendMessage(BigBrother.premessage + "Not currently watching:");
                    player.sendMessage(unwatchedPlayers);
                }
            } else if (split[0].equalsIgnoreCase("rollback")) {
                if (split.length > 1) {
                    RollbackInterpreter interpreter = new RollbackInterpreter(player, split, getServer());
                    interpreter.interpret();
                } else {
                    // TODO Better help
                    player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb rollback <arg1> <arg2>");
                }
            } else if (split[0].equalsIgnoreCase("undo")) {
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
            } else if (split[0].equalsIgnoreCase("here")) {
                if (split.length == 1) {
                    Finder finder = new Finder(player.getLocation());
                    finder.addReciever(player);
                    finder.find();
                } else if (isNumber(split[1]) && split.length == 2) {
                    Finder finder = new Finder(player.getLocation());
                    finder.setRadius(Double.parseDouble(split[1]));
                    finder.addReciever(player);
                    finder.find();
                } else if (split.length == 2) {
                    Finder finder = new Finder(player.getLocation());
                    finder.addReciever(player);
                    List<Player> targets = getServer().matchPlayer(split[1]);
                    Player findee = null;
                    if (targets.size() == 1) {
                        findee = targets.get(0);
                    }
                    finder.find((findee == null) ? split[1] : findee.getName());
                } else if (isNumber(split[2]) && split.length == 3) {
                    Finder finder = new Finder(player.getLocation());
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
            } else if (split[0].equalsIgnoreCase("find")) {
                if (split.length == 4 && isNumber(split[1]) && isNumber(split[2]) && isNumber(split[3])) {
                    World currentWorld = player.getWorld();
                    Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                    Finder finder = new Finder(loc);
                    finder.addReciever(player);
                    finder.find();
                } else if (split.length == 5 && isNumber(split[1]) && isNumber(split[2]) && isNumber(split[3]) && isNumber(split[4])) {
                    World currentWorld = player.getWorld();
                    Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                    Finder finder = new Finder(loc);
                    finder.setRadius(Double.parseDouble(split[4]));
                    finder.addReciever(player);
                    finder.find();
                } else if (split.length == 5 && isNumber(split[1]) && isNumber(split[2]) && isNumber(split[3])) {
                    World currentWorld = player.getWorld();
                    Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                    Finder finder = new Finder(loc);
                    finder.addReciever(player);
                    List<Player> targets = getServer().matchPlayer(split[4]);
                    Player findee = null;
                    if (targets.size() == 1) {
                        findee = targets.get(0);
                    }
                    finder.find((findee == null) ? split[4] : findee.getName());
                } else if (split.length == 6 && isNumber(split[1]) && isNumber(split[2]) && isNumber(split[3]) && isNumber(split[5])) {
                    World currentWorld = player.getWorld();
                    Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                    Finder finder = new Finder(loc);
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
                player.sendMessage(BigBrother.premessage + "help!");
            } else {
                return false;
            }
            return true;
        }
        return false;
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
}
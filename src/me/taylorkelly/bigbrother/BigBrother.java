package me.taylorkelly.bigbrother;

import java.io.*;
import java.util.logging.*;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import org.bukkit.*;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.*;

public class BigBrother extends JavaPlugin {
    private BBPlayerListener playerListener;
    private BBBlockListener blockListener;
    private Watcher watcher;

    public static Logger log;
    public final static String name = "BigBrother";
    public final static String version = "1.0";
    public final static String premessage = Color.AQUA + "[BBROTHER]: " + Color.WHITE;
    public final static String directory = "BigBrother";

    public BigBrother(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, plugin, cLoader);
    }

    public void onDisable() {
    }

    public void onEnable() {
        log = Logger.getLogger("Minecraft");
        if (!new File(directory).exists()) {
            try {
                (new File(directory)).mkdir();
            } catch (Exception e) {
                BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Unable to create bigbrother/ directory");
            }
        }
        playerListener = new BBPlayerListener(this);
        blockListener = new BBBlockListener(this);
        registerEvents();
        BBSettings.initialize();
        watcher = BBSettings.getWatcher(getServer());
        BBDataBlock.initialize();
        log.info(name + " " + version + " initialized");
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal, this);
        // getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICK,
        // playerListener, Priority.Normal, this);
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
}
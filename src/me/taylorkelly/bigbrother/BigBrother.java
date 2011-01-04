package me.taylorkelly.bigbrother;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;

import org.bukkit.*;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.*;

public class BigBrother extends JavaPlugin {
	private final BBPlayerListener playerListener;
	private final BBBlockListener blockListener;

	public static Logger log;
	public final static String name = "BigBrother";
	public final static String version = "1.0";
	public final static String premessage = Color.AQUA + "[BBROTHER]: "
			+ Color.WHITE;
	public final static String directory = "bigbrother";
	
	public BigBrother(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, plugin, cLoader);
		
		initialize();
    	playerListener = new BBPlayerListener(this);
    	blockListener = new BBBlockListener(this);
        registerEvents();
		BBLogger.initialize();
		BBSettings.initialize();
		BBDataBlock.initialize();
		log = Logger.getLogger("Minecraft");
		log.info(name + " " + version + " initialized");
	}
	
	public void onDisable() {}

	public void onEnable() {}
	
	private void initialize() {
		if (!new File(directory).exists()) {
			try {
				(new File(directory)).mkdir();
			} catch (Exception e) {
				BigBrother.log.log(Level.SEVERE,
						"[BBROTHER]: Unable to create bigbrother/ directory");
			}
		}		
	}

    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        //getServer().getPluginManager().registerEvent(Event.Type.SIGN_BUILT, playerListener, Priority.Normal, this); Sign hook
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal, this);
        //getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICK, playerListener, Priority.Normal, this); rightclick hook
    }

	public boolean watching(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean toggleWatch(String playerName) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getWatchedPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean haveSeen(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	public void markSeen(Player player) {
		// TODO Auto-generated method stub
		
	}

	public void watchPlayer(Player player) {
		// TODO Auto-generated method stub
		
	}

	public String getUnwatchedPlayers() {
		// TODO Auto-generated method stub
		return null;
	}
}
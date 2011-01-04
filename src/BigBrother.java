import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.*;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.*;

public class BigBrother extends JavaPlugin {
	private final BBPlayerListener playerListener = new BBPlayerListener();
	private final BBBlockListener blockListener = new BBBlockListener();

	public static Logger log;
	public static String name = "BigBrother";
	public static String version = "1.0";
	public static String premessage = Color.AQUA + "[BBROTHER]: "
			+ Color.WHITE;
	
	public BigBrother(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, plugin, cLoader);
		
        registerEvents();
		BBLogger.initialize();
		BBSettings.initialize();
		BBData.initialize();
		log = Logger.getLogger("Minecraft");
		log.info(name + " " + version + " initialized");
	}
	
	public void onDisable() {}

	public void onEnable() {}
	

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
	
	static boolean toggleWatch(String player) {
		boolean watching = false;

		if (BBSettings.watchList.contains(player)) {
			BBSettings.watchList.remove(player);
			if (BBSettings.verbose) {
				BBLogger.log(player, (BBSettings.getTime()
						+ ": No longer watching " + player + "\n"));
				BBNotify.notify("No longer watching " + player + "\n");
			} else {
				BBLogger.log(player, BBSettings.getTime() + ": NotWatching\n");
				BBNotify.notify("Not watching " + player + "\n");
			}
			saveWatchList();
		} else {
			watch(player);
			watching = true;
		}
		return watching;
	}

	static void watch(String player) {
		if (!BBSettings.watchList.contains(player)) {
			BBSettings.watchList.add(player);
			if (BBSettings.verbose) {
				BBLogger.log(player, (BBSettings.getTime() + ": Now watching "
						+ player + "\n"));
				BBNotify.notify("Now watching " + player + "\n");
			} else {
				BBLogger.log(player, BBSettings.getTime() + ": NotWatching\n");
				BBNotify.notify("Watching " + player + "\n");
			}
			saveWatchList();
		}
	}

	private static void saveWatchList() {
		String list = "";

		for (String player : BBSettings.watchList) {
			list += player + ",";
		}

		if (list.startsWith(","))
			list = list.substring(1);
		if (list.endsWith(","))
			list = list.substring(0, list.length() - 1);

		PropertiesFile pf = new PropertiesFile("bigbrother.txt");
		try {
			pf.load();
		} catch (IOException e) {
		}
		pf.setString("watchedplayers", list);
		pf.save();
	}

	static void saveSeenPlayers() {
		String list = "";

		for (String player : BBSettings.seenPlayers) {
			list += player + ",";
		}

		if (list.startsWith(","))
			list = list.substring(1);
		if (list.endsWith(","))
			list = list.substring(0, list.length() - 1);

		PropertiesFile pf = new PropertiesFile("bigbrother.txt");
		try {
			pf.load();
		} catch (IOException e) {
		}
		pf.setString("seenPlayers", list);
		pf.save();
	}

	public boolean verifyAdmin(String apt, String input, Player heir) {
		return (heir.canUseCommand(apt) && input.equalsIgnoreCase(apt));
	}

	public static String fixName(String player) {
		return player.replace(".", "").replace(":", "").replace("<", "")
				.replace(">", "").replace("*", "").replace("\\", "")
				.replace("/", "").replace("?", "").replace("\"", "")
				.replace("|", "");
	}
}
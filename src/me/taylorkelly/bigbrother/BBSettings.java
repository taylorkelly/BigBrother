package me.taylorkelly.bigbrother;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Timer;
import java.util.logging.Level;

import org.bukkit.Server;

public class BBSettings {

	public static final int BLOCK_BROKEN = 0;
	public static final int BLOCK_PLACED = 1;
	public static final int SIGN_TEXT = 2;
	public static final int TELEPORT = 3;
	public static final int DELTA_CHEST = 4;
	public static final int COMMAND = 5;
	public static final int CHAT = 6;
	public static final int DISCONNECT = 7;
	public static final int LOGIN = 8;
	public static final int DOOR_OPEN = 9;
	public static final int BUTTON_PRESS = 10;
	public static final int LEVER_SWITCH = 10;

	public static boolean blockBreak;
	public static boolean blockPlace;
	public static boolean teleport;
	public static boolean chestChanges;
	public static boolean commands;
	public static boolean chat;
	public static boolean disconnect;
	public static boolean login;
	public static boolean doorOpen;
	public static boolean buttonPress;
	public static boolean leverSwitch;

	public static boolean autoWatch;
	public static int defaultSearchRadius;
	public static DataDest dataDest;
	public static String mysqlUser = "root";
	public static String mysqlPass = "root";
	public static String mysqlDB = "jdbc:mysql://localhost:3306/minecraft";

	private static ArrayList<String> watchList;
	private static ArrayList<String> seenList;

	public static String liteDb = "jdbc:sqlite:" + BigBrother.directory + File.separator + "bigbrother.db";

	public static void initialize() {
		watchList = new ArrayList<String>();
		seenList = new ArrayList<String>();

		loadPropertiesFiles();
		loadLists();
	}

	private static void loadPropertiesFiles() {
		PropertiesFile pf = new PropertiesFile(new File(BigBrother.directory, "watching.properties"));
		blockBreak = pf.getBoolean("blockBreak", true, "Watch when players break blocks");
		blockPlace = pf.getBoolean("blockPlace", true, "Watch when players place blocks");
		teleport = pf.getBoolean("teleport", true, "Watch when players teleport around");
		chestChanges = pf.getBoolean("chestChanges", true, "Watch when players add/remove items from chests");
		commands = pf.getBoolean("commands", true, "Watch for all player commands");
		chat = pf.getBoolean("chat", false, "Watch for player chat");
		login = pf.getBoolean("login", false, "Watch for player logins");
		disconnect = pf.getBoolean("disconnect", false, "Watch for player disconnects");
		doorOpen = pf.getBoolean("doorOpen", false, "Watch for when player opens doors");
		buttonPress = pf.getBoolean("buttonPress", false, "Watch for when player pushes buttons");
		leverSwitch = pf.getBoolean("leverSwitch", false, "Watch for when player switches levers");
		pf.save();

		pf = new PropertiesFile(new File(BigBrother.directory, "BigBrother.properties"));
		autoWatch = pf.getBoolean("autoWatch", true, "Automatically start watching players");
		defaultSearchRadius = pf.getInt("defaultSearchRadius", 3, "Default search radius for bbhere and bbfind");
		boolean sqlite = pf.getBoolean("MySQL", false, "If true, uses MySQL. If false, uses Sqlite");
		boolean flatlog = pf.getBoolean("flatFileLogs", false, "If true, will also log actions to .logs (one for each player)");
		mysqlUser = pf.getString("mysqlUser", "root", "Username for MySQL db (if applicable)");
		mysqlPass = pf.getString("mysqlPass", "root", "Password for MySQL db (if applicable)");
		mysqlDB = pf.getString("mysqlDB", "jdbc:mysql://localhost:3306/minecraft", "DB for MySQL (if applicable)");
		pf.save();

		if (sqlite) {
			if (flatlog) {
				dataDest = DataDest.SQLITE_AND_FLAT;
			} else {
				dataDest = DataDest.SQLITE;
			}
		} else {
			if (flatlog) {
				dataDest = DataDest.MYSQL_AND_FLAT;
			} else {
				dataDest = DataDest.MYSQL;
			}
		}
	}

	private static void loadLists() {
		File file = new File(BigBrother.directory, "WatchedPlayers.txt");
		try {
			if (!file.exists())
				file.createNewFile();
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String player = sc.nextLine();
				if(player.contains(" ")) continue;
				watchList.add(player);
			}
		} catch (FileNotFoundException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Cannot read file " + file.getName());
		} catch (IOException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: IO Exception with file " + file.getName() + "");
		}
		
		file = new File(BigBrother.directory, "SeenPlayers.txt");
		try {
			if (!file.exists())
				file.createNewFile();
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String player = sc.nextLine();
				if(player.contains(" ")) continue;
				seenList.add(player);
			}
		} catch (FileNotFoundException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Cannot read file " + file.getName());
		} catch (IOException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: IO Exception with file " + file.getName() + "");
		}

	}

	public static Watcher getWatcher(Server server) {
		return new Watcher(watchList, seenList, server);
	}
}

package me.taylorkelly.bigbrother;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;


public class BBSettings {

	private static String format;
	public static ArrayList<String> watchList;
	public static boolean login;
	public static boolean logout;
	public static boolean position;
	public static boolean blockDestroying;
	public static boolean commands;
	public static boolean notifyMods;
	public static boolean chat;
	public static boolean verbose;
	public static boolean blockPlacing;
	public static ArrayList<Integer> watchedBlocks;
	public static boolean autoWatch;
	public static ArrayList<String> seenPlayers;
	public static ArrayList<String> optedOut;
	
	public static int defaultRadius;

	
	public static int hours;
	public static double hourCount;
	public static ArrayList<String> emails;
	public static Timer emailTimer;
	
	public static String driver = "com.mysql.jdbc.Driver";
	public static String username = "root";
	public static String password = "root";
	public static String db = "jdbc:mysql://localhost:3306/minecraft";
	
	
	public static String getTime() {
		return now(format);
	}
	
	private static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static void initialize() {
		loadPropertiesFile();
		optedOut = new ArrayList<String>();
	}
	
	private static void loadPropertiesFile() {
		PropertiesFile pf = new PropertiesFile("bigbrother.txt");
		try {
			pf.load();
		} catch (IOException e) {
		}

		// WatchedPlayers
		if (pf.keyExists("watchedplayers"))
			watchList = processPlayerList(pf.getString("watchedplayers"));
		else {
			pf.setString("watchedplayers", "");
			watchList = new ArrayList<String>();
		}

		// seenPlayers
		if (pf.keyExists("seenPlayers"))
			seenPlayers = processPlayerList(pf.getString("seenPlayers"));
		else {
			pf.setString("seenPlayers", "");
			seenPlayers = new ArrayList<String>();
		}

		// Date Format
		if (pf.keyExists("dateformat"))
			format = pf.getString("dateformat");
		else {
			pf.setString("dateformat", "dd.MM.yy@HH:mm:ss");
			format = "dd.MM.yy@HH:mm:ss";
		}

		login = getBooleanProperty("login", true, pf);
		logout = getBooleanProperty("logout", true, pf);
		position = getBooleanProperty("position", true, pf);
		blockDestroying = getBooleanProperty("blockDestroying", true, pf);
		commands = getBooleanProperty("commands", true, pf);
		chat = getBooleanProperty("chat", false, pf);
		blockPlacing = getBooleanProperty("blockPlacing", true, pf);
		autoWatch = getBooleanProperty("autoWatch", false, pf);
		verbose = getBooleanProperty("verbose", false, pf);
		notifyMods = getBooleanProperty("notifyMods", false, pf);

		pf.setString("version", BigBrother.version);

		if (pf.keyExists("watchedBlocks"))
			watchedBlocks = processBlockList(pf.getString("watchedBlocks"));
		else {
			pf.setString("watchedBlocks", "");
			watchedBlocks = new ArrayList<Integer>();
		}
		
		
		if (!pf.containsKey("hours")) {
			pf.setInt("hours", 12);
		}
		hours = pf.getInt("hours");
		
		if (!pf.containsKey("hourCount")) {
			pf.setDouble("hourCount", 0);
		}
		hourCount = pf.getDouble("hourCount");
		
		if (!pf.containsKey("emails")) {
			pf.setString("emails", "");
		}
		emails = BBEmail.processEmails(pf.getString("emails"));
		
		if(emails.size()==0) {
			hours = 0;
		}
		
		if (hours > 0) {
			emailTimer = new Timer();
			emailTimer.schedule(new EmailTask(), 15 * 60 * 1000, 15 * 60 * 1000);
		}

		pf.save();
	}
	
	public static void saveHourCount() {
		PropertiesFile pf = new PropertiesFile("bigbrother.txt");
		try {
			pf.load();
		} catch (IOException e) {
		}
		
		pf.setDouble("hourCount", hourCount);
		pf.save();
	}
	
	private static boolean getBooleanProperty(String key, boolean defaultValue,
			PropertiesFile pf) {
		if (pf.keyExists(key))
			return pf.getBoolean(key);
		else {
			pf.setBoolean(key, defaultValue);
			return defaultValue;
		}
	}

	private static ArrayList<String> processPlayerList(String string) {
		return new ArrayList<String>(Arrays.asList(string.split(",")));
	}

	private static ArrayList<Integer> processBlockList(String string) {
		ArrayList<Integer> data = new ArrayList<Integer>();
		if (string == null)
			return new ArrayList<Integer>();
		String[] list = string.split(",");
		for (String str : list) {
			if (str.equals(""))
				continue;
			try {
				data.add(Integer.parseInt(str));
			} catch (Exception e) {
				System.out
						.println("[BBROTHER]: Invalid block id value: " + str);
			}
		}
		return data;
	}
}

package me.taylorkelly.bigbrother;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import me.taylorkelly.util.TimeParser;

import org.bukkit.Server;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

// TODO: Split all these vars into seperate classes in anticipation of yamlification.
public class BBSettings {
    public enum DBMS {
        sqlite, mysql, invalid,
        // postgres,
    }
    
    // TODO: Disabled until we can get a way for it to not break rollbacks -
    // tkelly
    // public static int maxRollbackRadius; // Maximum rollback radius. - N3X
    // public static int maxBlocksRolledBackPerPass; // Maximum blocks rolled
    // back per "pass".
    
    // Convert to bitflags?
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
    public static boolean leafDrops;
    public static boolean fire;
    public static boolean tntExplosions;
    public static boolean creeperExplosions;
    public static boolean miscExplosions;
    public static boolean ipPlayer;
    public static boolean lavaFlow;
    
    public static boolean restoreFire = false;
    public static boolean autoWatch = true;
    public static boolean flatLog = false;
    public static boolean mysqlLowPrioInserts = true;
    public static int defaultSearchRadius = 2;
    public static DBMS databaseSystem = DBMS.sqlite;
    public static String mysqlUser = "minecraft";
    public static String mysqlPass = "";
    public static String mysqlHost = "localhost";
    public static int mysqlPort = 3306;
    public static String mysqlEngine = "MyISAM";
    public static String mysqlPrefix = ""; // Table Prefix ("bb_" would turn
                                           // bbdata into bb_bbdata)
    public static String mysqlDatabase = "minecraft";
    
    // The presence of mysqlDSN overrides the above (except for password), the
    // above is a simplified version of this. The option is provided so advanced
    // users can specify flags via the DSN.
    public static String mysqlDSN = "jdbc:mysql://localhost:3306/minecraft";
    // Use persistant connections by default.
    public static boolean mysqlPersistant = true;
    public static int sendDelay = 4;
    public static long stickItem = 280L;
    
    // TODO: Get long version of this
    public static long cleanseAge = TimeParser.parseInterval("1d12h");
    
    // 100 million?!
    public static long maxRecords = 100000001L;
    // Maximum records deleted per cleanBy*().
    // Tested with this value, 10000rows = 1-2s on a
    // Pentium 4 MySQL server with 1GB RAM and a SATA MySQL HDD
    public static long deletesPerCleansing = 10000L;
    
    private static ArrayList<String> watchList;
    private static ArrayList<String> seenList;
    
    public static void initialize(File dataFolder) {
        watchList = new ArrayList<String>();
        seenList = new ArrayList<String>();
        
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        final File yml = new File(dataFolder, "BigBrother.yml");
        final File bbprops = new File(dataFolder, "BigBrother.properties");
        if (!yml.exists() && bbprops.exists()) {
            BBLogging.info("Importing properties files to new configuration file!");
            loadPropertiesFiles(dataFolder);
            // Until we're sure this works.
            // bbprops.deleteOnExit();
            // (new File(dataFolder, "watching.properties")).deleteOnExit();
        }
        loadLists(dataFolder);
        loadYaml(yml);
    }
    
    private static void loadYaml(File yamlfile) {
        final Configuration yml = new Configuration(yamlfile);
        yml.load();
        
        loadDBSettings(yml);
        loadWatchSettings(yml);
        
        stickItem = yml.getInt("bigbrother:stickItem", (int)stickItem);// "The item used for /bb stick");
        restoreFire = yml.getBoolean("bigbrother:restoreFire", false);// "Restore fire when rolling back");
        autoWatch = yml.getBoolean("bigbrother:autoWatch", true);// "Automatically start watching players");
        defaultSearchRadius = yml.getInt("bigbrother:defaultSearchRadius", 2);// "Default search radius for bbhere and bbfind");
        flatLog = yml.getBoolean("bigbrother:flatFileLogs", false);// "If true, will also log actions to .logs (one for each player)");
        
        yml.save();
    }
    
    private static void loadWatchSettings(Configuration yml) {
        ConfigurationNode watched = yml.getNode("bigbrother.watched");
        
        blockBreak = watched.getBoolean("blockBreak", true);// "Watch when players break blocks");
        blockPlace = watched.getBoolean("blockPlace", true);// "Watch when players place blocks");
        teleport = watched.getBoolean("teleport", true);// "Watch when players teleport around");
        chestChanges = watched.getBoolean("chestChanges", true);// "Watch when players add/remove items from chests");
        commands = watched.getBoolean("commands", true);// "Watch for all player commands");
        chat = watched.getBoolean("chat", true);// "Watch for player chat");
        login = watched.getBoolean("login", true);// "Watch for player logins");
        disconnect = watched.getBoolean("disconnect", true);// "Watch for player disconnects");
        doorOpen = watched.getBoolean("doorOpen", false);// "Watch for when player opens doors");
        buttonPress = watched.getBoolean("buttonPress", false);// "Watch for when player pushes buttons");
        leverSwitch = watched.getBoolean("leverSwitch", false);// "Watch for when player switches levers");
        fire = watched.getBoolean("fireLogging", true);// "Watch for when players start fires");
        leafDrops = watched.getBoolean("leafDrops", false);// "Watch for when leaves drop");
        tntExplosions = watched.getBoolean("tntExplosions", true);// "Watch for when TNT explodes");
        creeperExplosions = watched.getBoolean("creeperExplosions", true);// "Watch for when Creepers explodes");
        miscExplosions = watched.getBoolean("miscExplosions", true);// "Watch for miscellaneous explosions");
        ipPlayer = watched.getBoolean("ipPlayer", true);// "Add player's IP when login");
        lavaFlow = watched.getBoolean("lavaFlow", false);// "Log lava flow (useful for rolling-back lava)");
    }
    
    // Database configuration
    private static void loadDBSettings(Configuration yml) {
        // Database type (Database Management System = DBMS :V)
        final String dbms = yml.getString("bigbrother:database:type", databaseSystem.name());
        setDBMS(dbms);
        
        deletesPerCleansing = Long.valueOf(yml.getString("bigbrother:database:deletes-per-cleansing", Long.toString(deletesPerCleansing))); // "The maximum number of records to delete per cleansing (0 to disable).");
        cleanseAge = TimeParser.parseInterval(yml.getString("bigbrother:database:cleanseAge", "1d12h"));// "The maximum age of items in the database (can be mixture of #d,h,m,s) (0s to disable)"));
        maxRecords = Long.valueOf(yml.getString("bigbrother:database:maxRecords", Long.toString(maxRecords)));// "The maximum number of records that you want in your database (-1 to disable)");
        sendDelay = yml.getInt("bigbrother:database:sendDelay", sendDelay);// "Delay in seconds to batch send updates to database (4-5 recommended)");
        
        if (databaseSystem == DBMS.mysql) {
            final ConfigurationNode mysqlConfig = yml.getNode("bigbrother:database:mysql");
            mysqlUser = mysqlConfig.getString("username", mysqlUser);
            mysqlPass = mysqlConfig.getString("password", mysqlPass);
            mysqlHost = mysqlConfig.getString("hostname", mysqlHost);
            mysqlDatabase = mysqlConfig.getString("database", mysqlDatabase);
            mysqlPort = mysqlConfig.getInt("port", mysqlPort);
            mysqlEngine = mysqlConfig.getString("engine", mysqlEngine);
            mysqlPrefix = mysqlConfig.getString("username", mysqlPrefix);
            mysqlLowPrioInserts = mysqlConfig.getBoolean("low-priority-insert", mysqlLowPrioInserts);
        } else if (databaseSystem == DBMS.sqlite) {
            // SQLite stuff here
        }
    }
    
    private static void setDBMS(String name) {
        databaseSystem = DBMS.valueOf(name.toLowerCase());
    }
    
    //@Deprecated
    private static void loadPropertiesFiles(File dataFolder) {
        PropertiesFile pf = new PropertiesFile(new File(dataFolder, "watching.properties"));
        blockBreak = pf.getBoolean("blockBreak", true, "Watch when players break blocks");
        blockPlace = pf.getBoolean("blockPlace", true, "Watch when players place blocks");
        teleport = pf.getBoolean("teleport", true, "Watch when players teleport around");
        chestChanges = pf.getBoolean("chestChanges", true, "Watch when players add/remove items from chests");
        commands = pf.getBoolean("commands", true, "Watch for all player commands");
        chat = pf.getBoolean("chat", true, "Watch for player chat");
        login = pf.getBoolean("login", true, "Watch for player logins");
        disconnect = pf.getBoolean("disconnect", true, "Watch for player disconnects");
        doorOpen = pf.getBoolean("doorOpen", false, "Watch for when player opens doors");
        buttonPress = pf.getBoolean("buttonPress", false, "Watch for when player pushes buttons");
        leverSwitch = pf.getBoolean("leverSwitch", false, "Watch for when player switches levers");
        fire = pf.getBoolean("fireLogging", true, "Watch for when players start fires");
        leafDrops = pf.getBoolean("leafDrops", true, "Watch for when leaves drop");
        tntExplosions = pf.getBoolean("tntExplosions", true, "Watch for when TNT explodes");
        creeperExplosions = pf.getBoolean("creeperExplosions", true, "Watch for when Creepers explodes");
        miscExplosions = pf.getBoolean("miscExplosions", true, "Watch for miscellaneous explosions");
        ipPlayer = pf.getBoolean("ipPlayer", true, "Add player's IP when login");
        lavaFlow = pf.getBoolean("lavaFlow", true, "Log lava flow (useful for rolling-back lava)");
        // pf.save();
        
        pf = new PropertiesFile(new File(dataFolder, "BigBrother.properties"));
        deletesPerCleansing = pf.getLong("deletesPerCleansing", 1000L, "The maximum number of records to delete per cleansing (0 to disable).");
        cleanseAge = TimeParser.parseInterval(pf.getString("cleanseAge", "1d12h", "The maximum age of items in the database (can be mixture of #d,h,m,s) (0s to disable)"));
        maxRecords = pf.getLong("maxRecords", 10000000l, "The maximum number of records that you want in your database (-1 to disable)");
        stickItem = pf.getLong("stickItem", 280L, "The item used for /bb stick");
        restoreFire = pf.getBoolean("restoreFire", false, "Restore fire when rolling back");
        autoWatch = pf.getBoolean("autoWatch", true, "Automatically start watching players");
        defaultSearchRadius = pf.getInt("defaultSearchRadius", 2, "Default search radius for bbhere and bbfind");
        final boolean mysql = pf.getBoolean("MySQL", true, "If true, uses MySQL. If false, uses Sqlite");
        flatLog = pf.getBoolean("flatFileLogs", false, "If true, will also log actions to .logs (one for each player)");
        mysqlUser = pf.getString("mysqlUser", "root", "Username for MySQL db (if applicable)");
        mysqlPass = pf.getString("mysqlPass", "root", "Password for MySQL db (if applicable)");
        mysqlDSN = pf.getString("mysqlDB", "jdbc:mysql://localhost:3306/minecraft", "DB for MySQL (if applicable)");
        mysqlEngine = pf.getString("engine", "MyISAM", "Engine for the Database (MyISAM is recommended)");
        mysqlLowPrioInserts = pf.getBoolean("mysqlLowPriorityInserts", true, "All INSERTS should be run with LOW_PRIORITY");
        if (!mysql) {
            mysqlLowPrioInserts = false;
            databaseSystem = DBMS.sqlite;
        } else {
            databaseSystem = DBMS.mysql;
        }
        sendDelay = pf.getInt("sendDelay", 4, "Delay in seconds to batch send updates to database (4-5 recommended)");
        
        // Save to YAML Only
        // pf.save();
    }
    
    /**
     * @todo Move to SQL tables.
     * @param dataFolder
     */
    private static void loadLists(File dataFolder) {
        File file = new File(dataFolder, "WatchedPlayers.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            final Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                final String player = sc.nextLine();
                if (player.equals("")) {
                    continue;
                }
                if (player.contains(" ")) {
                    continue;
                }
                watchList.add(player);
            }
        } catch (final FileNotFoundException e) {
            BBLogging.severe("Cannot read file " + file.getName());
        } catch (final IOException e) {
            BBLogging.severe("IO Exception with file " + file.getName());
        }
        
        file = new File(dataFolder, "SeenPlayers.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            final Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                final String player = sc.nextLine();
                if (player.equals("")) {
                    continue;
                }
                if (player.contains(" ")) {
                    continue;
                }
                seenList.add(player);
            }
        } catch (final FileNotFoundException e) {
            BBLogging.severe("Cannot read file " + file.getName());
        } catch (final IOException e) {
            BBLogging.severe("IO Exception with file " + file.getName());
        }
        
    }
    
    public static Watcher getWatcher(Server server, File dataFolder) {
        return new Watcher(watchList, seenList, server, dataFolder);
    }
    
    /**
     * Returns "LOW_PRIORITY" for MySQL when mysqlLowPrioInserts is set.
     * 
     * @return LOW_PRIORITY | ""
     */
    public static String getMySQLIgnore() {
        // Don't really need to check mysql, but going to anyway to be safe.
        if (mysqlLowPrioInserts && usingDBMS(DBMS.mysql))
            return " LOW_PRIORITY ";
        else
            return " ";
    }
    
    /**
     * Are we using a certain Database Management System?
     * @param system The database system to check against.
     * @return 
     */
    public static boolean usingDBMS(DBMS system) {
        return databaseSystem == system;
    }
    
    /**
     * Get the JDBC DSN for a specific database system, with included database-specific settings.
     * @return The DSN we want.
     */
    public static String getDSN() {
        if (mysqlDSN != null)
            return mysqlDSN;
        
        if (usingDBMS(DBMS.mysql))
            return String.format("jdbc:mysql://%s:%d/%s", mysqlHost, mysqlPort, mysqlDatabase);
        // SQLite = failover
        return "jdbc:sqlite:plugins" + File.separator + "BigBrother" + File.separator + "bigbrother.db";
    }
}

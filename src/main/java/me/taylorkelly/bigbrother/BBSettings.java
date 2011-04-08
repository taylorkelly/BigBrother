package me.taylorkelly.bigbrother;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;

import me.taylorkelly.util.TimeParser;
import org.bukkit.Server;
import com.sk89q.worldedit.blocks.ItemType;

// TODO: Split all these vars into seperate classes in anticipation of yamlification.
public class BBSettings {
    
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
    public static boolean pickupItem;
    public static boolean dropItem;


    public static boolean libraryAutoDownload;
    public static boolean debugMode;
    public static boolean restoreFire = false;
    public static boolean autoWatch = true;
    public static boolean flatLog = false;
    public static boolean mysqlLowPrioInserts = true;
    public static int defaultSearchRadius = 2;
    public static DBMS databaseSystem = DBMS.H2;
    public static String mysqlUser = "minecraft";
    public static String mysqlPass = "";
    public static String mysqlHost = "localhost";
    public static int mysqlPort = 3306;
    public static String mysqlEngine = "MyISAM";
    private static String mysqlPrefix = ""; // Table Prefix ("bb_" would turn
    // bbdata into bb_bbdata)
    public static String mysqlDatabase = "minecraft";
    // The presence of mysqlDSN overrides the above (except for password), the
    // above is a simplified version of this. The option is provided so advanced
    // users can specify flags via the DSN.
    public static String mysqlDSN = "jdbc:mysql://localhost:3306/minecraft";
    // Use persistant connections by default.
    public static boolean mysqlPersistant = false;
    public static int sendDelay = 4;
    public static int stickItem = 280;
    // TODO: Get long version of this
    public static long cleanseAge = TimeParser.parseInterval("3d");
    public static long maxRecords = 3000000L;
    // Maximum records deleted per cleanBy*().
    // Tested with this value, 10000rows = 1-2s on a
    // Pentium 4 MySQL server with 1GB RAM and a SATA MySQL HDD
    public static long deletesPerCleansing = 20000L;
    private static ArrayList<String> watchList;
    private static ArrayList<String> seenList;
    private static ArrayList<Integer> blockExclusionList;
    public static int rollbacksPerTick;

    public static void initialize(File dataFolder) {
        watchList = new ArrayList<String>();
        seenList = new ArrayList<String>();
        blockExclusionList = new ArrayList<Integer>();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        final File yml = new File(dataFolder, "BigBrother.yml");
        BBLogging.debug("Path to BigBrother.yml: " + yml.getPath());
        final File bbprops = new File(dataFolder, "BigBrother.properties");
        if (!yml.exists() && bbprops.exists()) {
            BBLogging.info("Importing properties files to new configuration file!");
            File watching = new File(dataFolder, "watching.properties");
            convertPropFile(bbprops, watching, yml);
            bbprops.delete();
            watching.delete();
        }
        loadLists(dataFolder);
        loadYaml(yml);
        BBLogging.debug("Loaded Settings");

    }

    private static void loadYaml(File yamlfile) {
        final BetterConfig yml = new BetterConfig(yamlfile);
        
        // If the file's not there, don't load it
        if(yamlfile.exists())
            yml.load();

        loadDBSettings(yml);
        loadWatchSettings(yml);
        
        List<Object> excluded = yml.getList("general.excluded-blocks");
        // Dodge NPE reported by Mineral (and set a default)
        if(excluded==null) {
            yml.setProperty("general.excluded-blocks", blockExclusionList);
        } else {
            for (Object o : excluded) {
                int id = 0;
                if(o instanceof Integer)
                    id = (int)(Integer)o;
                else if(o instanceof String) {
                    id = ItemType.lookup((String)o).getID();
                }
                blockExclusionList.add(id);
            }
        }
        stickItem = yml.getInt("general.stick-item", 280);// "The item used for /bb stick");
        restoreFire = yml.getBoolean("general.restore-fire", false);// "Restore fire when rolling back");
        autoWatch = yml.getBoolean("general.auto-watch", true);// "Automatically start watching players");
        defaultSearchRadius = yml.getInt("general.default-search-radius", 5);// "Default search radius for bbhere and bbfind");
        flatLog = yml.getBoolean("general.personal-log-files", false);// "If true, will also log actions to .logs (one for each player)");
        rollbacksPerTick = yml.getInt("general.rollbacks-per-tick", 2000);// "If true, will also log actions to .logs (one for each player)");
        debugMode = yml.getBoolean("general.debug-mode", false);// "If true, will also log actions to .logs (one for each player)");
        libraryAutoDownload = yml.getBoolean("general.library-autodownload", true);// "If true, will also log actions to .logs (one for each player)");
        TNTLogger.THRESHOLD = yml.getDouble("general.tnt-threshold", 10.0);// "If true, will also log actions to .logs (one for each player)");
        yml.save();
    }

    private static void loadWatchSettings(BetterConfig watched) {
        blockBreak = watched.getBoolean("watched.blocks.block-break", true);// "Watch when players break blocks");
        blockPlace = watched.getBoolean("watched.blocks.block-place", true);// "Watch when players place blocks");
        teleport = watched.getBoolean("watched.player.teleport", true);// "Watch when players teleport around");
        chestChanges = watched.getBoolean("watched.blocks.chest-changes", true);// "Watch when players add/remove items from chests");
        commands = watched.getBoolean("watched.chat.commands", true);// "Watch for all player commands");
        chat = watched.getBoolean("watched.chat.chat", true);// "Watch for player chat");
        login = watched.getBoolean("watched.player.login", true);// "Watch for player logins");
        disconnect = watched.getBoolean("watched.player.disconnect", true);// "Watch for player disconnects");
        doorOpen = watched.getBoolean("watched.misc.door-open", false);// "Watch for when player opens doors");
        buttonPress = watched.getBoolean("watched.misc.button-press", false);// "Watch for when player pushes buttons");
        leverSwitch = watched.getBoolean("watched.misc.lever-switch", false);// "Watch for when player switches levers");
        fire = watched.getBoolean("watched.misc.flint-logging", true);// "Watch for when players start fires");
        leafDrops = watched.getBoolean("watched.environment.leaf-decay", false);// "Watch for when leaves drop");
        tntExplosions = watched.getBoolean("watched.explosions.tnt", true);// "Watch for when TNT explodes");
        creeperExplosions = watched.getBoolean("watched.explosions.creeper", true);// "Watch for when Creepers explodes");
        miscExplosions = watched.getBoolean("watched.explosions.misc", true);// "Watch for miscellaneous explosions");
        ipPlayer = watched.getBoolean("watched.player.ip-player", true);
        dropItem = watched.getBoolean("watched.player.drop-item", false);
        pickupItem = watched.getBoolean("watched.player.pickup-item", false);
        lavaFlow = watched.getBoolean("watched.environment.lava-flow", false);
    }

    // Database configuration
    private static void loadDBSettings(BetterConfig yml) {
        // Database type (Database Management System = DBMS :V)
        final String dbms = yml.getString("database.type", DBMS.H2.name());
        setDBMS(dbms);

        deletesPerCleansing = Long.valueOf(yml.getString("database.deletes-per-cleansing", Long.toString(deletesPerCleansing))); // "The maximum number of records to delete per cleansing (0 to disable).");
        cleanseAge = TimeParser.parseInterval(yml.getString("database.cleanse-age", "3d"));// "The maximum age of items in the database (can be mixture of #d,h,m,s) (0s to disable)"));
        maxRecords = Long.valueOf(yml.getString("database.max-records", Long.toString(maxRecords)));// "The maximum number of records that you want in your database (-1 to disable)");
        sendDelay = yml.getInt("database.send-delay", sendDelay);// "Delay in seconds to batch send updates to database (4-5 recommended)");
        //mysqlPersistant = yml.getBoolean("database.use-persistant-connection", mysqlPersistant);
        
        // MySQL/Postgres crap
            mysqlUser = yml.getString("database.mysql.username", mysqlUser);
            mysqlPass = yml.getString("database.mysql.password", mysqlPass);
            mysqlHost = yml.getString("database.mysql.hostname", mysqlHost);
            mysqlDatabase = yml.getString("database.mysql.database", mysqlDatabase);
            mysqlPort = yml.getInt("database.mysql.port", mysqlPort);
            mysqlEngine = yml.getString("database.mysql.engine", mysqlEngine);
            mysqlPrefix = yml.getString("database.mysql.prefix", mysqlPrefix);
            mysqlLowPrioInserts = yml.getBoolean("database.mysql.low-priority-insert", mysqlLowPrioInserts);
       //H2 Crap
    }

    private static void setDBMS(String name) {
        try {
            databaseSystem = DBMS.valueOf(name.toUpperCase());
        } catch(IllegalArgumentException e) {
            databaseSystem = DBMS.H2;
        }
    }

    private static void convertPropFile(File props, File watching, File yamlfile) {
        PropertiesFile watchingPf = new PropertiesFile(watching);
        PropertiesFile propsPf = new PropertiesFile(props);

        final BetterConfig yml = new BetterConfig(yamlfile);
        yml.load();
        stickItem = yml.getInt("general.stick-item", propsPf.getInt("stickItem", 280, "The item used for /bb stick"));
        restoreFire = yml.getBoolean("general.restore-fire", propsPf.getBoolean("restoreFire", false, "Restore fire when rolling back"));
        autoWatch = yml.getBoolean("general.auto-watch", propsPf.getBoolean("autoWatch", true, "Automatically start watching players"));
        defaultSearchRadius = yml.getInt("general.default-search-radius", propsPf.getInt("defaultSearchRadius", 2, "Default search radius for bbhere and bbfind"));
        flatLog = yml.getBoolean("general.personal-log-files", propsPf.getBoolean("flatFileLogs", false, "If true, will also log actions to .logs (one for each player)"));


        if (propsPf.getBoolean("MySQL", false, "If true, uses MySQL. If false, uses Sqlite")) {
            final String dbms = yml.getString("database.type", DBMS.MYSQL.name());
            setDBMS(dbms);
        } else {
            final String dbms = yml.getString("database.type", DBMS.H2.name());
            setDBMS(dbms);
        }

        deletesPerCleansing = Long.valueOf(yml.getString("database.deletes-per-cleansing", Long.toString(propsPf.getLong("deletesPerCleansing", 100000L, "The maximum number of records to delete per cleansing (0 to disable).")))); // "The maximum number of records to delete per cleansing (0 to disable).");
        cleanseAge = TimeParser.parseInterval(yml.getString("database.cleanse-age", "3d"));// "The maximum age of items in the database (can be mixture of #d,h,m,s) (0s to disable)"));
        maxRecords = Long.valueOf(yml.getString("database.max-records", Long.toString(3000000l)));// "The maximum number of records that you want in your database (-1 to disable)");
        sendDelay = yml.getInt("database.send-delay", propsPf.getInt("send-delay", 4, "Delay in seconds to batch send updates to database (4-5 recommended)"));// "Delay in seconds to batch send updates to database (4-5 recommended)");

        if (databaseSystem == DBMS.MYSQL) {
            mysqlUser = yml.getString("database.mysql.username", propsPf.getString("mysqlUser", "root", "Username for MySQL db (if applicable)"));
            mysqlPass = yml.getString("database.mysql.password", propsPf.getString("mysqlPass", "root", "Password for MySQL db (if applicable)"));
            String fullDSN = propsPf.getString("mysqlDB", "jdbc:mysql://localhost:3306/minecraft", "DB for MySQL (if applicable)");
            fullDSN = fullDSN.substring(13); // cut out the jdbc:mysql stuff
            if (fullDSN.split(":").length == 2) {
                mysqlHost = yml.getString("database.mysql.hostname", fullDSN.split(":")[0]);
                if (fullDSN.split(":")[1].split("/").length == 2) { // lol.
                    mysqlDatabase = yml.getString("database.mysql.database", fullDSN.split(":")[1].split("/")[1]);
                    mysqlPort = yml.getInt("database.mysql.port", Integer.parseInt(fullDSN.split(":")[1].split("/")[0]));
                } else {
                    mysqlDatabase = yml.getString("database.mysql.database", mysqlDatabase);
                    mysqlPort = yml.getInt("database.mysql.port", mysqlPort);
                }
            } else {
                mysqlHost = yml.getString("database.mysql.hostname", mysqlHost);
                mysqlDatabase = yml.getString("database.mysql.database", mysqlDatabase);
                mysqlPort = yml.getInt("database.mysql.port", mysqlPort);
            }

            mysqlEngine = yml.getString("database.mysql.engine", mysqlEngine);
            //mysqlPrefix = yml.getString("database.mysql.prefix", mysqlPrefix);
            mysqlLowPrioInserts = yml.getBoolean("database.mysql.low-priority-insert", mysqlLowPrioInserts);
        }

        blockBreak = yml.getBoolean("watched.blocks.block-break", watchingPf.getBoolean("blockBreak", true, "Watch when players break blocks"));// "Watch when players break blocks");
        blockPlace = yml.getBoolean("watched.blocks.block-place", watchingPf.getBoolean("blockPlace", true, "Watch when players place blocks"));// "Watch when players place blocks");
        teleport = yml.getBoolean("watched.player.teleport", watchingPf.getBoolean("teleport", true, "Watch when players teleport around"));// "Watch when players teleport around");
        chestChanges = yml.getBoolean("watched.blocks.chest-changes", watchingPf.getBoolean("chestChanges", true, "Watch when players add/remove items from chests"));// "Watch when players add/remove items from chests");
        commands = yml.getBoolean("watched.chat.commands", watchingPf.getBoolean("commands", true, "Watch for all player commands"));// "Watch for all player commands");
        chat = yml.getBoolean("watched.chat.chat", watchingPf.getBoolean("chat", true, "Watch for player chat"));// "Watch for player chat");
        login = yml.getBoolean("watched.player.login", watchingPf.getBoolean("login", true, "Watch for player logins"));// "Watch for player logins");
        disconnect = yml.getBoolean("watched.player.disconnect", watchingPf.getBoolean("disconnect", true, "Watch for player disconnects"));// "Watch for player disconnects");
        doorOpen = yml.getBoolean("watched.misc.door-open", watchingPf.getBoolean("doorOpen", false, "Watch for when player opens doors"));// "Watch for when player opens doors");
        buttonPress = yml.getBoolean("watched.misc.button-press", watchingPf.getBoolean("buttonPress", false, "Watch for when player pushes buttons"));// "Watch for when player pushes buttons");
        leverSwitch = yml.getBoolean("watched.misc.lever-switch", watchingPf.getBoolean("leverSwitch", false, "Watch for when player switches levers"));// "Watch for when player switches levers");
        fire = yml.getBoolean("watched.misc.flint-logging", watchingPf.getBoolean("fireLogging", true, "Watch for when players start fires"));// "Watch for when players start fires");
        leafDrops = yml.getBoolean("watched.environment.leaf-decay", watchingPf.getBoolean("leafDrops", false, "Watch for when leaves drop"));// "Watch for when leaves drop");
        tntExplosions = yml.getBoolean("watched.explosions.tnt", watchingPf.getBoolean("tntExplosions", true, "Watch for when TNT explodes"));// "Watch for when TNT explodes");
        creeperExplosions = yml.getBoolean("watched.explosions.creeper", watchingPf.getBoolean("creeperExplosions", true, "Watch for when Creepers explodes"));// "Watch for when Creepers explodes");
        miscExplosions = yml.getBoolean("watched.explosions.misc", watchingPf.getBoolean("miscExplosions", true, "Watch for miscellaneous explosions"));// "Watch for miscellaneous explosions");
        ipPlayer = yml.getBoolean("watched.player.ip-player", watchingPf.getBoolean("ipPlayer", true, "Add player's IP when login"));// "Add player's IP when login");
        lavaFlow = yml.getBoolean("watched.environment.lava-flow", watchingPf.getBoolean("lavaFlow", false, "Log lava flow (useful for rolling-back lava)"));// "Log lava flow (useful for rolling-back lava)");

        yml.save();
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
        return new Watcher(server);
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
        if (usingDBMS(DBMS.MYSQL)) {
            return String.format("jdbc:mysql://%s:%d/%s", mysqlHost, mysqlPort, mysqlDatabase);
        } else if (usingDBMS(DBMS.POSTGRES)) {
        	return String.format("jdbc:postgresql://%s:%d/%s", mysqlHost, mysqlPort, mysqlDatabase);
        } else if (usingDBMS(DBMS.H2)) {
            return "jdbc:h2:plugins" + File.separator + "BigBrother" + File.separator + "bigbrother";
        } else {
            return "";
        }
    }
    
    /**
     * Prefixify table names.
     * @param tablename
     * @return
     */
    public static String applyPrefix(String tablename) {
        return BBSettings.mysqlPrefix+tablename;
    }

    public enum DBMS {

        H2, 
        MYSQL,
        POSTGRES,
    }

    /**
     * Replace placeholder with the table prefix.
     * @param sql
     * @param placeholder
     * @return
     */
    public static String replaceWithPrefix(String sql, String placeholder) {
        return sql.replace(placeholder, mysqlPrefix);
    }
    
    /**
     * Check if a blocktype is being ignored.
     * @param type
     * @return
     */
    public static boolean isBlockIgnored(int type) {
        return blockExclusionList.contains(type);
    }
}

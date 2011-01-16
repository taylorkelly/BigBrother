package me.taylorkelly.bigbrother.datablock;

import java.io.*;
import java.sql.*;
import java.util.logging.Level;

import org.bukkit.Server;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;

public abstract class BBDataBlock {
    public final static String BBDATA_NAME = "bbdata";
    private final static String BBDATA_TABLE_SQLITE = "CREATE TABLE `bbdata` (" + "`id` INTEGER PRIMARY KEY," + "`date` INT UNSIGNED NOT NULL DEFAULT '0',"
            + "`player` varchar(32) NOT NULL DEFAULT 'Player'," + "`action` tinyint NOT NULL DEFAULT '0'," + "`world` tinyint NOT NULL DEFAULT '0',"
            + "`x` int NOT NULL DEFAULT '0'," + "`y` tinyint UNSIGNED NOT NULL DEFAULT '0'," + "`z` int NOT NULL DEFAULT '0'," + "`type` smallint NOT NULL DEFAULT '0',"
            + "`data` varchar(150) NOT NULL DEFAULT ''," + "`rbacked` boolean NOT NULL DEFAULT '0'" + ");" + "CREATE INDEX dateIndex on bbdata (date);"
            + "CREATE INDEX playerIndex on bbdata (player);" + "CREATE INDEX actionIndex on bbdata (action);" + "CREATE INDEX worldIndex on bbdata (world);"
            + "CREATE INDEX xIndex on bbdata (x);" + "CREATE INDEX yIndex on bbdata (y);" + "CREATE INDEX zIndex on bbdata (z);"
            + "CREATE INDEX typeIndex on bbdata (type);" + "CREATE INDEX rbackedIndex on bbdata (rbacked);";
    private final static String BBDATA_TABLE_MYSQL = "CREATE TABLE `bbdata` (" + "`id` INT NOT NULL AUTO_INCREMENT,"
            + "`date` INT UNSIGNED NOT NULL DEFAULT '0'," + "`player` varchar(32) NOT NULL DEFAULT 'Player'," + "`action` tinyint NOT NULL DEFAULT '0',"
            + "`world` tinyint NOT NULL DEFAULT '0'," + "`x` int NOT NULL DEFAULT '0'," + "`y` tinyint UNSIGNED NOT NULL DEFAULT '0'," + "`z` int NOT NULL DEFAULT '0',"
            + "`type` smallint NOT NULL DEFAULT '0'," + "`data` varchar(150) NOT NULL DEFAULT ''," + "`rbacked` boolean NOT NULL DEFAULT '0',"
            + "PRIMARY KEY (`id`)," + "INDEX(`world`)," + "INDEX(`x`)," + "INDEX(`y`)," + "INDEX(`z`)," + "INDEX(`player`)," + "INDEX(`action`),"
            + "INDEX(`date`)," + "INDEX(`type`)," + "INDEX(`rbacked`)" + ") ENGINE=InnoDB;";

    protected String player;
    protected int action;
    protected int x;
    protected int y;
    protected int z;
    protected int world;
    protected int type;
    protected String data;
    protected long date;

    public static final int BLOCK_BROKEN = 0;
    public static final int BLOCK_PLACED = 1;
    public static final int DESTROY_SIGN_TEXT = 2;
    public static final int TELEPORT = 3;
    public static final int DELTA_CHEST = 4;
    public static final int COMMAND = 5;
    public static final int CHAT = 6;
    public static final int DISCONNECT = 7;
    public static final int LOGIN = 8;
    public static final int DOOR_OPEN = 9;
    public static final int BUTTON_PRESS = 10;
    public static final int LEVER_SWITCH = 11;
    public static final int CREATE_SIGN_TEXT = 12;

    public BBDataBlock(String player, int action, int world, int x, int y, int z, int type, String data) {
        this.date = System.currentTimeMillis() / 1000;
        this.player = player;
        this.action = action;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
    }

    public void send() {
        DataBlockSender.offer(this);
    }

    public static void initialize() {
        boolean sqlite = false;
        switch (BBSettings.dataDest) {
        case MYSQL:
        case MYSQL_AND_FLAT:
            sqlite = false;
            break;
        case SQLITE:
        case SQLITE_AND_FLAT:
            sqlite = true;
            break;
        }

        if (!bbdataTableExists(sqlite)) {
            BigBrother.log.info("[BBROTHER]: Generating bbdata table");
            createBBDataTable(sqlite);
        }
    }

    private static boolean bbdataTableExists(boolean sqlite) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            if (sqlite) {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(BBSettings.liteDb);
            } else {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
            }
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, BBDATA_NAME, null);
            if (!rs.next())
                return false;
            return true;
        } catch (SQLException ex) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Table Check SQL Exception" + ((sqlite) ? " sqlite" : " mysql"), ex);
            return false;
        } catch (ClassNotFoundException e) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Table Check SQL Exception (cnf)" + ((sqlite) ? " sqlite" : " mysql"));
            return false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Table Check SQL Exception (on closing)");
            }
        }
    }

    private static void createBBDataTable(boolean sqlite) {
        Connection conn = null;
        Statement st = null;
        try {
            if (sqlite) {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(BBSettings.liteDb);
                conn.setAutoCommit(false);
            } else {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
                conn.setAutoCommit(false);
            }
            st = conn.createStatement();
            if (sqlite) {
                st.executeUpdate(BBDATA_TABLE_SQLITE);
            } else {
                st.executeUpdate(BBDATA_TABLE_MYSQL);
            }
            conn.commit();
        } catch (SQLException e) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Create Table SQL Exception" + ((sqlite) ? " sqlite" : " mysql"), e);
        } catch (ClassNotFoundException e) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Create Table SQL Exception (cnf)" + ((sqlite) ? " sqlite" : " mysql"));
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (st != null)
                    st.close();
            } catch (SQLException e) {
                BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Could not create the table (on close)");
            }
        }
    }

    public abstract void rollback(Server server);

    public abstract void redo(Server server);

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return null;
    }

    public static BBDataBlock getBBDataBlock(String player, int action, int world, int x, int y, int z, int type, String data) {
        switch (action) {
        case (BLOCK_BROKEN):
            return BrokenBlock.getBBDataBlock(player, world, x, y, z, type, data);
        case (BLOCK_PLACED):
            return PlacedBlock.getBBDataBlock(player, world, x, y, z, type, data);
        case (DESTROY_SIGN_TEXT):
            return DestroySignText.getBBDataBlock(player, world, x, y, z, type, data);
        case (TELEPORT):
            return Teleport.getBBDataBlock(player, world, x, y, z, type, data);
        case (DELTA_CHEST):
            return DeltaChest.getBBDataBlock(player, world, x, y, z, type, data);
        case (COMMAND):
            return Command.getBBDataBlock(player, world, x, y, z, type, data);
        case (CHAT):
            return Chat.getBBDataBlock(player, world, x, y, z, type, data);
        case (DISCONNECT):
            return Disconnect.getBBDataBlock(player, world, x, y, z, type, data);
        case (LOGIN):
            return Login.getBBDataBlock(player, world, x, y, z, type, data);
        case (DOOR_OPEN):
            return DoorOpen.getBBDataBlock(player, world, x, y, z, type, data);
        case (BUTTON_PRESS):
            return ButtonPress.getBBDataBlock(player, world, x, y, z, type, data);
        case (LEVER_SWITCH):
            return LeverSwitch.getBBDataBlock(player, world, x, y, z, type, data);
        case (CREATE_SIGN_TEXT):
            return CreateSignText.getBBDataBlock(player, world, x, y, z, type, data);
        default:
            return null;
        }
    }
}
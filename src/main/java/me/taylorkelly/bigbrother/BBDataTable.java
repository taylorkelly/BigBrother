
package me.taylorkelly.bigbrother;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

public class BBDataTable {
    public final static String BBDATA_NAME = "bbdata";
    private final static String BBDATA_TABLE_SQLITE = "CREATE TABLE `bbdata` (" + "`id` INTEGER PRIMARY KEY," + "`date` INT UNSIGNED NOT NULL DEFAULT '0',"
            + "`player` varchar(32) NOT NULL DEFAULT 'Player'," + "`action` tinyint NOT NULL DEFAULT '0'," + "`world` tinyint NOT NULL DEFAULT '0',"
            + "`x` int NOT NULL DEFAULT '0'," + "`y` tinyint UNSIGNED NOT NULL DEFAULT '0'," + "`z` int NOT NULL DEFAULT '0',"
            + "`type` smallint NOT NULL DEFAULT '0'," + "`data` varchar(500) NOT NULL DEFAULT ''," + "`rbacked` boolean NOT NULL DEFAULT '0'" + ");"
            + "CREATE INDEX dateIndex on bbdata (date);" + "CREATE INDEX playerIndex on bbdata (player);" + "CREATE INDEX actionIndex on bbdata (action);"
            + "CREATE INDEX worldIndex on bbdata (world);" + "CREATE INDEX xIndex on bbdata (x);" + "CREATE INDEX yIndex on bbdata (y);"
            + "CREATE INDEX zIndex on bbdata (z);" + "CREATE INDEX typeIndex on bbdata (type);" + "CREATE INDEX rbackedIndex on bbdata (rbacked);";
    public static String BBDATA_TABLE_MYSQL = "CREATE TABLE `bbdata` (" + "`id` INT NOT NULL AUTO_INCREMENT,"
            + "`date` INT UNSIGNED NOT NULL DEFAULT '0'," + "`player` varchar(32) NOT NULL DEFAULT 'Player'," + "`action` tinyint NOT NULL DEFAULT '0',"
            + "`world` tinyint NOT NULL DEFAULT '0'," + "`x` int NOT NULL DEFAULT '0'," + "`y` tinyint UNSIGNED NOT NULL DEFAULT '0',"
            + "`z` int NOT NULL DEFAULT '0'," + "`type` smallint NOT NULL DEFAULT '0'," + "`data` varchar(500) NOT NULL DEFAULT '',"
            + "`rbacked` boolean NOT NULL DEFAULT '0'," + "PRIMARY KEY (`id`)," + "INDEX(`world`)," + "INDEX(`x`)," + "INDEX(`y`)," + "INDEX(`z`),"
            + "INDEX(`player`)," + "INDEX(`action`)," + "INDEX(`date`)," + "INDEX(`type`)," + "INDEX(`rbacked`)" + ")";

    public static void initialize() {
        if (!bbdataTableExists(!BBSettings.mysql)) {
            BBLogging.info("Building `bbdata` table...");
            createBBDataTable(!BBSettings.mysql);
        }
    }

    private static boolean bbdataTableExists(boolean sqlite) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, BBDATA_NAME, null);
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (SQLException ex) {
            BBLogging.severe("Table Check SQL Exception" + ((sqlite) ? " sqlite" : " mysql"), ex);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BBLogging.severe("Table Check SQL Exception (on closing)");
            }
        }
    }

    private static void createBBDataTable(boolean sqlite) {
        Connection conn = null;
        Statement st = null;
        try {
            conn = ConnectionManager.getConnection();
            st = conn.createStatement();
            if (sqlite) {
                st.executeUpdate(BBDATA_TABLE_SQLITE);
            } else {
                st.executeUpdate(BBDATA_TABLE_MYSQL);
            }
            conn.commit();
        } catch (SQLException e) {
            BBLogging.severe("Create Table SQL Exception" + ((sqlite) ? " sqlite" : " mysql"), e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                BBLogging.severe("Could not create the table (on close)");
            }
        }
    }

}

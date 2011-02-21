package me.taylorkelly.bigbrother;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

public class WorldManager {
    public final static String WORLD_TABLE_NAME = "bbworlds";
    private final static String WORLD_TABLE_SQL = 
            "CREATE TABLE `bbworlds` (" +
                "`id` INTEGER PRIMARY KEY," +
                "`name` varchar(50) NOT NULL DEFAULT 'world');";
    private HashMap<String, Integer> worldMap;

    public WorldManager() {
        if (!worldTableExists()) {
            createWorldTable();
        }
        worldMap = loadWorlds();
    }

    private HashMap<String, Integer> loadWorlds() {
        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        Connection conn = null;
        Statement statement = null;
        ResultSet set = null;
        try {
            conn = ConnectionManager.getConnection();

            statement = conn.createStatement();
            set = statement.executeQuery("SELECT * FROM `bbworlds`");
            int size = 0;
            while (set.next()) {
                size++;
                int index = set.getInt("id");
                String name = set.getString("name");
                ret.put(name, index);
            }
        } catch (SQLException ex) {
            BigBrother.log.log(Level.SEVERE, "[BIGBROTHER]: World Load Exception", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
                BigBrother.log.log(Level.SEVERE, "[BIGBROTHER]: World Load Exception (on close)", ex);
            }
        }
        return ret;
    }

    private static boolean worldTableExists() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, WORLD_TABLE_NAME, null);
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (SQLException ex) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: World Table Check SQL Exception", ex);
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
                BigBrother.log.log(Level.SEVERE, "[BBROTHER]: World Table Check SQL Exception (on closing)");
            }
        }
    }

    private static void createWorldTable() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = ConnectionManager.getConnection();
            st = conn.createStatement();
            st.executeUpdate(WORLD_TABLE_SQL);
            conn.commit();
        } catch (SQLException e) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Create World Table SQL Exception", e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Could not create the world table (on close)");
            }
        }
    }
}

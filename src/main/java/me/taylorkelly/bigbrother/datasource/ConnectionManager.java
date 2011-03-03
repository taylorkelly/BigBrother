package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BigBrother;

import me.taylorkelly.bigbrother.BBSettings;

public class ConnectionManager {

    private static BigBrother plugin;

	public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:jdc:jdcpool");
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            BBLogging.severe("Error getting connection", e);
        	plugin.getServer().getPluginManager().disablePlugin(plugin);
        	plugin.getServer().broadcastMessage("[BBROTHER]: CONNECTION FAILURE. Please tell the ops to fix the connection and restart BigBrother.");
            return null;
        }
    }

    public static Connection createConnection(BigBrother bb) {
    	plugin=bb;
        try {
            if (BBSettings.mysql) {
                new JDCConnectionDriver("com.mysql.jdbc.Driver", BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
                Connection ret = DriverManager.getConnection("jdbc:jdc:jdcpool");
                ret.setAutoCommit(false);
                return ret;
            } else {
                new JDCConnectionDriver("org.sqlite.JDBC", BBSettings.liteDb, BBSettings.mysqlUser, BBSettings.mysqlPass);
                Connection ret = DriverManager.getConnection("jdbc:jdc:jdcpool");
                ret.setAutoCommit(false);
                return ret;
            }
        } catch (ClassNotFoundException e) {
            if (BBSettings.mysql) {
                BBLogging.severe("Could not find MySQL Library");
            } else {
                BBLogging.severe("Could not find SQLite Library");
            }
            return null;
        } catch (SQLException e) {
            if (BBSettings.mysql) {
                BBLogging.severe("MySQL SQLException on Creation", e);
            } else {
                BBLogging.severe("SQLite SQLException on Creation", e);
            }
            return null;
        } catch (InstantiationException e) {
            if (BBSettings.mysql) {
                BBLogging.severe("InstantiationException", e);
            } else {
                BBLogging.severe("InstantiationException", e);
            }
            return null;
        } catch (IllegalAccessException e) {
            if (BBSettings.mysql) {
                BBLogging.severe("IllegalAccessException", e);
            } else {
                BBLogging.severe("IllegalAccessException", e);
            }
            return null;
        }
    }
}

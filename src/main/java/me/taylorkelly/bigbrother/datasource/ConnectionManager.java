package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
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
            BBLogging.severe("Error getting a connection, disabling BigBrother...", e);
        	plugin.getServer().getPluginManager().disablePlugin(plugin);
        	plugin.getServer().broadcastMessage("[BBROTHER]: CONNECTION FAILURE. Please tell the ops to fix the connection and restart BigBrother.");
            return null;
        }
    }

    public static boolean createConnection(BigBrother bb) {
    	plugin=bb;
        try {
            if (BBSettings.usingDBMS(DBMS.mysql)) {
                new JDCConnectionDriver("com.mysql.jdbc.Driver", BBSettings.getDSN(), BBSettings.mysqlUser, BBSettings.mysqlPass);
            } else {
                new JDCConnectionDriver("org.sqlite.JDBC", BBSettings.getDSN(), BBSettings.mysqlUser, BBSettings.mysqlPass);
            }
            return true;
        } catch (ClassNotFoundException e) {
            if (BBSettings.usingDBMS(DBMS.mysql)) {
                BBLogging.severe("Could not find lib/mysql.jar!  Please make sure it is present and readable.");
            } else {
                BBLogging.severe("Could not find lib/sqlite.jar!  Please make sure it is present and readable.");
            }
        } catch (SQLException e) {
            if (BBSettings.usingDBMS(DBMS.mysql)) {
                BBLogging.severe("MySQL error during connection:", e);
            } else {
                BBLogging.severe("SQLite error during connection:", e);
            }
        } catch (InstantiationException e) {
            if (BBSettings.usingDBMS(DBMS.mysql)) {
                BBLogging.severe("InstantiationException", e);
            } else {
                BBLogging.severe("InstantiationException", e);
            }
        } catch (IllegalAccessException e) {
            if (BBSettings.usingDBMS(DBMS.mysql)) {
                BBLogging.severe("IllegalAccessException", e);
            } else {
                BBLogging.severe("IllegalAccessException", e);
            }
        }
        return false;
    }
}

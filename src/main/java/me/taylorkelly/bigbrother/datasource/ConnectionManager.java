package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
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
        plugin = bb;
        try {
            BBLogging.debug("Creating connection using " + BBSettings.databaseSystem + " at " + BBSettings.getDSN());
            if (BBSettings.usingDBMS(DBMS.MYSQL)) {
                new JDCConnectionDriver("com.mysql.jdbc.Driver", BBSettings.getDSN(), BBSettings.mysqlUser, BBSettings.mysqlPass);
            } else {
                new JDCConnectionDriver("org.sqlite.JDBC", BBSettings.getDSN(), BBSettings.mysqlUser, BBSettings.mysqlPass);
            }
            return true;
        } catch (ClassNotFoundException e) {
            if (BBSettings.usingDBMS(DBMS.MYSQL)) {
                BBLogging.severe("Could not find lib/mysql.jar!  Please make sure it is present and readable.");
            } else if (BBSettings.usingDBMS(DBMS.SQLITE)) {
                BBLogging.severe("Could not find lib/sqlite.jar!  Please make sure it is present and readable.");
            }
        } catch (SQLException e) {
            BBLogging.severe(BBSettings.databaseSystem.name() + " error during connection:", e);
        } catch (InstantiationException e) {
            if (BBSettings.usingDBMS(DBMS.MYSQL)) {
                BBLogging.severe("InstantiationException", e);
            } else if (BBSettings.usingDBMS(DBMS.SQLITE)) {
                BBLogging.severe("InstantiationException", e);
            }
        } catch (IllegalAccessException e) {
            if (BBSettings.usingDBMS(DBMS.MYSQL)) {
                BBLogging.severe("IllegalAccessException", e);
            } else if (BBSettings.usingDBMS(DBMS.SQLITE)) {
                BBLogging.severe("IllegalAccessException", e);
            }
        }
        return false;
    }

    public static void cleanup( String caller, Connection conn, Statement stmt, ResultSet rs ) {
        try {
            if ( null != rs ) {
                rs.close();
            }
        } catch (SQLException e) {
            BBLogging.severe("Error closing recordset from '" + caller + "':", e);
        }

        try {
            if ( null != stmt ) {
                stmt.close();
            }
        } catch (SQLException e) {
            BBLogging.severe("Error closing statement from '" + caller + "':", e);
        }

        try {
            if ( null != conn ) {
                conn.close();
            }
        } catch (SQLException e) {
            BBLogging.severe("Error closing connection from '" + caller + "':", e);
        }
    }
}

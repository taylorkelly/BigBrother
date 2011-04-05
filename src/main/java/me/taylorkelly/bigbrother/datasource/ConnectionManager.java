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
    private static boolean failedLink=false;
    private static boolean shouldReconnect=false;
    private static Connection connection; // Use one connection.
    
    public static Connection getConnection() {
        if(failedLink) {
            if(!shouldReconnect) {
                return null;
            } else {
                if(BBSettings.mysqlPersistant)
                    initConnection();
            }
        }
        
        if(BBSettings.mysqlPersistant)
            return connection;
        else
            return createConnection(false);
    }

    public static boolean createConnection(BigBrother bb) {
        plugin = bb;
        try {
            BBLogging.debug("Creating connection using " + BBSettings.databaseSystem + " at " + BBSettings.getDSN());
            if (BBSettings.usingDBMS(DBMS.MYSQL)) {
                new JDCConnectionDriver("com.mysql.jdbc.Driver", BBSettings.getDSN(), BBSettings.mysqlUser, BBSettings.mysqlPass);
            } else if(BBSettings.usingDBMS(DBMS.H2)) {
                new JDCConnectionDriver("org.h2.Driver", BBSettings.getDSN(),"sa","");
            }
            return true;
        } catch (ClassNotFoundException e) {
            if (BBSettings.usingDBMS(DBMS.MYSQL)) {
                BBLogging.severe("Could not find lib/mysql.jar!  Please make sure it is present and readable.");
            } else if (BBSettings.usingDBMS(DBMS.H2)) {
                BBLogging.severe("Could not find lib/h2.jar!  Please make sure it is present and readable.");
            }
        } catch (SQLException e) {
            BBLogging.severe(BBSettings.databaseSystem.name() + " error during connection:", e);
        } catch (InstantiationException e) {
            BBLogging.severe("InstantiationException", e);
        } catch (IllegalAccessException e) {
            BBLogging.severe("IllegalAccessException", e);
        }
        if(BBSettings.mysqlPersistant)
            initConnection();
        return false;
    }

    private static void initConnection() {
        connection=createConnection(true);
    }
    private static Connection createConnection(boolean firstConnection) {
        try {
            BBLogging.debug("Opening connection");
            Connection conn = DriverManager.getConnection("jdbc:jdc:jdcpool");
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            if(firstConnection) {
                BBLogging.severe("Error getting a connection, disabling BigBrother...", e);
                BBLogging.severe("Make sure your database settings are correct!");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                //plugin.getServer().broadcastMessage("[BBROTHER]: CONNECTION FAILURE. Please tell the ops to fix the connection and restart BigBrother.");
            }
            setFailedLink(true);
            setShouldReconnect(!firstConnection); // Don't reconnect, MySQL settings are probably wrong.
            return null;
        }
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
        if(!BBSettings.mysqlPersistant) {
            try {
                if ( null != conn ) {
                    conn.close();
                }
            } catch (SQLException e) {
                BBLogging.severe("Error closing connection from '" + caller + "':", e);
            }
        }
    }

    /**
     * @param doReconnect the doReconnect to set
     */
    public static void setShouldReconnect(boolean doReconnect) {
        ConnectionManager.shouldReconnect = doReconnect;
    }

    /**
     * @return the doReconnect
     */
    public static boolean getShouldReconnect() {
        return shouldReconnect;
    }

    /**
     * @param failedLink the failedLink to set
     */
    public static void setFailedLink(boolean failedLink) {
        ConnectionManager.failedLink = failedLink;
    }

    /**
     * @return the failedLink
     */
    public static boolean hasLinkFailed() {
        return failedLink;
    }

    public static Connection getFirstConnection() {
        if(failedLink) {
            if(!shouldReconnect) {
                return null;
            } else {
                if(BBSettings.mysqlPersistant)
                    initConnection();
            }
        }
        
        if(BBSettings.mysqlPersistant)
            return connection;
        else
            return createConnection(true);
    }
}

package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;

public class ConnectionManager {
    private static boolean failedLink=false;
    private static boolean shouldReconnect=false;
    private static Connection connection; // Use one connection.
    private static JDCConnectionDriver driver;
    
    public static Connection getConnection() {
        if(failedLink) {
            if(!shouldReconnect) {
                return null;
            } else {
                reconnect();
            }
        }
        
        if(BBSettings.mysqlPersistant)
            return connection;
        else
            return createConnection(false);
    }

    private static void reconnect() {
        driver.reconnect();
        if(BBSettings.mysqlPersistant)
            initConnection();
    }

    public static boolean setupConnection() {
        try {
            BBLogging.debug("Creating connection using " + BBSettings.databaseSystem + " at " + BBSettings.getDSN());
            if (BBSettings.usingDBMS(DBMS.MYSQL)) {
                driver=new JDCConnectionDriver("com.mysql.jdbc.Driver", BBSettings.getDSN(), BBSettings.mysqlUser, BBSettings.mysqlPass);
            } else if (BBSettings.usingDBMS(DBMS.POSTGRES)) {
                driver=new JDCConnectionDriver("org.postgresql.Driver", BBSettings.getDSN(), BBSettings.mysqlUser, BBSettings.mysqlPass);
            } else if(BBSettings.usingDBMS(DBMS.H2)) {
                driver=new JDCConnectionDriver("org.h2.Driver", BBSettings.getDSN(),"sa","");
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
                BBLogging.severe("Error getting a connection. Make sure your database settings are correct!",e);
            } else {
                BBLogging.severe("Connection failure, will try to reconnect is a moment...", e);
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
                reconnect();
            }
        }
        
        if(BBSettings.mysqlPersistant)
            return connection;
        else
            return createConnection(true);
    }
}

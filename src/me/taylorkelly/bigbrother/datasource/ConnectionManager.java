package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.taylorkelly.bigbrother.BBSettings;

public class ConnectionManager {

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:jdc:jdcpool");
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, "[BBROTHER] Error getting connection", e);
            e.printStackTrace();
            return null;
        }
    }

    public static Connection createConnection() {
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
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.taylorkelly.bigbrother.BBLogging;

import me.taylorkelly.bigbrother.BBSettings;

public class ConnectionManager {

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:jdc:jdcpool");
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            BBLogging.severe("Error getting connection", e);
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
            return null;
        } catch (SQLException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}

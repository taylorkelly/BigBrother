package datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBSettings;

public class ConnectionManager {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            connection = createConnection();
        }
        return connection;
    }

    private static Connection createConnection() {
        try {
            if (BBSettings.mysql) {
                Class.forName("com.mysql.jdbc.Driver");
                Connection ret = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
                ret.setAutoCommit(false);
                return ret;
            } else {
                Class.forName("org.sqlite.JDBC");
                Connection ret =  DriverManager.getConnection(BBSettings.liteDb);
                ret.setAutoCommit(false);
                return ret;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void freeConnection() {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

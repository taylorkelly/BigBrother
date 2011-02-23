package me.taylorkelly.bigbrother;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.util.Time;

class Cleanser {

    static boolean needsCleaning() {
        return BBSettings.cleanseAge != -1 || BBSettings.maxRecords != -1;
    }

    static void clean() {
        cleanByAge();
        cleanByNumber();
    }

    private static void cleanByAge() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.createStatement();
            int amount = stmt.executeUpdate("DELETE FROM `bbdata` WHERE date < " + Long.valueOf(Time.ago(BBSettings.cleanseAge)) + ";");
            BigBrother.info("Cleaned out " + Integer.valueOf(amount) + " records because of age", null);
            conn.commit();
        } catch (SQLException ex) {
            BigBrother.severe("Cleanse SQL exception (by age)", ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BigBrother.severe("Cleanse SQL exception (by age) (on close)", ex);
            }
        }
    }

    private static void cleanByNumber() {
        Connection conn = null;
        Statement statement = null;
        ResultSet set = null;
        int id = -1;
        try {
            conn = ConnectionManager.getConnection();
            statement = conn.createStatement();
            set = statement.executeQuery("SELECT * FROM `bbdata` ORDER BY `id` DESC LIMIT " + Long.valueOf(BBSettings.maxRecords) + ";");
            set.afterLast();
            if (set.previous()) {
                id = set.getInt("id");
            }
        } catch (SQLException ex) {
            BigBrother.severe("Cleanse SQL Exception (on # inspect)", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (set != null) {
                    set.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BigBrother.severe("Cleanse SQL Exception (on # inspect) (on close)", ex);
            }
        }

        if (id != -1) {
            conn = null;
            Statement stmt = null;
            try {
                conn = ConnectionManager.getConnection();
                stmt = conn.createStatement();
                int amount = stmt.executeUpdate("DELETE FROM `bbdata` WHERE id < " + Integer.valueOf(id) + ";");
                BigBrother.info("Cleaned out " + Integer.valueOf(amount) + " records because there are too many", null);
                conn.commit();
            } catch (SQLException ex) {
                BigBrother.severe("Cleanse SQL exception (by #)", ex);
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    BigBrother.severe("Cleanse SQL exception (by #) (on close)", ex);
                }
            }
        }
    }
}

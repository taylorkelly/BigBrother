package me.taylorkelly.bigbrother;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.datasource.ConnectionManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Stats {
    private static long globalRollback;
    private static long sessionRollback;
    private static long globalEdits;
    private static long sessionEdits;

    public static void initialize() {
        globalEdits = loadNumEdits();
        globalRollback = loadNumRollback();
    }

    private static long loadNumRollback() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("SELECT count(*) AS `num` FROM " + BBDataTable.BBDATA_NAME + " WHERE rbacked = '1';");
            rs = ps.executeQuery();
            conn.commit();
            rs.next();
            long num = rs.getLong("num");
            return num;
        } catch (SQLException ex) {
            BBLogging.severe("Find SQL Exception", ex);
            return 0;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                BBLogging.severe("Find SQL Exception (on close)");
            }
        }
    }

    private static long loadNumEdits() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("SELECT count(*) AS `num` FROM " + BBDataTable.BBDATA_NAME + " WHERE rbacked = '0';");
            rs = ps.executeQuery();
            conn.commit();
            rs.next();
            long num = rs.getLong("num");
            return num;
        } catch (SQLException ex) {
            BBLogging.severe("Find SQL Exception", ex);
            return 0;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                BBLogging.severe("Find SQL Exception (on close)");
            }
        }
    }

    public static void report(Player player) {
        player.sendMessage(BigBrother.premessage + ChatColor.AQUA + "Global Stats:");
        player.sendMessage("- " + ChatColor.YELLOW + globalEdits + ChatColor.WHITE + " edits logged");
        player.sendMessage("- " + ChatColor.YELLOW + globalRollback + ChatColor.WHITE + " edits rollback'd");
        player.sendMessage(ChatColor.AQUA + "Session Stats:");
        player.sendMessage("- " + ChatColor.YELLOW + sessionEdits + ChatColor.WHITE + " edits logged");
        player.sendMessage("- " + ChatColor.YELLOW + sessionRollback + ChatColor.WHITE + " edits rollback'd");

    }

    public static void logBlocks(int size) {
        sessionEdits += size;
        globalEdits += size;
    }

    public static void logRollback(long size) {
        sessionRollback += size;
        globalRollback += size;
        sessionEdits -= size;
        globalEdits -= size;
    }

}

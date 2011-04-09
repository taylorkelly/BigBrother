package me.taylorkelly.bigbrother;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.util.Time;

// Rule 1 - If you end up using ResultSet, you're doing it wrong.
public class Cleanser {

    private static CleanupThread cleanupThread = null;

    public static boolean needsCleaning() {
        return BBSettings.cleanseAge != -1 || BBSettings.maxRecords != -1;
    }

    /**
     * Start the cleaner thread and pray to God it finishes.
     * @param player Person to blame for the lag. Or null.
     */
    public static void clean(Player player) {
        if (cleanupThread != null && cleanupThread.done) {
            cleanupThread = null;
        }
        if (cleanupThread == null || !cleanupThread.isAlive()) {
            cleanupThread = new CleanupThread(player);
            cleanupThread.start();
        } else {
            if (player != null) {
                player.chat(BigBrother.premessage + " Cleaner already busy.  Try again later.");
            }
        }
    }

    public static void initialize(BigBrother bigbrother) {
        bigbrother.getServer().getScheduler().scheduleAsyncRepeatingTask(bigbrother, new CleanupTask(), 50, 26000);
    }

    private static class CleanupTask implements Runnable {
        @Override
        public void run() {
            clean(null);
        }
    }

    /**
     * Cleanser thread, to avoid blocking the main app when cleaning crap.
     * @author N3X15 <nexis@7chan.org>
     *
     */
    private static class CleanupThread extends Thread {

        private long cleanedSoFarAge = 0;
        private long cleanedSoFarNumber = 0;
        private boolean done = false;
        private final Player player;

        /**
         * @param p The player. Can be null.
         */
        public CleanupThread(Player p) {
            // Constructor.
            player = p;
            this.setName("Cleanser");
        }

        @Override
        public void run() {
            //BBLogging.info("Starting Cleanser thread...");
            if (BBSettings.cleanseAge != -1) {
                cleanByAge();
            }

            if (BBSettings.maxRecords != -1) {
                //cleanByNumber();
            }
            //BBLogging.info("Ending Cleanser thread...");
            done = true; // Wait for cleanup
        }

        private void cleanByAge() {
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = ConnectionManager.getConnection();
                if(conn==null) return;
                stmt = conn.createStatement();
                long start = System.currentTimeMillis() / 1000;

                cleanedSoFarAge = stmt.executeUpdate(BBDataTable.getInstance().getCleanseAged(Long.valueOf(Time.ago(BBSettings.cleanseAge)),
                		BBSettings.deletesPerCleansing));
                String timespent = Time.formatDuration(System.currentTimeMillis() / 1000 - start);

                String words = String.format("Cleaned out %d records because of age in %s.", cleanedSoFarAge, timespent);
                if (player == null) {
                    BBLogging.debug(words);
                } else {
                    synchronized (player) {
                        player.sendMessage(ChatColor.BLUE + words);
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                BBLogging.severe("Cleanse SQL exception (by age)", ex);
            } finally {
                ConnectionManager.cleanup( "Cleanse (by age)",  conn, stmt, null );
            }
        }

        @SuppressWarnings("unused")
        private void cleanByNumber() {
            if (BBSettings.usingDBMS(DBMS.MYSQL) || BBSettings.usingDBMS(DBMS.POSTGRES)) {
                if (BBSettings.maxRecords < 0) {
                    // Fix exception caused when trying to delete -1 records.
                    BBLogging.info("Skipping; max-records is negative.");
                    return;
                }
                Connection conn = null;
                Statement stmt = null;
                try {
                    conn = ConnectionManager.getConnection();
                    if(conn==null) return;
                    stmt = conn.createStatement();
                    long start = System.currentTimeMillis() / 1000;

                    cleanedSoFarNumber = stmt.executeUpdate(BBDataTable.getInstance().getCleanseByLimit(Long.valueOf(BBSettings.maxRecords),
                    		BBSettings.deletesPerCleansing));

                    String timespent = Time.formatDuration(System.currentTimeMillis() / 1000 - start);


                    String words = String.format("Cleaned out %d records because of number in %s.", cleanedSoFarNumber, timespent);
                    if (player == null) {
                        BBLogging.info(words);
                    } else {
                        synchronized (player) {
                            player.sendMessage(ChatColor.BLUE + words);
                        }
                    }
                    conn.commit();
                } catch (SQLException ex) {
                    BBLogging.severe("Cleanse SQL exception (by #)", ex);
                    if (player != null) {
                        synchronized (player) {
                            player.sendMessage(ChatColor.RED + "Action failed, read server log for the gory details.");
                        }
                    }
                } finally {
                    ConnectionManager.cleanup( "Cleanse (by #)",  conn, stmt, null );
                }

            } else {
                String words = "SQLite can't cleanse by # of records.";
                if (player == null) {
                    BBLogging.info(words);
                } else {
                    synchronized (player) {
                        player.sendMessage(ChatColor.RED + words);
                    }
                }
            }
        }
    }
}

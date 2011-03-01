package me.taylorkelly.bigbrother;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.util.Time;

// Rule 1 - If you end up using ResultSet, you're doing it wrong.
class Cleanser {
	private static CleanupThread cleanupThread=null;

	static boolean needsCleaning() {
		return BBSettings.cleanseAge != -1 || BBSettings.maxRecords != -1;
	}

	/**
	 * Start the cleaner thread and pray to God it finishes.
	 * @param player Person to blame for the lag. Or null.
	 */
	static void clean(Player player) {
		if(cleanupThread==null || !cleanupThread.isAlive()) {
			cleanupThread = new CleanupThread(player);
			cleanupThread.start();
		} else {
			if(player!=null)
				player.chat(BigBrother.premessage+" Cleaner already busy.  Try again later.");
		}
	}
	
	/**
	 * Cleanser thread, to avoid blocking the main app when cleaning crap.
	 * @author Rob
	 *
	 */
	private static class CleanupThread extends Thread {
		private long cleanedSoFarAge=0;
		private long cleanedSoFarNumber=0;
		private Player player;

		/**
		 * 
		 * @param p Can be null.
		 */
		public CleanupThread(Player p) {
			// Constructor.
			player=p;
		}

		@Override
		public void run() {
			if (BBSettings.cleanseAge != -1) {
				cleanByAge();
			}

			if(BBSettings.maxRecords != -1) {
				cleanByNumber();
			}
		}

		private void cleanByAge() {
			Connection conn = null;
			Statement stmt = null;
			try {
				conn = ConnectionManager.getConnection();
				stmt = conn.createStatement();
				long start = System.currentTimeMillis()/1000;
				cleanedSoFarAge = stmt.executeUpdate("DELETE FROM `bbdata` WHERE date < " + Long.valueOf(Time.ago(BBSettings.cleanseAge)) + ";");
				//cleanedSoFarAge += stmt.executeUpdate("DELETE FROM `bbdata` WHERE date < " + Long.valueOf(Time.ago(BBSettings.cleanseAge)) + " LIMIT "+Long.valueOf(BBSettings.deletesPerCleansing)+";");
				String timespent = Time.formatDuration(System.currentTimeMillis()/1000 - start);



				String words=String.format("Cleaned out %d records because of age in %s.",cleanedSoFarAge,timespent);
				if(player==null)
					BBLogging.info(words);
				else
				{
					synchronized(player) {
						player.sendMessage(ChatColor.BLUE + words);
					}
				}

				conn.commit();
			} catch (SQLException ex) {
				BBLogging.severe("Cleanse SQL exception (by age)", ex);
			} finally {
				try {
					if (stmt != null) {
						stmt.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException ex) {
					BBLogging.severe("Cleanse SQL exception (by age) (on close)", ex);
				}
			}
		}

		private void cleanByNumber() {
			if (BBSettings.mysql) {
				if(BBSettings.maxRecords<0)
				{
					// Fix exception caused when trying to delete -1 records.
					BBLogging.info("Skipping; max-records is negative.");
					return;
				}
				Connection conn = null;
				Statement stmt = null;
				try {
					conn = ConnectionManager.getConnection();
					stmt = conn.createStatement();
					long start = System.currentTimeMillis()/1000;
					cleanedSoFarNumber = stmt.executeUpdate("DELETE FROM `bbdata` as b LEFT OUTER JOIN (SELECT `id` FROM `bbdata`  ORDER BY `id` DESC LIMIT 0,"+Long.valueOf(BBSettings.maxRecords)+") as j on j.id=b.id where j.id is null;");
					//cleanedSoFarNumber += stmt.executeUpdate("DELETE FROM `bbdata` as b LEFT OUTER JOIN (SELECT `id` FROM `bbdata`  ORDER BY `id` DESC LIMIT 0,"+Long.valueOf(BBSettings.maxRecords)+") as j on j.id=b.id where j.id is null LIMIT "+Long.valueOf(BBSettings.deletesPerCleansing)+";");
					String timespent = Time.formatDuration(System.currentTimeMillis()/1000 - start);


					String words=String.format("Cleaned out %d records because of number in %s.",cleanedSoFarNumber,timespent);
					if(player==null)
						BBLogging.info(words);
					else
					{
						synchronized(player) {
							player.sendMessage(ChatColor.BLUE + words);
						}
					}
					conn.commit();
				} catch (SQLException ex) {
					BBLogging.severe("Cleanse SQL exception (by #)", ex);
				} finally {
					try {
						if (stmt != null) {
							stmt.close();
						}
						if (conn != null) {
							conn.close();
						}
					} catch (SQLException ex) {
						BBLogging.severe("Cleanse SQL exception (by #) (on close)", ex);
					}
				}

			} else {
				player.sendMessage(ChatColor.RED + "SQLite can't cleanse by # of records.");
			}
		}
	}
}
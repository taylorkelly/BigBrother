import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BigBrother extends Plugin {
	private final BBListener listener = new BBListener();
	private Logger log;
	public static String name = "BigBrother";
	public static String version = "0.6.4";
	public static String premessage = Colors.LightBlue + "[BBROTHER]: "
			+ Colors.White;
	
	/*static String SQLdriver = "com.mysql.jdbc.Driver";
	static String SQLusername = "root";
	static String SQLpassword = "root";
	static String SQLdb = "jdbc:mysql://localhost:3306/minecraft";

	static String BB_TABLE = "CREATE TABLE `bigbrother` (`id` int(15) NOT NULL AUTO_INCREMENT, `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00', `player` varchar(30) NOT NULL DEFAULT 'Player', `action` int(15) NOT NULL DEFAULT '0', `x` int(10) NOT NULL DEFAULT '0', `y` int(10) NOT NULL DEFAULT '0', `z` int(10) NOT NULL DEFAULT '0', `data` varchar(50) NOT NULL DEFAULT '', PRIMARY KEY (`id`));";

	private boolean tableExists()
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(SQLdb,
					SQLusername, SQLpassword);
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, "bigbrother", null);
			if (!rs.next())
			{
				return false;
			}
			return true;
		} catch (SQLException ex) {
			log.log(Level.SEVERE, name + " SQL exception", ex);
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, name + " SQL exception on close", ex);
			}
		}
	}
	
	public void createTable() {
		    try {
		      Connection conn = DriverManager.getConnection(SQLdb, SQLusername, 
		        SQLpassword);
		      conn.setAutoCommit(false);
		      try {
		        Statement st = conn.createStatement();

		        st.executeUpdate(BB_TABLE);

		        conn.close();
		      } catch (SQLException localSQLException) {
			      log.log(Level.SEVERE, "Could not create the table", localSQLException);
		      }
		    } catch (Exception e) {
		      log.log(Level.SEVERE, "Could not create the table", e);
		    }
		  }
	
	public void test() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(SQLdb,
					SQLusername, SQLpassword);
			ps = conn
					.prepareStatement(
							"INSERT INTO bigbrother (date, player, action, x, y, z, data) VALUES (now(),?,?,?,?,?,?)",
							1);
			ps.setString(1, "tkelly");
			ps.setInt(2, 2);
			ps.setInt(3, 1);
			ps.setInt(4, 2);
			ps.setInt(5, 3);
			ps.setString(6, "you messed up");

			ps.executeUpdate();
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "Unable to add protection into SQL", ex);
			try {
				if (ps != null) {
					ps.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException er) {
				log.log(Level.SEVERE, "Could not close connection to SQL", er);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "Could not close connection to SQL", ex);
			}
		}
	}*/

	public void enable() {
	}

	public void disable() {
		if (BBSettings.emailTimer != null)
			BBSettings.emailTimer.cancel();
	}

	public void initialize() {
		log = Logger.getLogger("Minecraft");
		log.info(name + " " + version + " initialized");

		BBLogger.initialize();
		BBSettings.initialize();
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this,
				PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this,
				PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, listener,
				this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE, listener,
				this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.CHAT, listener, this,
				PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, listener,
				this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, listener,
				this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.TELEPORT, listener, this,
				PluginListener.Priority.MEDIUM);
		
		/*if(!tableExists()) {
			System.out.println("Table doesn't exist... creating");
			createTable();
		} else {
			System.out.println("Table exists!");

		}
		test();*/
	}

	static boolean toggleWatch(String player) {
		boolean watching = false;

		if (BBSettings.watchList.contains(player)) {
			BBSettings.watchList.remove(player);
			if (BBSettings.verbose) {
				BBLogger.log(player, (BBSettings.getTime()
						+ ": No longer watching " + player + "\n"));
				BBNotify.notify("No longer watching " + player + "\n");
			} else {
				BBLogger.log(player, BBSettings.getTime() + ": NotWatching\n");
				BBNotify.notify("Not watching " + player + "\n");
			}
			saveWatchList();
		} else {
			watch(player);
			watching = true;
		}
		return watching;
	}

	static void watch(String player) {
		if (!BBSettings.watchList.contains(player)) {
			BBSettings.watchList.add(player);
			if (BBSettings.verbose) {
				BBLogger.log(player, (BBSettings.getTime() + ": Now watching "
						+ player + "\n"));
				BBNotify.notify("Now watching " + player + "\n");
			} else {
				BBLogger.log(player, BBSettings.getTime() + ": NotWatching\n");
				BBNotify.notify("Watching " + player + "\n");
			}
			saveWatchList();
		}
	}

	private static void saveWatchList() {
		String list = "";

		for (String player : BBSettings.watchList) {
			list += player + ",";
		}

		if (list.startsWith(","))
			list = list.substring(1);
		if (list.endsWith(","))
			list = list.substring(0, list.length() - 1);

		PropertiesFile pf = new PropertiesFile("bigbrother.txt");
		try {
			pf.load();
		} catch (IOException e) {
		}
		pf.setString("watchedplayers", list);
		pf.save();
	}

	static void saveSeenPlayers() {
		String list = "";

		for (String player : BBSettings.seenPlayers) {
			list += player + ",";
		}

		if (list.startsWith(","))
			list = list.substring(1);
		if (list.endsWith(","))
			list = list.substring(0, list.length() - 1);

		PropertiesFile pf = new PropertiesFile("bigbrother.txt");
		try {
			pf.load();
		} catch (IOException e) {
		}
		pf.setString("seenPlayers", list);
		pf.save();
	}

	public boolean verifyAdmin(String apt, String input, Player heir) {
		return (heir.canUseCommand(apt) && input.equalsIgnoreCase(apt));
	}

	public static String fixName(String player) {
		return player.replace(".", "").replace(":", "").replace("<", "")
				.replace(">", "").replace("*", "").replace("\\", "")
				.replace("/", "").replace("?", "").replace("\"", "")
				.replace("|", "");
	}
}
package me.taylorkelly.bigbrother.datablock;
import java.sql.*;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;

public class BBDataBlock {
	private final static String BBDATA_NAME = "bbdata";
	private final static String BBDATA_TABLE = "CREATE TABLE `"
			+ BBDATA_NAME
			+ "` (`id` int(15) NOT NULL AUTO_INCREMENT, `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00', `player` varchar(30) NOT NULL DEFAULT 'Player', `action` tinyint(2) NOT NULL DEFAULT '0', `x` int(10) NOT NULL DEFAULT '0', `y` int(10) NOT NULL DEFAULT '0', `z` int(10) NOT NULL DEFAULT '0', `data` varchar(50) NOT NULL DEFAULT '', PRIMARY KEY (`id`));";
	private String player;
	private int action;
	private int x;
	private int y;
	private int z;
	private String data;
	
	public static final int BLOCK_BROKEN = 0;
	public static final int BLOCK_PLACED = 1;
	public static final int SIGN_TEXT = 2;
	public static final int TELEPORT = 3;
	public static final int CHEST_STEAL = 4;
	public static final int COMMAND = 5;
	public static final int CHAT = 6;
	public static final int DISCONNECT = 7;
	public static final int LOGIN = 8;
	

	public BBDataBlock(String player, int action, int x, int y, int z,
			String data) {
		this.player = player;
		this.action = action;
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}

	private static void createTable() {
		try {
			Connection conn = DriverManager.getConnection(BBSettings.db,
					BBSettings.username, BBSettings.password);
			conn.setAutoCommit(false);
			try {
				Statement st = conn.createStatement();

				st.executeUpdate(BBDATA_TABLE);

				conn.close();
			} catch (SQLException localSQLException) {
				BigBrother.log.log(Level.SEVERE, "Could not create the table",
						localSQLException);
			}
		} catch (Exception e) {
			BigBrother.log.log(Level.SEVERE, "Could not create the table", e);
		}
	}

	public void send() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(BBSettings.db,
					BBSettings.username, BBSettings.password);
			ps = conn
					.prepareStatement(
							"INSERT INTO "
									+ BBDATA_NAME
									+ " (date, player, action, x, y, z, data) VALUES (now(),?,?,?,?,?,?)",
							1);
			ps.setString(1, player);
			ps.setInt(2, action);
			ps.setInt(3, x);
			ps.setInt(4, y);
			ps.setInt(5, z);
			ps.setString(6, data);
			ps.executeUpdate();
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE,
					"[BBROTHER]: Data Insert SQL Exception");
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
				BigBrother.log.log(Level.SEVERE,
						"[BBROTHER]: Data Insert SQL Exception (on close)");
			}
		}
	}

	public static void initialize() {
		if (!tableExists()) {
			System.out.println("Table doesn't exist... creating");
			createTable();
		} else {
			System.out.println("Table exists!");
		}
	}

	private static boolean tableExists() {
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(BBSettings.db,
					BBSettings.username, BBSettings.password);
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, BBDATA_NAME, null);
			if (!rs.next())
				return false;
			return true;
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE,
					"[BBROTHER]: Table Check SQL Exception");
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				BigBrother.log.log(Level.SEVERE,
						"[BBROTHER]: Table Check SQL Exception (on closing)");
			}
		}
	}
}
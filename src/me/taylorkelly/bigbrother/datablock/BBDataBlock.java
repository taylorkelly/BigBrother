package me.taylorkelly.bigbrother.datablock;

import java.io.*;
import java.sql.*;
import java.util.Calendar;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.DataDest;

public class BBDataBlock {
	private static Calendar cal = Calendar.getInstance();
	private static final char separator = '\u0095';
	public final static String BBDATA_NAME = "bbdata";
	private final static String BBDATA_TABLE = "CREATE TABLE `" + BBDATA_NAME + "` (`id` int(15) NOT NULL AUTO_INCREMENT, "
			+ "`date` bigint NOT NULL DEFAULT '0', `player` varchar(30) NOT NULL DEFAULT 'Player', `action` tinyint(2) NOT NULL DEFAULT '0',"
			+ " `world` tinyint(2) NOT NULL DEFAULT '0', `x` int(10) NOT NULL DEFAULT '0', `y` int(10) NOT NULL DEFAULT '0', "
			+ "`z` int(10) NOT NULL DEFAULT '0', `data` varchar(50) NOT NULL DEFAULT '', `rbacked` boolean NOT NULL DEFAULT '0', PRIMARY KEY (`id`));";
	private String player;
	private int action;
	private int x;
	private int y;
	private int z;
	private int world;
	private String data;

	public static final int BLOCK_BROKEN = 0;
	public static final int BLOCK_PLACED = 1;
	public static final int SIGN_TEXT = 2;
	public static final int TELEPORT = 3;
	public static final int DELTA_CHEST = 4;
	public static final int COMMAND = 5;
	public static final int CHAT = 6;
	public static final int DISCONNECT = 7;
	public static final int LOGIN = 8;
	public static final int DOOR_OPEN = 9;
	public static final int BUTTON_PRESS = 10;
	public static final int LEVER_SWITCH = 10;

	public BBDataBlock(String player, int action, int world, int x, int y, int z, String data) {
		this.player = player;
		this.action = action;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}

	public void send() {
		switch (BBSettings.dataDest) {
		case MYSQL:
			sendSql(false);
			break;
		case MYSQL_AND_FLAT:
			sendSql(false);
			sendFlatFile();
			break;
		case SQLITE:
			sendSql(true);
		case SQLITE_AND_FLAT:
			sendSql(true);
			sendFlatFile();
			break;
		}
	}

	private void sendFlatFile() {
		//TODO separate into different names
		File file = new File(BigBrother.directory + File.separator + "logs", BBDATA_NAME + ".log");
		StringBuilder builder = new StringBuilder(cal.getTimeInMillis() + "");
		builder.append(separator);
		builder.append(player);
		builder.append(separator);
		builder.append(action);
		builder.append(separator);
		builder.append(world);
		builder.append(separator);
		builder.append(x);
		builder.append(separator);
		builder.append(y);
		builder.append(separator);
		builder.append(z);
		builder.append(separator);
		builder.append(data);
		builder.append(separator);
		builder.append("0");
		BufferedWriter bwriter = null;
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(file, true);
			bwriter = new BufferedWriter(fwriter);
			bwriter.write(builder.toString());
			bwriter.flush();
		} catch (IOException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert IO Exception (" + action + ")");
		} finally {
			try {
				if (bwriter != null)
					bwriter.close();
				if (fwriter != null)
					fwriter.close();
			} catch (IOException e) {
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert IO Exception (on close) (" + action + ")");
			}
		}
	}

	private void sendSql(boolean sqlite) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (sqlite) {
				Class.forName("org.sqlite.JDBC");  
	            conn = DriverManager.getConnection(BBSettings.liteDb);
			} else {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(BBSettings.db, BBSettings.username, BBSettings.password);
				conn.setAutoCommit(false);
			}
			ps = conn.prepareStatement("INSERT INTO " + BBDATA_NAME + " (date, player, action, world, x, y, z, data, rbacked) VALUES (?,?,?,?,?,?,?,?,0)", 1);
			ps.setLong(1, cal.getTimeInMillis());
			ps.setString(2, player);
			ps.setInt(3, action);
			ps.setInt(4, world);
			ps.setInt(5, x);
			ps.setInt(6, y);
			ps.setInt(7, z);
			ps.setString(8, data);
			ps.executeUpdate();
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert SQL Exception (" + action + ")");
		} catch (ClassNotFoundException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert SQL Exception (cnf)"  + ((sqlite)?"sqlite":"mysql"));
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
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert SQL Exception (on close)  (" + action + ")");
			}
		}
	}

	public static void initialize() {
		if (BBSettings.dataDest == DataDest.MYSQL || BBSettings.dataDest == DataDest.MYSQL_AND_FLAT) {
			if (!bbdataTableExists()) {
				BigBrother.log.info("[BBROTHER]: Generating bbdata table");
				createBBDataTable();
			}
		}
	}

	private static boolean bbdataTableExists() {
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(BBSettings.db, BBSettings.username, BBSettings.password);
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, BBDATA_NAME, null);
			if (!rs.next())
				return false;
			return true;
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Table Check SQL Exception");
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Table Check SQL Exception (on closing)");
			}
		}
	}

	private static void createBBDataTable() {
		Connection conn = null;
		Statement st = null;
		try {
			conn = DriverManager.getConnection(BBSettings.db, BBSettings.username, BBSettings.password);
			conn.setAutoCommit(false);
			st = conn.createStatement();
			st.executeUpdate(BBDATA_TABLE);
		} catch (SQLException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Could not create the table");
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Could not create the table (on close)");
			}
		}
	}
	
	public static String fixName(String player) {
		return player.replace(".", "").replace(":", "").replace("<", "")
				.replace(">", "").replace("*", "").replace("\\", "")
				.replace("/", "").replace("?", "").replace("\"", "")
				.replace("|", "");
	}
}
package me.taylorkelly.bigbrother.datablock;

import java.io.*;
import java.sql.*;
import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.Server;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.DataDest;

public abstract class BBDataBlock {
	private static Calendar cal = Calendar.getInstance();
	private static final char separator = '\u0095';
	public final static String BBDATA_NAME = "bbdata";
	private final static String BBDATA_TABLE_MYSQL = "CREATE TABLE `" + BBDATA_NAME + "` (`id` int(15) NOT NULL AUTO_INCREMENT, `date` bigint NOT NULL DEFAULT '0', `player` varchar(30) NOT NULL DEFAULT 'Player', `action` tinyint(2) NOT NULL DEFAULT '0', `world` tinyint(2) NOT NULL DEFAULT '0', `x` int(10) NOT NULL DEFAULT '0', `y` int(10) NOT NULL DEFAULT '0', `z` int(10) NOT NULL DEFAULT '0', `data` varchar(50) NOT NULL DEFAULT '', `rbacked` boolean NOT NULL DEFAULT '0', PRIMARY KEY (`id`));";
	private final static String BBDATA_TABLE_SQLITE = "CREATE TABLE `" + BBDATA_NAME + "` (`id` INTEGER PRIMARY KEY, `date` bigint NOT NULL DEFAULT '0', `player` varchar(30) NOT NULL DEFAULT 'Player', `action` tinyint(2) NOT NULL DEFAULT '0', `world` tinyint(2) NOT NULL DEFAULT '0', `x` int(10) NOT NULL DEFAULT '0', `y` int(10) NOT NULL DEFAULT '0', `z` int(10) NOT NULL DEFAULT '0', `data` varchar(50) NOT NULL DEFAULT '', `rbacked` boolean NOT NULL DEFAULT '0');";

	protected String player;
	protected int action;
	protected int x;
	protected int y;
	protected int z;
	protected int world;
	protected String data;

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
	public static final int LEVER_SWITCH = 11;

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
			break;
		case SQLITE_AND_FLAT:
			sendSql(true);
			sendFlatFile();
			break;
		}
	}

	private void sendFlatFile() {
		File dir = new File(BigBrother.directory + File.separator + "logs");
		if(!dir.exists()) dir.mkdir();
		File file = new File(BigBrother.directory + File.separator + "logs", fixName(player) + ".log");
		StringBuilder builder = new StringBuilder(cal.getTimeInMillis() + "");
		builder.append(" - ");
		builder.append(getAction(action));
		builder.append(" ");
		builder.append(world);
		builder.append("@(");
		builder.append(x);
		builder.append(",");
		builder.append(y);
		builder.append(",");
		builder.append(z);
		builder.append(") info: ");
		builder.append(data);
		BufferedWriter bwriter = null;
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(file, true);
			bwriter = new BufferedWriter(fwriter);
			bwriter.write(builder.toString());
			bwriter.newLine();
			bwriter.flush();
		} catch (IOException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert IO Exception (" + action + ")");
		} finally {
			try {
				if (bwriter != null) {
					bwriter.flush();
					bwriter.close();
				} if (fwriter != null)
					fwriter.close();
			} catch (IOException e) {
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert IO Exception (on close) (" + action + ")");
			}
		}
	}

	private String getAction(int action) {
		switch(action) {
		case(BLOCK_BROKEN):
			return "brokeBlock";
		case(BLOCK_PLACED):
			return "placedBlock";
		case(SIGN_TEXT):
			return "signText";
		case(TELEPORT):
			return "teleport";
		case(DELTA_CHEST):
			return "deltaChest";
		case(COMMAND):
			return "command";
		case(CHAT):
			return "chat";
		case(DISCONNECT):
			return "disconnect";
		case(LOGIN):
			return "login";
		case(DOOR_OPEN):
			return "door";
		case(BUTTON_PRESS):
			return "button";
		case(LEVER_SWITCH):
			return "lever";
		default:
			return "";
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
				conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
				conn.setAutoCommit(false);
				System.out.println("test");
			}
			ps = conn.prepareStatement("INSERT INTO " + BBDATA_NAME + " (date, player, action, world, x, y, z, data, rbacked) VALUES (?,?,?,?,?,?,?,?,0)");
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
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert SQL Exception (" + action + ")", ex);
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
		boolean sqlite = false;
		switch (BBSettings.dataDest) {
		case MYSQL:
		case MYSQL_AND_FLAT:
			sqlite = false;
			break;
		case SQLITE:
		case SQLITE_AND_FLAT:
			sqlite = true;
			break;
		}
		

		if (!bbdataTableExists(sqlite)) {
			BigBrother.log.info("[BBROTHER]: Generating bbdata table");
			createBBDataTable(sqlite);
		}
	}

	private static boolean bbdataTableExists(boolean sqlite) {
		Connection conn = null;
		ResultSet rs = null;
		try {
			if (sqlite) {
				Class.forName("org.sqlite.JDBC");  
	            conn = DriverManager.getConnection(BBSettings.liteDb);
			} else {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
				conn.setAutoCommit(false);
			}
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, BBDATA_NAME, null);
			if (!rs.next())
				return false;
			return true;
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Table Check SQL Exception" + ((sqlite)?" sqlite":" mysql"), ex);
			return false;
		} catch (ClassNotFoundException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Table Check SQL Exception (cnf)"  + ((sqlite)?" sqlite":" mysql"));
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

	private static void createBBDataTable(boolean sqlite) {
		Connection conn = null;
		Statement st = null;
		try {
			if (sqlite) {
				Class.forName("org.sqlite.JDBC");  
	            conn = DriverManager.getConnection(BBSettings.liteDb);
			} else {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
				conn.setAutoCommit(false);
			}
			st = conn.createStatement();
			if(sqlite) {
				st.executeUpdate(BBDATA_TABLE_SQLITE);
			} else {
				st.executeUpdate(BBDATA_TABLE_MYSQL);
			}
		} catch (SQLException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Create Table SQL Exception" + ((sqlite)?" sqlite":" mysql"), e);
		} catch (ClassNotFoundException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Create Table SQL Exception (cnf)"  + ((sqlite)?" sqlite":" mysql"));
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
	
	public abstract void rollback(Server server);
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return null;
	}
	
	public static BBDataBlock getBBDataBlock(String player, int action, int world, int x, int y, int z, String data) {
		switch(action) {
		case(BLOCK_BROKEN):
			return BrokenBlock.getBBDataBlock(player, world, x, y, z, data);
		case(BLOCK_PLACED):
			return PlacedBlock.getBBDataBlock(player, world, x, y, z, data);
		case(SIGN_TEXT):
			return SignText.getBBDataBlock(player, world, x, y, z, data);
		case(TELEPORT):
			return Teleport.getBBDataBlock(player, world, x, y, z, data);
		case(DELTA_CHEST):
			return DeltaChest.getBBDataBlock(player, world, x, y, z, data);
		case(COMMAND):
			return Command.getBBDataBlock(player, world, x, y, z, data);
		case(CHAT):
			return Chat.getBBDataBlock(player, world, x, y, z, data);
		case(DISCONNECT):
			return Disconnect.getBBDataBlock(player, world, x, y, z, data);
		case(LOGIN):
			return Login.getBBDataBlock(player, world, x, y, z, data);
		case(DOOR_OPEN):
			return DoorOpen.getBBDataBlock(player, world, x, y, z, data);
		case(BUTTON_PRESS):
			return ButtonPress.getBBDataBlock(player, world, x, y, z, data);
		case(LEVER_SWITCH):
			return LeverSwitch.getBBDataBlock(player, world, x, y, z, data);
		default:
			return null;
		}
	}
}
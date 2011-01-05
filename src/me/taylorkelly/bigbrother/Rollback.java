package me.taylorkelly.bigbrother;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;

import org.bukkit.*;

public class Rollback {
	private Server server;
	private ArrayList<Player> recievers;
	private ArrayList<String> players;

	private LinkedList<BBDataBlock> list;
	
	private static LinkedList<BBDataBlock> lastRollback = new LinkedList<BBDataBlock>();

	public Rollback(Server server) {
		this.server = server;
		recievers = new ArrayList<Player>();
		list = new LinkedList<BBDataBlock>();
		players = new ArrayList<String>();
	}

	public void addReciever(Player player) {
		recievers.add(player);
	}
	
	public void addPlayer(String player) {
		players.add(player);
	}
	
	public void rollback() {
		switch (BBSettings.dataDest) {
		case MYSQL:
		case MYSQL_AND_FLAT:
			mysqlRollback(false);
			break;
		case SQLITE:
		case SQLITE_AND_FLAT:
			mysqlRollback(true);
			break;
		}
	}

	private void mysqlRollback(boolean sqlite) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			if (sqlite) {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(BBSettings.liteDb);
			} else {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
			}

			// TODO maybe more customizable actions?
			String actionString = "action = " + BBDataBlock.BLOCK_BROKEN + " or action = " + BBDataBlock.BLOCK_PLACED + " or action = "
					+ BBDataBlock.DELTA_CHEST + " or action = " + BBDataBlock.SIGN_TEXT;
			
			String playerString = createPlayerString();
			ps = conn.prepareStatement("SELECT * from " + BBDataBlock.BBDATA_NAME + " where (" + actionString
					+ ")" + playerString + " and rbacked = 0 order by date desc");

			set = ps.executeQuery();

			int size = 0;
			while (set.next()) {
				list.addLast(BBDataBlock.getBBDataBlock(set.getString("player"), set.getInt("action"), set.getInt("world"), set.getInt("x"), set.getInt("y"),
						set.getInt("z"), set.getString("data")));
				size++;
			}
			if (size > 0) {
				for (Player player : recievers) {
					player.sendMessage(BigBrother.premessage + "Rolling back " + size + " edits.");
				}
				try {
					rollbackBlocks();
					ps = conn.prepareStatement("UPDATE " + BBDataBlock.BBDATA_NAME + " set rbacked = 1 where (" + actionString
							+ ")" + playerString + " and rbacked = 0");
					ps.execute();
					for (Player player : recievers) {
						player.sendMessage(BigBrother.premessage + "Successfully rollback'd all" + getPlayerSimpleString() + " changes.");
					}
				} catch (SQLException ex) {
					BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Rollback edit SQL Exception", ex);
				}
			} else {
				for (Player player : recievers) {
					player.sendMessage(BigBrother.premessage + "Nothing to rollback.");
				}
			}
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Rollback get SQL Exception", ex);
		} catch (ClassNotFoundException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Rollback SQL Exception (cnf)" + ((sqlite) ? "sqlite" : "mysql"));
		} finally {

			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Rollback get SQL Exception (on close)");
			}
		}
	}

	private String getPlayerSimpleString() {
		if(players.size() == 0) return "";
		StringBuilder builder = new StringBuilder(" of");
		for(String name: players) {
			builder.append(" ");
			builder.append(name);
			builder.append("'s, ");
		}
		if (builder.toString().contains(","))
			builder.delete(builder.lastIndexOf(","), builder.length());
		return builder.toString();
	}

	private String createPlayerString() {
		if(players.size() == 0) return "";
		StringBuilder builder = new StringBuilder(" and (");
		for(String name: players) {
			builder.append("player");
			builder.append(" = ");
			builder.append(name);
			builder.append(" or ");
		}
		if (builder.toString().contains("or"))
			builder.delete(builder.lastIndexOf("or")-1, builder.length());
		builder.append(")");
		return builder.toString();
	}

	private void rollbackBlocks() {
		lastRollback.clear();
		while (list.size() > 0) {
			BBDataBlock dataBlock = list.removeFirst();
			if (dataBlock != null) {
				lastRollback.addLast(dataBlock);
				dataBlock.rollback(server);
			}
		}
	}
	
	public static boolean canUndo() {
		if(lastRollback != null) {
			return lastRollback.size() > 0;
		} else return false;
	}
	
	public static int undoSize() {
		if(lastRollback != null) {
			return lastRollback.size();
		} else return 0;
	}
	
	public static void undo(Server server) {
		while (lastRollback.size() > 0) {
			BBDataBlock dataBlock = lastRollback.removeFirst();
			if (dataBlock != null) {
				dataBlock.redo(server);
			}
		}
	}
}

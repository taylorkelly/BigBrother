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
	private String playerName;
	private ArrayList<Player> players;
	private LinkedList<BBDataBlock> list;

	public Rollback(Server server, String playerName) {
		this.server = server;
		this.playerName = playerName;
		players = new ArrayList<Player>();
		list = new LinkedList<BBDataBlock>();
	}

	public void addReciever(Player player) {
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
				conn.setAutoCommit(false);
			}

			// TODO maybe more customizable actions?
			String actionString = "action = " + BBDataBlock.BLOCK_BROKEN + " or action = " + BBDataBlock.BLOCK_PLACED + " or action = "
					+ BBDataBlock.DELTA_CHEST + " or action = " + BBDataBlock.SIGN_TEXT;
			ps = conn.prepareStatement("SELECT * from " + BBDataBlock.BBDATA_NAME + " where (" + actionString
					+ ") and player = ? and rbacked = 0 order by date desc");

			ps.setString(1, playerName);
			set = ps.executeQuery();

			int size = 0;
			while (set.next()) {
				list.addLast(BBDataBlock.getBBDataBlock(set.getString("player"), set.getInt("action"), set.getInt("world"), set.getInt("x"), set.getInt("y"),
						set.getInt("z"), set.getString("data")));
				size++;
			}
			if (size > 0) {
				for (Player player : players) {
					player.sendMessage(BigBrother.premessage + "Rolling back " + size + " edits.");
				}
				try {
					rollbackBlocks();
					ps = conn.prepareStatement("UPDATE " + BBDataBlock.BBDATA_NAME + " set rbacked = 1 where (" + actionString
							+ ") and player = ? and rbacked = 0");
					ps.setString(1, playerName);
					ps.execute();
					for (Player player : players) {
						player.sendMessage(BigBrother.premessage + "Successfully rollback'd.");
					}
				} catch (SQLException ex) {
					BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Rollback edit SQL Exception", ex);
				}
			} else {
				for (Player player : players) {
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

	private void rollbackBlocks() {
		while (list.size() > 0) {
			BBDataBlock dataBlock = list.removeFirst();
			if (dataBlock != null)
				dataBlock.rollback(server);
		}
	}
}

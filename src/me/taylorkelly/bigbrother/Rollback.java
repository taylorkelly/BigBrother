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
	private LinkedList<RollbackDataBlock> list;

	public Rollback(Server server, String playerName) {
		this.server = server;
		this.playerName = playerName;
		players = new ArrayList<Player>();
		list = new LinkedList<RollbackDataBlock>();
	}

	public void addReciever(Player player) {
		players.add(player);
	}

	public void rollback() {
		switch(BBSettings.dataDest) {
		case MYSQL:
		case MYSQL_AND_FLAT:
			mysqlRollback();
			break;
		case FLAT:
			flatRollback();
			break;
		}
	}	
	
	private void mysqlRollback() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(BBSettings.db, BBSettings.username, BBSettings.password);
			conn.setAutoCommit(false);

			// TODO maybe more customizable actions?
			String actionString = "action = " + BBDataBlock.BLOCK_BROKEN + " or action = " + BBDataBlock.BLOCK_PLACED + " or action = "
					+ BBDataBlock.DELTA_CHEST + " or action = " + BBDataBlock.SIGN_TEXT;
			ps = conn.prepareStatement("SELECT * from " + BBDataBlock.BBDATA_NAME + " where (" + actionString
					+ ") player = ? and rbacked = 0 order by date desc", Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, playerName);
			rs = ps.executeQuery();

			if (!rs.next()) {
				for (Player player : players) {
					player.sendMessage(BigBrother.premessage + "Nothing to rollback.");
				}
			} else {
				rs.last();
				int size = rs.getRow();
				rs.first();
				for (Player player : players) {
					player.sendMessage(BigBrother.premessage + "Rolling back " + size + " edits.");
				}
				list.addLast(new RollbackDataBlock(rs.getString("player"), rs.getInt("action"), rs.getInt("world"), rs.getInt("x"), rs.getInt("y"), rs
						.getInt("z"), rs.getString("data")));

				try {
					ps = conn.prepareStatement("UPDATE " + BBDataBlock.BBDATA_NAME + " set rbacked = 1 where (" + actionString
							+ ") player = ? and rbacked = 0 order by date desc");
					ps.setString(1, playerName);
					ps.execute();
					for (Player player : players) {
						player.sendMessage(BigBrother.premessage + "Successfully rollback'd.");
					}
				} catch (SQLException ex) {
					BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Rollback edit SQL Exception");
				}
			}
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Rollback get SQL Exception");
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Rollback get SQL Exception (on close)");
			}
		}
	}

	private void flatRollback() {

	}

	private class RollbackDataBlock {
		private String player;
		private int action;
		private Location location;
		private String data;

		public RollbackDataBlock(String player, int action, int world, int x, int y, int z, String data) {
			this.player = player;
			this.action = action;
			this.location = new Location(server.getWorlds()[world], x, y, z);
			this.data = data;
		}

	}
}

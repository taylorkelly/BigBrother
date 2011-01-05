package me.taylorkelly.bigbrother;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;

import org.bukkit.*;
import org.bukkit.Color;

public class Finder {
	private Location location;
	private int radius;
	private ArrayList<Player> players;

	public Finder(Location location) {
		this.location = location;
		this.radius = BBSettings.defaultSearchRadius;
		players = new ArrayList<Player>();
	}

	public void setRadius(double radius) {
		this.radius = (int) radius;
	}

	public void addReciever(Player player) {
		players.add(player);
	}

	public void find() {
		switch (BBSettings.dataDest) {
		case MYSQL:
		case MYSQL_AND_FLAT:
			mysqlFind(false);
			break;
		case SQLITE:
		case SQLITE_AND_FLAT:
			mysqlFind(true);
			break;
		}
	}

	public void find(String player) {
		switch (BBSettings.dataDest) {
		case MYSQL:
		case MYSQL_AND_FLAT:
			mysqlFind(false, player);
			break;
		case SQLITE:
		case SQLITE_AND_FLAT:
			mysqlFind(true, player);
			break;
		}
	}

	public void find(ArrayList<String> players) {
		// TODO find around player
	}

	private void mysqlFind(boolean sqlite) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		HashMap<String, Integer> modifications = new HashMap<String, Integer>();
		try {
			if (sqlite) {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(BBSettings.liteDb);
			} else {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
			}

			// TODO maybe more customizable actions?
			String actionString = "action = " + BBDataBlock.BLOCK_BROKEN + " or action = " + BBDataBlock.BLOCK_PLACED;
			ps = conn.prepareStatement("SELECT player from " + BBDataBlock.BBDATA_NAME + " where (" + actionString
					+ ") and rbacked = 0 and x < ? and x > ? and y < ? and y > ?  and z < ? and z > ? order by date desc");

			ps.setInt(1, location.getBlockX() + radius);
			ps.setInt(2, location.getBlockX() - radius);
			ps.setInt(3, location.getBlockY() + radius);
			ps.setInt(4, location.getBlockY() - radius);
			ps.setInt(5, location.getBlockZ() + radius);
			ps.setInt(6, location.getBlockZ() - radius);
			rs = ps.executeQuery();

			int size = 0;
			while (rs.next()) {
				String player = rs.getString("player");
				if (modifications.containsKey(player)) {
					modifications.put(player, modifications.get(player) + 1);
				} else {
					modifications.put(player, 1);
					size++;
				}
			}
			if (size > 0) {
				StringBuilder playerList = new StringBuilder();
				for (Entry<String, Integer> entry : modifications.entrySet()) {
					playerList.append(entry.getKey());
					playerList.append(" (");
					playerList.append(entry.getValue());
					playerList.append("), ");
				}
				playerList.delete(playerList.lastIndexOf(","), playerList.length());
				for (Player player : players) {
					player.sendMessage(BigBrother.premessage + size + " player(s) have modified this area:");
					player.sendMessage(playerList.toString());
				}
			} else {
				for (Player player : players) {
					player.sendMessage(BigBrother.premessage + "No modifications in this area.");
				}

			}
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Find SQL Exception");
		} catch (ClassNotFoundException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Find SQL Exception (cnf)" + ((sqlite) ? "sqlite" : "mysql"));
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Find SQL Exception (on close)");
			}
		}
	}

	private void mysqlFind(boolean sqlite, String playerName) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		HashMap<Integer, Integer> creations = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> destructions = new HashMap<Integer, Integer>();

		try {
			if (sqlite) {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(BBSettings.liteDb);
			} else {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
			}

			// TODO maybe more customizable actions?
			String actionString = "action = " + BBDataBlock.BLOCK_BROKEN + " or action = " + BBDataBlock.BLOCK_PLACED;
			ps = conn.prepareStatement("SELECT action, type from " + BBDataBlock.BBDATA_NAME + " where (" + actionString
					+ ") and rbacked = 0 and x < ? and x > ? and y < ? and y > ?  and z < ? and z > ? and player = ? order by date desc");

			ps.setInt(1, location.getBlockX() + radius);
			ps.setInt(2, location.getBlockX() - radius);
			ps.setInt(3, location.getBlockY() + radius);
			ps.setInt(4, location.getBlockY() - radius);
			ps.setInt(5, location.getBlockZ() + radius);
			ps.setInt(6, location.getBlockZ() - radius);
			ps.setString(7, playerName);
			rs = ps.executeQuery();

			int size = 0;
			while (rs.next()) {
				int action = rs.getInt("action");
				int type = rs.getInt("type");

				switch (action) {
				case (BBDataBlock.BLOCK_BROKEN):
					if (destructions.containsKey(type)) {
						destructions.put(type, destructions.get(type) + 1);
					} else {
						destructions.put(type, 1);
						size++;
					}
					break;
				case (BBDataBlock.BLOCK_PLACED):
					if (creations.containsKey(type)) {
						creations.put(type, creations.get(type) + 1);
					} else {
						creations.put(type, 1);
						size++;
					}
					break;
				}

			}
			if (size > 0) {
				StringBuilder creationList = new StringBuilder(Color.BLUE.toString());
				creationList.append("Placed Blocks: ");
				creationList.append(Color.WHITE);
				for (Entry<Integer, Integer> entry : creations.entrySet()) {
					creationList.append(Material.getMaterial(entry.getKey()));
					creationList.append(" (");
					creationList.append(entry.getValue());
					creationList.append("), ");
				}
				if (creationList.toString().contains(","))
					creationList.delete(creationList.lastIndexOf(","), creationList.length());
				StringBuilder brokenList = new StringBuilder(Color.RED.toString());
				brokenList.append("Broken Blocks: ");
				brokenList.append(Color.WHITE);
				for (Entry<Integer, Integer> entry : destructions.entrySet()) {
					brokenList.append(Material.getMaterial(entry.getKey()));
					brokenList.append(" (");
					brokenList.append(entry.getValue());
					brokenList.append("), ");
				}
				if (brokenList.toString().contains(","))
					brokenList.delete(brokenList.lastIndexOf(","), brokenList.length());
				for (Player player : players) {
					player.sendMessage(BigBrother.premessage + playerName + " has made " + size + " modifications");
					if (!creationList.toString().equals(""))
						player.sendMessage(creationList.toString());
					if (!brokenList.toString().equals(""))
						player.sendMessage(brokenList.toString());
				}
			} else {
				for (Player player : players) {
					player.sendMessage(BigBrother.premessage + playerName + " has no modifications in this area.");
				}

			}
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Find SQL Exception");
		} catch (ClassNotFoundException e) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Find SQL Exception (cnf)" + ((sqlite) ? "sqlite" : "mysql"));
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Find SQL Exception (on close)");
			}
		}
	}
}

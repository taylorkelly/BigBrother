package me.taylorkelly.bigbrother;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;

import org.bukkit.*;

public class Finder {
	private Location location;
	private int radius;
	private ArrayList<Player> players;

	public Finder(Location location) {
		this.location = location;
		this.radius = BBSettings.defaultRadius;
		players = new ArrayList<Player>();
	}

	public void setRadius(double radius) {
		this.radius = (int)radius;
	}

	public void addReciever(Player player) {
		players.add(player);
	}

	public void find() {
		switch(BBSettings.dataDest) {
		case MYSQL:
		case MYSQL_AND_FLAT:
			mysqlFind();
			break;
		case FLAT:
			flatFind();
			break;
		}
	}

	public void find(Player p) {
		//TODO find around player
	}
	
	public void find(ArrayList<Player> players) {
		//TODO find around player
	}
	
	private void flatFind() {
		// TODO Auto-generated method stub
		
	}

	public void mysqlFind() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			
			//TODO maybe more customizable actions?
			String actionString = "action = " + BBDataBlock.BLOCK_BROKEN + " or action = " + BBDataBlock.BLOCK_PLACED;
			ps = conn
					.prepareStatement(
							"SELECT player from " + BBDataBlock.BBDATA_NAME + " where (" + actionString + ") and rbacked = 0 and x < ? and x > ? and y < ? and y > ?  and z < ? and z > ? order by date desc limit 15",
							Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, location.getBlockX() + radius);
			ps.setInt(2, location.getBlockX() - radius);
			ps.setInt(3, location.getBlockY() + radius);
			ps.setInt(4, location.getBlockY() - radius);
			ps.setInt(5, location.getBlockZ() + radius);
			ps.setInt(6, location.getBlockZ() - radius);
			rs = ps.executeQuery();

			if(!rs.next()) {
				for(Player player: players) {
					player.sendMessage(BigBrother.premessage + "No modifications in this area.");
				}
			} else {				
				rs.last();
				int size = rs.getRow();
				rs.first();
				StringBuilder playerList = new StringBuilder();
				while(rs.next()) {
					playerList.append(rs.getString("player"));
					playerList.append(", ");
				}
				playerList.delete(playerList.lastIndexOf(", "), playerList.length());
				for(Player player: players) {
					player.sendMessage(BigBrother.premessage + "(" + size + ") players have modified this area.");
					player.sendMessage(playerList.toString());
				}
			}
		} catch (SQLException ex) {
			BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Find SQL Exception");
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

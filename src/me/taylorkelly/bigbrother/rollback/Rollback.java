package me.taylorkelly.bigbrother.rollback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;

import org.bukkit.*;

public class Rollback {
	Server server;
	ArrayList<Player> recievers;
	ArrayList<String> players;
	boolean rollbackAll;
	long time;
	ArrayList<Integer> blockTypes;
	
	
	private LinkedList<BBDataBlock> listBlocks;
	private static LinkedList<BBDataBlock> lastRollback = new LinkedList<BBDataBlock>();

	public Rollback(Server server) {
	    this.rollbackAll = false;
		this.server = server;
		this.time = 0;
		blockTypes = new ArrayList<Integer>();
		players = new ArrayList<String>();
		recievers = new ArrayList<Player>();
		
		listBlocks = new LinkedList<BBDataBlock>();
	}

	public void addReciever(Player player) {
		recievers.add(player);
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
 
	//TODO Use IN(1,2,3)
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
		
			ps = conn.prepareStatement(RollbackPreparedStatement.create(this));

			set = ps.executeQuery();

			int size = 0;
			while (set.next()) {
				listBlocks.addLast(BBDataBlock.getBBDataBlock(set.getString("player"), set.getInt("action"), set.getInt("world"), set.getInt("x"), set.getInt("y"),
						set.getInt("z"), set.getInt("type"), set.getString("data")));
				size++;
			}
			if (size > 0) {
				for (Player player : recievers) {
					player.sendMessage(BigBrother.premessage + "Rolling back " + size + " edits.");
				}
				try {
					rollbackBlocks();
					ps = conn.prepareStatement(RollbackPreparedStatement.update(this));
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
			builder.append("'s,");
		}
		if (builder.toString().contains(","))
			builder.delete(builder.lastIndexOf(","), builder.length());
		return builder.toString();
	}

    public void rollbackAll() {
        rollbackAll = true;
    }

    public void addPlayers(ArrayList<String> playerList) {
        players.addAll(playerList);
    }

    public void setTime(long l) {
        this.time = l;
    }

    public void addTypes(ArrayList<Integer> blockTypes) {
        this.blockTypes.addAll(blockTypes);
    }
    
    private void rollbackBlocks() {
        lastRollback.clear();
        while (listBlocks.size() > 0) {
            BBDataBlock dataBlock = listBlocks.removeFirst();
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

package me.taylorkelly.bigbrother.rollback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.BBDataBlock.Action;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Rollback {

    public final Server server;
    private final ArrayList<Player> recievers;
    public final ArrayList<String> players;
    public boolean rollbackAll;
    public long time;
    public final ArrayList<Integer> blockTypes;
    public int radius;
    public Location center;
    private final LinkedList<BBDataBlock> listBlocks;
    private static final LinkedList<BBDataBlock> lastRollback = new LinkedList<BBDataBlock>();
    private static String undoRollback = null;
    private final WorldManager manager;
    //private int size; // Number of items to roll back
    private final Plugin plugin;

    public Rollback(Server server, WorldManager manager, Plugin plugin) {
        this.manager = manager;
        this.rollbackAll = false;
        this.server = server;
        this.plugin = plugin;
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
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        Thread rollbacker = new Rollbacker(plugin, server.getScheduler());
        rollbacker.start();
    }

    private String getSimpleString(ArrayList<?> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i).toString());
            if (i + 1 < list.size()) {
                builder.append(", ");
            }
        }
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
        new RollbackByTick(plugin.getServer().getScheduler(), plugin);
    }

    public static boolean canUndo() {
        if (lastRollback != null) {
            return lastRollback.size() > 0;
        } else {
            return false;
        }
    }

    public static int undoSize() {
        if (lastRollback != null) {
            return lastRollback.size();
        } else {
            return 0;
        }
    }

    public static void undo(Server server, Player player) {
        int i = 0;
        while (lastRollback.size() > 0) {
            BBDataBlock dataBlock = lastRollback.removeFirst();
            if (dataBlock != null) {
                dataBlock.redo(server);
                i++;
            }
        }
        if (undoRollback != null) {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = ConnectionManager.getConnection();
                if(conn==null) return;
                ps = conn.prepareStatement(undoRollback);
                ps.execute();
                conn.commit();
                undoRollback = null;
                player.sendMessage(ChatColor.AQUA + "Successfully undid a rollback of " + i + " edits");
            } catch (SQLException ex) {
                BBLogging.severe("Rollback undo SQL Exception", ex);
            } finally {
                ConnectionManager.cleanup( "Rollback undo",  conn, ps, null );
            }
        }
    }

    public void setRadius(int radius, Location center) {
        this.radius = radius;
        this.center = center;
    }

    private class Rollbacker extends Thread {
        // Not used
        //private final Plugin plugin;
        //private final BukkitScheduler scheduler;

        private Rollbacker(Plugin plugin, BukkitScheduler scheduler) {
            //this.plugin = plugin;
            //this.scheduler = scheduler;
        }

        public void run() {
            PreparedStatement ps = null;
            ResultSet set = null;
            Connection conn = null;
            try {
                conn = ConnectionManager.getConnection();
                if(conn==null) return;
                ps = conn.prepareStatement(RollbackPreparedStatement.getInstance().create(Rollback.this, manager));
                set = ps.executeQuery();
                conn.commit();

                int rollbackSize = 0;
                while (set.next()) {
                    listBlocks.addLast(BBDataBlock.getBBDataBlock(set.getInt("player"), Action.values()[set.getInt("action")], set.getString("world"), set.getInt("x"),
                            set.getInt("y"), set.getInt("z"), set.getInt("type"), set.getString("data")));
                    rollbackSize++;
                }
                if (rollbackSize > 0) {
                    for (Player player : recievers) {
                        player.sendMessage(BigBrother.premessage + "Rolling back " + rollbackSize + " edits.");
                        String playersString = (rollbackAll) ? "All Players" : getSimpleString(players);
                        player.sendMessage(ChatColor.BLUE + "Player(s): " + ChatColor.WHITE + playersString);
                        if (blockTypes.size() > 0) {
                            player.sendMessage(ChatColor.BLUE + "Block Type(s): " + ChatColor.WHITE + getSimpleString(blockTypes));
                        }
                        if (time != 0) {
                            Calendar cal = Calendar.getInstance();
                            String DATE_FORMAT = "kk:mm:ss 'on' MMM d";
                            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                            cal.setTimeInMillis(time * 1000);
                            player.sendMessage(ChatColor.BLUE + "Since: " + ChatColor.WHITE + sdf.format(cal.getTime()));
                        }
                        if (radius != 0) {
                            player.sendMessage(ChatColor.BLUE + "Radius: " + ChatColor.WHITE + radius + " blocks");
                        }

                    }
                    try {
                        ps.close();
                        rollbackBlocks();
                        for (Player player : recievers) {
                            player.sendMessage(BigBrother.premessage + "Successfully rollback'd.");
                        }
                        ps = conn.prepareStatement(RollbackPreparedStatement.getInstance().update(Rollback.this, manager));
                        ps.execute();
                        conn.commit();
                        undoRollback = RollbackPreparedStatement.getInstance().undoStatement(Rollback.this, manager);
                    } catch (SQLException ex) {
                        BBLogging.severe("Rollback edit SQL Exception: "+RollbackPreparedStatement.getInstance().update(Rollback.this, manager), ex);
                    }
                } else {
                    for (Player player : recievers) {
                        player.sendMessage(BigBrother.premessage + "Nothing to rollback.");
                    }
                }
            } catch (SQLException ex) {
                BBLogging.severe("Rollback get SQL Exception", ex);
            } finally {
                ConnectionManager.cleanup( "Rollback get",  conn, ps, set );
            }
        }
    }

    private class RollbackByTick implements Runnable {

        private final int id;

        public RollbackByTick(BukkitScheduler scheduler, Plugin plugin) {
            this.id = scheduler.scheduleSyncRepeatingTask(plugin, this, 0, 1);
        }

        @Override
        public void run() {
            int count = 0;

            while (count < BBSettings.rollbacksPerTick && listBlocks.size() > 0) {
                BBDataBlock dataBlock = listBlocks.removeFirst();
                if (dataBlock != null) {
                    lastRollback.addFirst(dataBlock);
                    try {
                        dataBlock.rollback(server.getWorld(dataBlock.world));
                    } catch (Exception e) {
                        BBLogging.warning("Caught exception when rolling back a " + dataBlock.action);
                    }
                    count++;
                }
            }


            if (listBlocks.size() == 0) {
                BBLogging.debug("Finished rollback");

                plugin.getServer().getScheduler().cancelTask(id);
            } else {
                BBLogging.debug("Need to rollback " + listBlocks.size() + " more");

            }
        }
    }
}

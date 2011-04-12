package me.taylorkelly.bigbrother.finder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.BBDataBlock.Action;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Finder {

    private final Location location;
    private int radius;
    private final ArrayList<Player> players;
    private final WorldManager manager;
    private final Plugin plugin;

    public Finder(Location location, List<World> worlds, WorldManager manager, Plugin plugin) {
        this.manager = manager;
        this.location = location;
        this.radius = BBSettings.defaultSearchRadius;
        players = new ArrayList<Player>();
        this.plugin = plugin;
    }

    public void setRadius(double radius) {
        this.radius = (int) radius;
    }

    public void addReciever(Player player) {
        players.add(player);
    }

    public void find() {
        for (Player player : players) {
            player.sendMessage(ChatColor.AQUA + "Searching...");
        }
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new FinderRunner(plugin, location, radius, manager, players));
    }

    public void find(String player) {
        for (Player reciept : players) {
            reciept.sendMessage(ChatColor.AQUA + "Searching...");
        }
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new FinderRunner(plugin, player, location, radius, manager, players));
    }

    public void find(ArrayList<String> players) {
        // TODO find around multiple players
    }

    private class FinderRunner implements Runnable {

        private final Location location;
        private final int radius;
        private final ArrayList<Player> players;
        private final WorldManager manager;
        private final Plugin plugin;
        private final String player;

        public FinderRunner(final Plugin plugin, final String player, final Location location, final int radius, final WorldManager manager, final ArrayList<Player> players) {
            this.player = player;
            this.plugin = plugin;
            this.radius = radius;
            this.location = location;
            this.manager = manager;
            this.players = players;
        }

        public FinderRunner(Plugin plugin, final Location location, final int radius, final WorldManager manager, final ArrayList<Player> players) {
            this(plugin, null, location, radius, manager, players);
        }

        @Override
        public void run() {
            if (player == null) {
                mysqlFind(plugin, location, radius, manager, players);

            } else {
                mysqlFind(plugin, player, location, radius, manager, players);
            }
        }
    }

    private static final void mysqlFind(final Plugin plugin, final Location location, final int radius, final WorldManager manager, final ArrayList<Player> players) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        HashMap<BBPlayerInfo, Integer> modifications = new HashMap<BBPlayerInfo, Integer>();
        try {
            conn = ConnectionManager.getConnection();
            if(conn==null) return;
            // TODO maybe more customizable actions?
            String actionString = "action IN('" + Action.BLOCK_BROKEN.ordinal() + "', '" + Action.BLOCK_PLACED.ordinal() + "', '" + Action.LEAF_DECAY.ordinal() + "', '" + Action.TNT_EXPLOSION.ordinal() + "', '" + Action.CREEPER_EXPLOSION.ordinal() + "', '" + Action.MISC_EXPLOSION.ordinal() + "', '" + Action.LAVA_FLOW.ordinal() + "', '" + Action.BLOCK_BURN.ordinal() + "')";
            
            /*
             * org.h2.jdbc.JdbcSQLException: Column "ID" must be in the GROUP BY
             * list; SQL statement:
             */
            if (BBSettings.usingDBMS(DBMS.H2) || BBSettings.usingDBMS(DBMS.POSTGRES)) {
                ps = conn.prepareStatement("SELECT player, count(player) AS modifications FROM " + BBDataTable.getInstance().getTableName() + " WHERE " + actionString + " AND rbacked = '0' AND x < ? AND x > ? AND y < ? AND y > ? AND z < ? AND z > ? AND world = ? GROUP BY id,player ORDER BY id DESC");
            } else {
                ps = conn.prepareStatement("SELECT player, count(player) AS modifications FROM " + BBDataTable.getInstance().getTableName() + " WHERE " + actionString + " AND rbacked = '0' AND x < ? AND x > ? AND y < ? AND y > ? AND z < ? AND z > ? AND world = ? GROUP BY player ORDER BY id DESC");
            }
            ps.setInt(1, location.getBlockX() + radius);
            ps.setInt(2, location.getBlockX() - radius);
            ps.setInt(3, location.getBlockY() + radius);
            ps.setInt(4, location.getBlockY() - radius);
            ps.setInt(5, location.getBlockZ() + radius);
            ps.setInt(6, location.getBlockZ() - radius);
            ps.setInt(7, manager.getWorld(location.getWorld().getName()));
            rs = ps.executeQuery();
            conn.commit();

            int size = 0;
            while (rs.next()) {
                BBPlayerInfo player = BBUsersTable.getInstance().getUserByID(rs.getInt("player"));
                int mods = rs.getInt("modifications");
                modifications.put(player, mods);
                size++;
            }
            if (size > 0) {
                StringBuilder playerList = new StringBuilder();
                for (Entry<BBPlayerInfo, Integer> entry : modifications.entrySet()) {
                    if(entry.getKey()!=null) {
                        playerList.append(entry.getKey().getName());
                        playerList.append(" (");
                        playerList.append(entry.getValue());
                        playerList.append("), ");
                    }
                }
                if(playerList.indexOf(",")!=-1) {
                    playerList.delete(playerList.lastIndexOf(","), playerList.length());
                }
                //TODO Put into sync'd runnable
                for (Player player : players) {
                    player.sendMessage(BigBrother.premessage + playerList.length() + " player(s) have modified this area:");
                    player.sendMessage(playerList.toString());
                }
            } else {
                for (Player player : players) {
                    player.sendMessage(BigBrother.premessage + "No modifications in this area.");
                }

            }
        } catch (SQLException ex) {
            BBLogging.severe("Find SQL Exception", ex);
        } finally {
            ConnectionManager.cleanup("Find SQL", conn, ps, rs);
        }
    }

    private static final void mysqlFind(final Plugin plugin, final String playerName, final Location location, final int radius, final WorldManager manager, final ArrayList<Player> players) {
        
        BBPlayerInfo hunted = BBUsersTable.getInstance().getUserByName(playerName);
        
        PreparedStatement ps = null;
        ResultSet rs = null;

        HashMap<Integer, Integer> creations = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> destructions = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> explosions = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> burns = new HashMap<Integer, Integer>();

        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            if(conn==null) return;
            // TODO maybe more customizable actions?
            String actionString = "action IN('" + Action.BLOCK_BROKEN.ordinal() + "', '" + Action.BLOCK_PLACED.ordinal() + "', '" + Action.LEAF_DECAY.ordinal() + "', '" + Action.TNT_EXPLOSION.ordinal() + "', '" + Action.CREEPER_EXPLOSION.ordinal() + "', '" + Action.MISC_EXPLOSION.ordinal() + "', '" + Action.LAVA_FLOW.ordinal() + "', '" + Action.BLOCK_BURN.ordinal() + "')";
            ps = conn.prepareStatement("SELECT action, type FROM " + BBDataTable.getInstance().getTableName() + " WHERE " + actionString
                    + " AND rbacked = 0 AND x < ? AND x > ? AND y < ? AND y > ?  AND z < ? AND z > ? AND player = ? AND world = ? order by date desc");

            ps.setInt(1, location.getBlockX() + radius);
            ps.setInt(2, location.getBlockX() - radius);
            ps.setInt(3, location.getBlockY() + radius);
            ps.setInt(4, location.getBlockY() - radius);
            ps.setInt(5, location.getBlockZ() + radius);
            ps.setInt(6, location.getBlockZ() - radius);
            ps.setInt(7, hunted.getID());
            ps.setInt(8, manager.getWorld(location.getWorld().getName()));
            rs = ps.executeQuery();
            conn.commit();

            int size = 0;
            while (rs.next()) {
                Action action = Action.values()[rs.getInt("action")];
                int type = rs.getInt("type");

                switch (action) {
                    case BLOCK_BROKEN:
                    case LEAF_DECAY:
                        if (destructions.containsKey(type)) {
                            destructions.put(type, destructions.get(type) + 1);
                            size++;
                        } else {
                            destructions.put(type, 1);
                            size++;
                        }
                        break;
                    case BLOCK_PLACED:
                        if (creations.containsKey(type)) {
                            creations.put(type, creations.get(type) + 1);
                            size++;
                        } else {
                            creations.put(type, 1);
                            size++;
                        }
                        break;
                    case TNT_EXPLOSION:
                    case CREEPER_EXPLOSION:
                    case MISC_EXPLOSION:
                        if (explosions.containsKey(type)) {
                            explosions.put(type, explosions.get(type) + 1);
                            size++;
                        } else {
                            explosions.put(type, 1);
                            size++;
                        }
                    case BLOCK_BURN:
                        if (burns.containsKey(type)) {
                            burns.put(type, burns.get(type) + 1);
                            size++;
                        } else {
                            burns.put(type, 1);
                            size++;
                        }
                        break;
                    case LAVA_FLOW:
                        if (creations.containsKey(type)) {
                            creations.put(type, creations.get(type) + 1);
                            size++;
                        } else {
                            creations.put(type, 1);
                            size++;
                        }
                        break;
                }

            }
            if (size > 0) {
                StringBuilder creationList = new StringBuilder();
                creationList.append(ChatColor.AQUA.toString());
                creationList.append("Placed Blocks: ");
                creationList.append(ChatColor.WHITE.toString());
                for (Entry<Integer, Integer> entry : creations.entrySet()) {
                    creationList.append(Material.getMaterial(entry.getKey()));
                    creationList.append(" (");
                    creationList.append(entry.getValue());
                    creationList.append("), ");
                }
                if (creationList.toString().contains(",")) {
                    creationList.delete(creationList.lastIndexOf(","), creationList.length());
                }
                StringBuilder brokenList = new StringBuilder();
                brokenList.append(ChatColor.RED.toString());
                brokenList.append("Broken Blocks: ");
                brokenList.append(ChatColor.WHITE.toString());
                for (Entry<Integer, Integer> entry : destructions.entrySet()) {
                    brokenList.append(Material.getMaterial(entry.getKey()));
                    brokenList.append(" (");
                    brokenList.append(entry.getValue());
                    brokenList.append("), ");
                }
                if (brokenList.toString().contains(",")) {
                    brokenList.delete(brokenList.lastIndexOf(","), brokenList.length());
                }
                StringBuilder explodeList = new StringBuilder();
                explodeList.append(ChatColor.RED.toString());
                explodeList.append("Exploded Blocks: ");
                explodeList.append(ChatColor.WHITE.toString());
                for (Entry<Integer, Integer> entry : explosions.entrySet()) {
                    explodeList.append(Material.getMaterial(entry.getKey()));
                    explodeList.append(" (");
                    explodeList.append(entry.getValue());
                    explodeList.append("), ");
                }
                if (explodeList.toString().contains(",")) {
                    explodeList.delete(explodeList.lastIndexOf(","), explodeList.length());
                }

                StringBuilder burnList = new StringBuilder();
                burnList.append(ChatColor.RED.toString());
                burnList.append("Burned Blocks: ");
                burnList.append(ChatColor.WHITE.toString());
                for (Entry<Integer, Integer> entry : burns.entrySet()) {
                    burnList.append(Material.getMaterial(entry.getKey()));
                    burnList.append(" (");
                    burnList.append(entry.getValue());
                    burnList.append("), ");
                }
                if (burnList.toString().contains(",")) {
                    burnList.delete(burnList.lastIndexOf(","), burnList.length());
                }
                for (Player player : players) {
                    player.sendMessage(BigBrother.premessage + playerName + " has made " + size + " modifications");
                    if (creations.entrySet().size() > 0) {
                        player.sendMessage(creationList.toString());
                    }
                    if (destructions.entrySet().size() > 0) {
                        player.sendMessage(brokenList.toString());
                    }
                    if (explosions.entrySet().size() > 0) {
                        player.sendMessage(explodeList.toString());
                    }
                    if (burns.entrySet().size() > 0) {
                        player.sendMessage(burnList.toString());
                    }
                }
            } else {
                for (Player player : players) {
                    player.sendMessage(BigBrother.premessage + playerName + " has no modifications in this area.");
                }

            }
        } catch (SQLException ex) {
            BBLogging.severe("Find SQL Exception", ex);
        } finally {
            ConnectionManager.cleanup("Find SQL", conn, ps, rs);
        }
    }
}

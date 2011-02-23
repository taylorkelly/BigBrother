package me.taylorkelly.bigbrother.finder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import me.taylorkelly.bigbrother.BBDataTable;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.BBDataBlock.Action;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class Finder {

    private Location location;
    private int radius;
    private ArrayList<Player> players;
    //private List<World> worlds; // Not used- N3X
    private WorldManager manager;

    public Finder(Location location, List<World> worlds, WorldManager manager) {
        this.manager = manager;
        this.location = location;
        this.radius = BBSettings.defaultSearchRadius;
        players = new ArrayList<Player>();
        //this.worlds = worlds;
    }

    public void setRadius(double radius) {
        this.radius = (int) radius;
    }

    public void addReciever(Player player) {
        players.add(player);
    }

    public void find() {
        mysqlFind(!BBSettings.mysql);
    }

    public void find(String player) {
        mysqlFind(!BBSettings.mysql, player);
    }

    public void find(ArrayList<String> players) {
        // TODO find around multiple players
    }

    // TODO use IN(1,2,3)
    private void mysqlFind(boolean sqlite) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        HashMap<String, Integer> modifications = new HashMap<String, Integer>();
        try {
            conn = ConnectionManager.getConnection();

            // TODO maybe more customizable actions?
            String actionString = "action IN('" + Action.BLOCK_BROKEN.ordinal() + "', '" + Action.BLOCK_PLACED.ordinal() + "', '" + Action.LEAF_DECAY.ordinal() + "', '" + Action.TNT_EXPLOSION.ordinal() + "', '" + Action.CREEPER_EXPLOSION.ordinal() + "', '" + Action.MISC_EXPLOSION.ordinal() + "', '" + Action.LAVA_FLOW.ordinal() + "', '" + Action.BLOCK_BURN.ordinal() + "')";
            ps = conn.prepareStatement("SELECT player, count(player) AS modifications FROM " + BBDataTable.BBDATA_NAME + " WHERE " + actionString
                    + " AND rbacked = '0' AND x < ? AND x > ? AND y < ? AND y > ? AND z < ? AND z > ? AND world = ? GROUP BY player ORDER BY id DESC");

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
                String player = rs.getString("player");
                int mods = rs.getInt("modifications");
                modifications.put(player, mods);
                size++;
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
            BBLogging.severe("Find SQL Exception", ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BBLogging.severe("Find SQL Exception (on close)");
            }
        }
    }

    private void mysqlFind(boolean sqlite, String playerName) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        HashMap<Integer, Integer> creations = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> destructions = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> explosions = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> burns = new HashMap<Integer, Integer>();

        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();

            // TODO maybe more customizable actions?
            String actionString = "action IN('" + Action.BLOCK_BROKEN.ordinal() + "', '" + Action.BLOCK_PLACED.ordinal() + "', '" + Action.LEAF_DECAY.ordinal() + "', '" + Action.TNT_EXPLOSION.ordinal() + "', '" + Action.CREEPER_EXPLOSION.ordinal() + "', '" + Action.MISC_EXPLOSION.ordinal() + "', '" + Action.LAVA_FLOW.ordinal() + "', '" + Action.BLOCK_BURN.ordinal() + "')";
            ps = conn.prepareStatement("SELECT action, type FROM " + BBDataTable.BBDATA_NAME + " WHERE " + actionString
                    + " AND rbacked = 0 AND x < ? AND x > ? AND y < ? AND y > ?  AND z < ? AND z > ? AND player = ? AND world = ? order by date desc");

            ps.setInt(1, location.getBlockX() + radius);
            ps.setInt(2, location.getBlockX() - radius);
            ps.setInt(3, location.getBlockY() + radius);
            ps.setInt(4, location.getBlockY() - radius);
            ps.setInt(5, location.getBlockZ() + radius);
            ps.setInt(6, location.getBlockZ() - radius);
            ps.setString(7, playerName);
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
                // creationList.append(Color.AQUA);
                creationList.append("Placed Blocks: ");
                // creationList.append(Color.WHITE);
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
                // brokenList.append(Color.RED);
                brokenList.append("Broken Blocks: ");
                // brokenList.append(Color.WHITE);
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
                // brokenList.append(Color.RED);
                explodeList.append("Exploded Blocks: ");
                // brokenList.append(Color.WHITE);
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
                // brokenList.append(Color.RED);
                burnList.append("Burned Blocks: ");
                // brokenList.append(Color.WHITE);
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
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BBLogging.severe("Find SQL Exception (on close)");
            }
        }
    }
}

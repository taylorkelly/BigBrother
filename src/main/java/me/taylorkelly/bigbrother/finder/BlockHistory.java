package me.taylorkelly.bigbrother.finder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.BBDataBlock.Action;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;

import org.bukkit.block.Block;

/**
 * Currently contains only one static method for getting the history of one
 * particular block. Could be expanded to include area histories, etc.
 */
public class BlockHistory {

    /**
     * Returns the list of changes to a given block.
     * Currently use with HistoryLog and HistoryStick, but can be used with other things
     * @param block The block to get the history of
     * @param manager The world manager (so we can get the world index)
     * @return The ArrayList of BBDataBlocks that represent the history
     */
    public static ArrayList<BBDataBlock> hist(Block block, WorldManager manager) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<BBDataBlock> blockList = new ArrayList<BBDataBlock>();

        try {
            conn = ConnectionManager.getConnection();

            // TODO maybe more customizable actions?
            ps = conn.prepareStatement("SELECT  bbdata.id, date, player, action, x, y, z, type, data, rbacked, bbworlds.name AS `world` FROM " + BBDataTable.BBDATA_NAME + " INNER JOIN bbworlds ON bbworlds.id = bbdata.world  WHERE rbacked = 0 AND x = ? AND y = ?  AND z = ? AND bbdata.world = ? ORDER BY bbdata.id ASC;");

            ps.setInt(1, block.getX());
            ps.setInt(2, block.getY());
            ps.setInt(3, block.getZ());
            ps.setInt(4, manager.getWorld(block.getWorld().getName()));
            rs = ps.executeQuery();
            conn.commit();

            while (rs.next()) {
                BBDataBlock newBlock = BBDataBlock.getBBDataBlock(rs.getString("player"), Action.values()[rs.getInt("action")], rs.getString("world"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), rs.getInt("type"), rs.getString("data"));
                newBlock.date = rs.getLong("date");
                blockList.add(newBlock);
            }
        } catch (SQLException ex) {
            BBLogging.severe("Find SQL Exception", ex);
        } finally {
            ConnectionManager.cleanup( "Find",  conn, ps, rs );
        }
        return blockList;
    }
}

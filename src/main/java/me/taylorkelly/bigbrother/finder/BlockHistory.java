package me.taylorkelly.bigbrother.finder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockHistory {

    public static ArrayList<BBDataBlock> hist(Block block) {
        PreparedStatement ps = null;
        ResultSet rs = null;    
        Connection conn = null;
        ArrayList<BBDataBlock> blockList = new ArrayList<BBDataBlock>();

        try {
            conn = ConnectionManager.getConnection();

            // TODO maybe more customizable actions?
            ps = conn.prepareStatement("SELECT * FROM " + BBDataBlock.BBDATA_NAME + " WHERE rbacked = 0 AND x = ? AND y = ?  AND z = ? ORDER BY id ASC");

            ps.setInt(1, block.getX());
            ps.setInt(2, block.getY());
            ps.setInt(3, block.getZ());
            rs = ps.executeQuery();
            conn.commit();

            while (rs.next()) {
                BBDataBlock newBlock = BBDataBlock.getBBDataBlock(rs.getString("player"), rs.getInt("action"), rs.getInt("world"), rs.getInt("x"), rs.getInt("y"),  rs.getInt("z"), rs.getInt("type"),  rs.getString("data"));
                newBlock.date = rs.getLong("date");
                blockList.add(newBlock);
            }
        } catch (SQLException ex) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Find SQL Exception", ex);
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
        return blockList;
    }

}

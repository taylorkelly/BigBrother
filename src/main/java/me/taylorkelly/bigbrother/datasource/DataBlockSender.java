package me.taylorkelly.bigbrother.datasource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import me.taylorkelly.bigbrother.BBDataTable;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Cleanser;
import me.taylorkelly.bigbrother.Stats;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.BBDataBlock.Action;

public class DataBlockSender {

    public static final LinkedBlockingQueue<BBDataBlock> SENDING = new LinkedBlockingQueue<BBDataBlock>();

    public static void disable(BigBrother bb) {
        bb.getServer().getScheduler().cancelTasks(bb);
    }

    public static void initialize(BigBrother bb, File dataFolder, WorldManager manager) {
        int result = bb.getServer().getScheduler().scheduleAsyncRepeatingTask(bb, new SendingTask(dataFolder, manager), BBSettings.sendDelay * 30, BBSettings.sendDelay * 30);
        if (result < 0) {
            BBLogging.severe("Unable to schedule sending of blocks");
        }
    }

    public static void offer(BBDataBlock dataBlock) {
        SENDING.add(dataBlock);
    }

    private static boolean sendBlocksMySQL(Collection<BBDataBlock> collection, WorldManager manager) {
        //SQLite fix...
        if (!BBSettings.mysql) {
            for (BBDataBlock block : collection) {
                manager.getWorld(block.world);
            }
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            if (conn == null) {
                return false;
            }
            ps = conn.prepareStatement("INSERT " + BBSettings.getMySQLIgnore() + " INTO " + BBDataTable.BBDATA_NAME
                    + " (date, player, action, world, x, y, z, type, data, rbacked) VALUES (?,?,?,?,?,?,?,?,?,0)");
            for (BBDataBlock block : collection) {
                ps.setLong(1, block.date);
                ps.setString(2, block.player);
                ps.setInt(3, block.action.ordinal());
                ps.setInt(4, manager.getWorld(block.world));
                ps.setInt(5, block.x);
                if (block.y < 0) {
                    block.y = 0;
                }
                if (block.y > 127) {
                    block.y = 127;
                }
                ps.setInt(6, block.y);
                ps.setInt(7, block.z);
                ps.setInt(8, block.type);
                if (block.data.length() > 150) {
                    ps.setString(9, block.data.substring(0, 150));
                } else {
                    ps.setString(9, block.data);
                }
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            return true;
        } catch (SQLException ex) {
            BBLogging.severe("Data Insert SQL Exception when sending blocks", ex);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BBLogging.severe("Data Insert SQL Exception when sending blocks (on close)", ex);
            }
        }
    }

    private static void sendBlocksFlatFile(File dataFolder, Collection<BBDataBlock> collection) {
        File dir = new File(dataFolder, "logs");
        if (!dir.exists()) {
            dir.mkdir();
        }
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            for (BBDataBlock block : collection) {
                File file = new File(dir, fixName(block.player) + ".log");
                StringBuilder builder = new StringBuilder(Long.toString(System.currentTimeMillis()));
                builder.append(" - ");
                builder.append(getAction(block.action));
                builder.append(" ");
                builder.append(block.world);
                builder.append("@(");
                builder.append(block.x);
                builder.append(",");
                builder.append(block.y);
                builder.append(",");
                builder.append(block.z);
                builder.append(") info: ");
                builder.append(block.type);
                builder.append(", ");
                builder.append(block.data);

                fwriter = new FileWriter(file, true);
                bwriter = new BufferedWriter(fwriter);
                bwriter.write(builder.toString());
                bwriter.newLine();
                bwriter.flush();
                bwriter.close();
                fwriter.close();
            }
        } catch (IOException e) {
            BBLogging.severe("Data Insert IO Exception", e);
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.close();
                }
                if (fwriter != null) {
                    fwriter.close();
                }
            } catch (IOException e) {
                BBLogging.severe("Data Insert IO Exception (on close)", e);
            }
        }
    }

    public static String getAction(Action action) {
        switch (action) {
            case BLOCK_BROKEN:
                return "broke block";
            case BLOCK_PLACED:
                return "placed block";
            case DESTROY_SIGN_TEXT:
                return "destroyed sign text";
            case TELEPORT:
                return "teleport";
            case DELTA_CHEST:
                return "changed chest";
            case COMMAND:
                return "command";
            case CHAT:
                return "chat";
            case DISCONNECT:
                return "disconnect";
            case LOGIN:
                return "login";
            case DOOR_OPEN:
                return "door";
            case BUTTON_PRESS:
                return "button";
            case LEVER_SWITCH:
                return "lever";
            case CREATE_SIGN_TEXT:
                return "created sign text";
            case LEAF_DECAY:
                return "decayed leafe";
            case FLINT_AND_STEEL:
                return "flint'd";
            case TNT_EXPLOSION:
                return "TNT-exploded";
            case CREEPER_EXPLOSION:
                return "Creeper-exploded";
            case MISC_EXPLOSION:
                return "Misc-exploded";
            case OPEN_CHEST:
                return "opened chest";
            case BLOCK_BURN:
                return "burned block";
            case LAVA_FLOW:
                return "flowed lava";
            default:
                return action.name();
        }
    }

    public static String fixName(String player) {
        return player.replace(".", "").replace(":", "").replace("<", "").replace(">", "").replace("*", "").replace("\\", "").replace("/", "").replace("?", "").replace("\"", "").replace("|", "");
    }

    private static class SendingTask implements Runnable {

        private File dataFolder;
        private WorldManager manager;

        public SendingTask(File dataFolder, WorldManager manager) {
            this.dataFolder = dataFolder;
            this.manager = manager;
        }

        @Override
        public void run() {
            if (SENDING.size() == 0) {
                return;
            }
            Collection<BBDataBlock> collection = new ArrayList<BBDataBlock>();
            SENDING.drainTo(collection);

            boolean worked = sendBlocksMySQL(collection, manager);
            if (BBSettings.flatLog) {
                sendBlocksFlatFile(dataFolder, collection);
            }

            if (!worked) {
                SENDING.addAll(collection);
                BBLogging.warning("SQL send failed. Keeping data for later send.");
            } else {
                Stats.logBlocks(collection.size());
            }
        }
    }
}

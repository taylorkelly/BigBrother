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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Stats;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;

public class DataBlockSender {
    public static LinkedBlockingQueue<BBDataBlock> sending = new LinkedBlockingQueue<BBDataBlock>();
    private static Timer sendTimer;

    public static void disable() {
        if (sendTimer != null)
            sendTimer.cancel();
    }

    public static void initialize(File dataFolder) {
        sendTimer = new Timer();
        sendTimer.schedule(new SendingTask(dataFolder), BBSettings.sendDelay * 1000L, BBSettings.sendDelay * 1000L);
    }

    public static void offer(BBDataBlock dataBlock) {
        sending.add(dataBlock);
    }

    private static void sendBlocks(File dataFolder) {
        if (sending.size() == 0)
            return;

        Collection<BBDataBlock> collection = new ArrayList<BBDataBlock>();
        sending.drainTo(collection);

        boolean worked = sendBlocksMySQL(collection);
        if (BBSettings.flatLog) {
            sendBlocksFlatFile(dataFolder, collection);
        }

        if (!worked) {
            sending.addAll(collection);
            BigBrother.log.log(Level.INFO, "[BBROTHER]: SQL send failed. Keeping data for later send.");
        } else {
            Stats.logBlocks(collection.size());
        }
    }

    private static boolean sendBlocksMySQL(Collection<BBDataBlock> collection) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("INSERT INTO " + BBDataBlock.BBDATA_NAME
                    + " (date, player, action, world, x, y, z, type, data, rbacked) VALUES (?,?,?,?,?,?,?,?,?,0)");
            for (BBDataBlock block : collection) {
                ps.setLong(1, block.date);
                ps.setString(2, block.player);
                ps.setInt(3, block.action);
                ps.setInt(4, block.world);
                ps.setInt(5, block.x);
                if (block.y < 0)
                    block.y = 0;
                if (block.y > 127)
                    block.y = 127;
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
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert SQL Exception", ex);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert SQL Exception (on close)");
            }
        }
    }

    private static void sendBlocksFlatFile(File dataFolder, Collection<BBDataBlock> collection) {
        File dir = new File(dataFolder, "logs");
        if (!dir.exists())
            dir.mkdir();
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            for (BBDataBlock block : collection) {
                File file = new File(dir, fixName(block.player) + ".log");
                StringBuilder builder = new StringBuilder(System.currentTimeMillis() + "");
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
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert IO Exception", e);
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert IO Exception (on close)", e);
            }
        }
    }

    public static String getAction(int action) {
        switch (action) {
        case (BBDataBlock.BLOCK_BROKEN):
            return "broke block";
        case (BBDataBlock.BLOCK_PLACED):
            return "placed block";
        case (BBDataBlock.DESTROY_SIGN_TEXT):
            return "destroyed sign text";
        case (BBDataBlock.TELEPORT):
            return "teleport";
        case (BBDataBlock.DELTA_CHEST):
            return "changed chest";
        case (BBDataBlock.COMMAND):
            return "command";
        case (BBDataBlock.CHAT):
            return "chat";
        case (BBDataBlock.DISCONNECT):
            return "disconnect";
        case (BBDataBlock.LOGIN):
            return "login";
        case (BBDataBlock.DOOR_OPEN):
            return "door";
        case (BBDataBlock.BUTTON_PRESS):
            return "button";
        case (BBDataBlock.LEVER_SWITCH):
            return "lever";
        case (BBDataBlock.CREATE_SIGN_TEXT):
            return "created sign text";
        case (BBDataBlock.LEAF_DECAY):
            return "decayed leafe";
        case (BBDataBlock.FLINT_AND_STEEL):
            return "flint'd";
        case (BBDataBlock.TNT_EXPLOSION):
            return "TNT-exploded";
        case (BBDataBlock.CREEPER_EXPLOSION):
            return "Creeper-exploded";
        case (BBDataBlock.MISC_EXPLOSION):
            return "Misc-exploded";
        case (BBDataBlock.OPEN_CHEST):
            return "opened chest";
        case (BBDataBlock.BLOCK_BURN):
            return "burned block";
        default:
            return "" + action;
        }
    }

    public static String fixName(String player) {
        return player.replace(".", "").replace(":", "").replace("<", "").replace(">", "").replace("*", "").replace("\\", "").replace("/", "").replace("?", "")
                .replace("\"", "").replace("|", "");
    }

    private static class SendingTask extends TimerTask {
        private File dataFolder;

        public SendingTask(File dataFolder) {
            this.dataFolder = dataFolder;
        }

        public void run() {
            sendBlocks(dataFolder);
        }
    }
}

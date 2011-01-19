package datasource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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
import me.taylorkelly.bigbrother.datablock.BBDataBlock;

public class DataBlockSender {
    public static LinkedBlockingQueue<BBDataBlock> sending = new LinkedBlockingQueue<BBDataBlock>();
    private static Timer sendTimer;

    public static void disable() {
        if(sendTimer != null) sendTimer.cancel();
    }

    public static void initialize(File dataFolder) {
        sendTimer = new Timer();
        sendTimer.schedule(new SendingTask(dataFolder), BBSettings.sendDelay * 1000, BBSettings.sendDelay * 1000);
    }

    public static void offer(BBDataBlock dataBlock) {
        sending.add(dataBlock);
    }

    private static void sendBlocks(File dataFolder) {
        if (sending.size() == 0)
            return;

        Collection<BBDataBlock> collection = new ArrayList<BBDataBlock>();
        sending.drainTo(collection);

        boolean worked = sendBlocksMySQL(!BBSettings.mysql, collection);
        if(BBSettings.flatLog) {
            sendBlocksFlatFile(dataFolder, collection);
        }
        
        if(!worked) {
            sending.addAll(collection);
            BigBrother.log.log(Level.INFO, "[BBROTHER]: SQL send failed. Keeping data for later send.");

        }
    }

    private static boolean sendBlocksMySQL(boolean sqlite, Collection<BBDataBlock> collection) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (sqlite) {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(BBSettings.liteDb);
            } else {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(BBSettings.mysqlDB, BBSettings.mysqlUser, BBSettings.mysqlPass);
            }
            ps = conn.prepareStatement("INSERT INTO " + BBDataBlock.BBDATA_NAME
                    + " (date, player, action, world, x, y, z, type, data, rbacked) VALUES (?,?,?,?,?,?,?,?,?,0)");
            for (BBDataBlock block : collection) {
                ps.setLong(1, block.date);
                ps.setString(2, block.player);
                ps.setInt(3, block.action);
                ps.setInt(4, block.world);
                ps.setInt(5, block.x);
                ps.setInt(6, block.y);
                ps.setInt(7, block.z);
                ps.setInt(8, block.type);
                ps.setString(9, block.data);
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException ex) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert SQL Exception", ex);
            return false;
        } catch (ClassNotFoundException e) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: Data Insert SQL Exception (cnf)" + ((sqlite) ? " using sqlite" : " using mysql"));
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

    private static String getAction(int action) {
        switch (action) {
        case (BBDataBlock.BLOCK_BROKEN):
            return "brokeBlock";
        case (BBDataBlock.BLOCK_PLACED):
            return "placedBlock";
        case (BBDataBlock.DESTROY_SIGN_TEXT):
            return "destroySignText";
        case (BBDataBlock.TELEPORT):
            return "teleport";
        case (BBDataBlock.DELTA_CHEST):
            return "deltaChest";
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
            return "createSignText";
        default:
            return "";
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

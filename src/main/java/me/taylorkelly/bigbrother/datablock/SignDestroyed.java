package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Restore sign blocks correctly.
 * @author Rob
 *
 */
public class SignDestroyed extends BBDataBlock {

    public SignDestroyed(String player, int type, byte data, Sign sign, String world) {
        super(player, Action.SIGN_DESTROYED, world, sign.getX(), sign.getY(), sign.getZ(), type, Byte.toString(data)+"\u0060"+getText(sign));
    }

    public SignDestroyed(String player, String[] lines, Block block) {
        super(player, Action.SIGN_DESTROYED, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), 323, getText(lines));
    }

    private static String getText(Sign sign) {
        String[] lines = sign.getLines();
        return getText(lines);
    }

    private static String getText(String[] lines) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            message.append(lines[i]);
            if (i < lines.length - 1) {
                message.append("\u0060");
            }
        }
        return message.toString();
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new SignDestroyed(pi, world, x, y, z, type, data);
    }

    private SignDestroyed(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.SIGN_DESTROYED, world, x, y, z, type, data);
    }


    @Override
    public void rollback(World wld) {
        World currWorld = wld;//server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        String[] lines = data.split("\u0060");


        Block block = currWorld.getBlockAt(x, y, z);
        block.setTypeId(type);
        block.setData(Byte.valueOf(lines[0]));
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 1; i < lines.length+1; i++) {
                sign.setLine(i, lines[i]);
            }
        } else {
            BBLogging.warning("Error when restoring sign");
        }
    }

    @Override
    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        currWorld.getBlockAt(x, y, z).setTypeIdAndData(0, (byte) 0, true);
    }
}

package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class PlacedBlock extends BBDataBlock {

    private ArrayList<BBDataBlock> bystanders;

    public PlacedBlock(String player, Block block, String world) {
        super(player, Action.BLOCK_PLACED, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBDataBlock>();
        // TODO snow check once it gets fixed
        // TODO Water/Lava Check
    }

    public PlacedBlock(String player, String world, int x, int y, int z, int type, byte data) {
        super(player, Action.BLOCK_PLACED, world, x, y, z, type, Byte.toString(data));
        bystanders = new ArrayList<BBDataBlock>();

    }

    public void send() {
        for (BBDataBlock block : bystanders) {
            block.send();
        }
        super.send();
    }

    private PlacedBlock(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.BLOCK_PLACED, world, x, y, z, type, data);
    }

    public void rollback(World wld) {
        World currWorld = wld;//server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        currWorld.getBlockAt(x, y, z).setTypeId(0);
    }

    public void redo(Server server) {
        if (type != 51 || BBSettings.restoreFire) {
            World currWorld = server.getWorld(world);
            if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
                currWorld.loadChunk(x >> 4, z >> 4);
            }

            byte blockData = Byte.parseByte(data);
            currWorld.getBlockAt(x, y, z).setTypeId(type);
            currWorld.getBlockAt(x, y, z).setData(blockData);
        }
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new PlacedBlock(pi, world, x, y, z, type, data);
    }
}

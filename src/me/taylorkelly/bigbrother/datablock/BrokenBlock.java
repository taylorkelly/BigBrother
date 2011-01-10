package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import org.bukkit.*;
import org.bukkit.block.Sign;

public class BrokenBlock extends BBDataBlock {
    private ArrayList<BBDataBlock> bystanders;

    public BrokenBlock(Player player, Block block) {
        // TODO Better World support
        super(player.getName(), BLOCK_BROKEN, 0, block.getX(), block.getY(), block.getZ(), block.getTypeID(), block.getData() + "");
        bystanders = new ArrayList<BBDataBlock>();
        torchCheck(player, block);
        //other torch checks
        signCheck(player, block);
        // TODO chest check
    }

    public void send() {
        for(BBDataBlock block: bystanders) {
            block.send();
        }
        super.send();
    }

    public void rollback(Server server) {
        // TODO Chunk loading stuffs
        // if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(),
        // destination.getBlockZ())))
        // world.loadChunk(world.getChunkAt(destination.getBlockX(),
        // destination.getBlockZ()));

        byte blockData = Byte.parseByte(data);
        server.getWorlds()[world].getBlockAt(x, y, z).setTypeID(type);
        server.getWorlds()[world].getBlockAt(x, y, z).setData(blockData);
    }

    public void redo(Server server) {
        // TODO Chunk loading stuffs
        // if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(),
        // destination.getBlockZ())))
        // world.loadChunk(world.getChunkAt(destination.getBlockX(),
        // destination.getBlockZ()));

        server.getWorlds()[world].getBlockAt(x, y, z).setTypeID(0);
    }

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return new BrokenBlock(player, world, x, y, z, type, data);
    }

    private BrokenBlock(String player, int world, int x, int y, int z, int type, String data) {
        super(player, BLOCK_BROKEN, world, x, y, z, type, data);
    }

    private void torchCheck(Player player, Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);
        if (torchTop.getTypeID() == 50 && torchTop.getData() == 5) {
            bystanders.add(new BrokenBlock(player, torchTop));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchNorth.getTypeID() == 50 && torchNorth.getData() == 1) {
            bystanders.add(new BrokenBlock(player, torchNorth));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchSouth.getTypeID() == 50 && torchSouth.getData() == 2) {
            bystanders.add(new BrokenBlock(player, torchSouth));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchEast.getTypeID() == 50 && torchEast.getData() == 3) {
            bystanders.add(new BrokenBlock(player, torchEast));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchWest.getTypeID() == 50 && torchWest.getData() == 4) {
            bystanders.add(new BrokenBlock(player, torchWest));
        }
    }
    
    private void signCheck(Player player, Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new DestroySignText(player, sign));
        }
    }

}

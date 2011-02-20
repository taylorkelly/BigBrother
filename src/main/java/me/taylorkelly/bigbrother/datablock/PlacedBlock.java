package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBSettings;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class PlacedBlock extends BBDataBlock {
    private ArrayList<BBDataBlock> bystanders;

    public PlacedBlock(Player player, Block block, int world) {
        super(player.getName(), Action.BLOCK_PLACED, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBDataBlock>();
        signCheck(player, block);
        // TODO snow check once it gets fixed
    }

    public PlacedBlock(Player player, int x, int y, int z, int type, int data) {
        super(player.getName(), Action.BLOCK_PLACED, 0, x, y, z, type, data + "");
        bystanders = new ArrayList<BBDataBlock>();
    }

	@Override
    public void send() {
        for (BBDataBlock block : bystanders) {
            block.send();
        }
        super.send();
    }

    private PlacedBlock(String player, int world, int x, int y, int z, int type, String data) {
        super(player, Action.BLOCK_PLACED, world, x, y, z, type, data);
    }

    private void signCheck(Player player, Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new CreateSignText(player, sign, world));
        }
    }

    public void rollback(Server server) {
        World worldy = server.getWorlds().get(world);
        if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        worldy.getBlockAt(x, y, z).setTypeId(0);
    }

    public void redo(Server server) {
        if (type != 51 || BBSettings.restoreFire) {
            World worldy = server.getWorlds().get(world);
            if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
                ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
            }

            byte blockData = Byte.parseByte(data);
            worldy.getBlockAt(x, y, z).setTypeId(type);
            worldy.getBlockAt(x, y, z).setData(blockData);
        }
    }

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return new PlacedBlock(player, world, x, y, z, type, data);
    }

}

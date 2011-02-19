package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FlintAndSteel extends BBDataBlock {

    public FlintAndSteel(Player player, Block block) {
        // TODO Better World support
        super(player.getName(), Action.FLINT_AND_STEEL, 0, block.getX(), block.getY(), block.getZ(), block.getTypeId(), "");
    }

    private FlintAndSteel(String player, int world, int x, int y, int z, int type, String data) {
        super(player, Action.FLINT_AND_STEEL, world, x, y, z, type, data);
    }

    public void rollback(Server server) {
    }

    public void redo(Server server) {
    }

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return new FlintAndSteel(player, world, x, y, z, type, data);
    }

}

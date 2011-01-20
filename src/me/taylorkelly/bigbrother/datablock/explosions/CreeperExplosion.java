package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;

public class CreeperExplosion extends Explosion {

    public CreeperExplosion(Player player, Block block) {
        super(player.getName(), CREEPER_EXPLOSION, 0, block.getX(), block.getY(), block.getZ(), block.getTypeId(), block.getData() + "");
    }
    
    public CreeperExplosion(Block block) {
        super(ENVIRONMENT, CREEPER_EXPLOSION, 0, block.getX(), block.getY(), block.getZ(), block.getTypeId(), block.getData() + "");
    }

    public static BBDataBlock create(Location location, List<Block> blockList) {
        for()
    }
}

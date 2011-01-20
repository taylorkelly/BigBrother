package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;

public class MiscExplosion extends Explosion {

    public MiscExplosion(String player, Block block) {
        super(MISC_EXPLOSION, player, block);
    }
    
    public MiscExplosion(Block block) {
        super(MISC_EXPLOSION, ENVIRONMENT, block);
    }

    protected Explosion newInstance(String player, Block block) {
        return new MiscExplosion(player, block);
    }
    
    public static void create(Location location, List<Block> blockList) {
        for(Block block: blockList) {
            BBDataBlock dataBlock = new MiscExplosion(ENVIRONMENT, block);
            dataBlock.send();
        }
    }
    
    private MiscExplosion(String player, int world, int x, int y, int z, int type, String data) {
        super(player, MISC_EXPLOSION, world, x, y, z, type, data);
    }

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return new MiscExplosion(player, world, x, y, z, type, data);
    }

}

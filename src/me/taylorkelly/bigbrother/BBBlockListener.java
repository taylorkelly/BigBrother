package me.taylorkelly.bigbrother;

import me.taylorkelly.bigbrother.datablock.*;

import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.*;

public class BBBlockListener extends BlockListener {
    private BigBrother plugin;

    public BBBlockListener(BigBrother plugin) {
        this.plugin = plugin;
    }

    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getDamageLevel() == BlockDamageLevel.BROKEN && !event.isCancelled()) {
            Player player = event.getPlayer();
            if (BBSettings.blockBreak && plugin.watching(player)) {
                Block block = event.getBlock();
                BrokenBlock dataBlock = new BrokenBlock(player, block);
                dataBlock.send();
            }
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (BBSettings.blockPlace && plugin.watching(player)) {
            Block block = event.getBlockPlaced();
            PlacedBlock dataBlock = new PlacedBlock(player, block);
            dataBlock.send();
        }
    }
    
    public void onBlockInteract(BlockInteractEvent event) {
        Block block = event.getBlock();
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player) {
            Player player = (Player)entity;
        switch(block.getType()) {
        case WOODEN_DOOR:
            DoorOpen doorDataBlock = new DoorOpen(player.getName(), block);
            doorDataBlock.send();
        case LEVER:
            LeverSwitch leverDataBlock = new LeverSwitch(player.getName(), block);
            leverDataBlock.send();
        case STONE_BUTTON:
            ButtonPress buttonDataBlock = new ButtonPress(player.getName(), block);
            buttonDataBlock.send();
        }
        }
    }
}

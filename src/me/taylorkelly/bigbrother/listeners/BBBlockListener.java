package me.taylorkelly.bigbrother.listeners;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.*;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;

import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.*;

public class BBBlockListener extends BlockListener {
    private BigBrother plugin;

    public BBBlockListener(BigBrother plugin) {
        this.plugin = plugin;
    }

    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getDamageLevel() == BlockDamageLevel.STARTED && !event.isCancelled()) {
            if (event.getBlock().getType() == Material.TNT) {
                TNTLogger.log(event.getPlayer(), event.getBlock());
            }
        }
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
        if (BBSettings.blockPlace && plugin.watching(player) && !event.isCancelled()) {
            Block block = event.getBlockPlaced();
            PlacedBlock dataBlock = new PlacedBlock(player, block);
            dataBlock.send();
        }
    }

    public void onBlockInteract(BlockInteractEvent event) {
        Block block = event.getBlock();
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (plugin.watching(player) && !event.isCancelled()) {
                if (!(BBPermissions.isAdmin(player) && plugin.hasStick(player) && player.getItemInHand().getType() == Material.STICK)) {
                    switch (block.getType()) {
                    case WOODEN_DOOR:
                        if (BBSettings.doorOpen) {
                            DoorOpen doorDataBlock = new DoorOpen(player.getName(), block);
                            doorDataBlock.send();
                        }
                        break;
                    case LEVER:
                        if (BBSettings.leverSwitch) {
                            LeverSwitch leverDataBlock = new LeverSwitch(player.getName(), block);
                            leverDataBlock.send();
                        }
                        break;
                    case STONE_BUTTON:
                        if (BBSettings.buttonPress) {
                            ButtonPress buttonDataBlock = new ButtonPress(player.getName(), block);
                            buttonDataBlock.send();
                        }
                        break;
                    case CHEST:
                        if (BBSettings.chestChanges) {
                            BBDataBlock chestDataBlock = new ChestOpen(player, block);
                            chestDataBlock.send();
                        }
                        break;
                    }
                } else {
                    event.setCancelled(true);
                }
            }

        }
    }

    public void onLeavesDecay(LeavesDecayEvent event) {
        if (BBSettings.leafDrops && !event.isCancelled()) {
            // TODO try to find a player that did it.
            BBDataBlock dataBlock = LeafDecay.create(event.getBlock());
            dataBlock.send();
        }
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
        if (BBSettings.fire && event.getCause() == IgniteCause.FLINT_AND_STEEL && !event.isCancelled()) {
            BBDataBlock dataBlock = new FlintAndSteel(event.getPlayer(), event.getBlock());
            dataBlock.send();
        }
    }

    public void onBlockRightClick(BlockRightClickEvent event) {
        Player player = event.getPlayer();
        if (BBPermissions.isAdmin(player) && plugin.hasStick(player) && event.getItemInHand().getType() == Material.STICK) {
            plugin.stick(player, event.getBlock());
        }
    }
}

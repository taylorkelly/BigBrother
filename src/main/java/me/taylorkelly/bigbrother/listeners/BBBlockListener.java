package me.taylorkelly.bigbrother.listeners;

import java.util.List;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.BlockBurn;
import me.taylorkelly.bigbrother.datablock.BrokenBlock;
import me.taylorkelly.bigbrother.datablock.ButtonPress;
import me.taylorkelly.bigbrother.datablock.ChestOpen;
import me.taylorkelly.bigbrother.datablock.DoorOpen;
import me.taylorkelly.bigbrother.datablock.FlintAndSteel;
import me.taylorkelly.bigbrother.datablock.LeafDecay;
import me.taylorkelly.bigbrother.datablock.LeverSwitch;
import me.taylorkelly.bigbrother.datablock.PlacedBlock;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class BBBlockListener extends BlockListener {
    private BigBrother plugin;
    private List<World> worlds;

    public BBBlockListener(BigBrother plugin) {
        this.plugin = plugin;
        this.worlds = plugin.getServer().getWorlds();
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
                BrokenBlock dataBlock = new BrokenBlock(player.getName(), block, block.getWorld().getName());
                dataBlock.send();
            }
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (BBSettings.blockPlace && plugin.watching(player) && !event.isCancelled()) {
            Block block = event.getBlockPlaced();
            PlacedBlock dataBlock = new PlacedBlock(player.getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    public void onBlockInteract(BlockInteractEvent event) {
        Block block = event.getBlock();
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (plugin.watching(player) && !event.isCancelled()) {
                switch (block.getType()) {
                case WOODEN_DOOR:
                    if (BBSettings.doorOpen) {
                        DoorOpen doorDataBlock = new DoorOpen(player.getName(), block, block.getWorld().getName());
                        doorDataBlock.send();
                    }
                    break;
                case LEVER:
                    if (BBSettings.leverSwitch) {
                        LeverSwitch leverDataBlock = new LeverSwitch(player.getName(), block, block.getWorld().getName());
                        leverDataBlock.send();
                    }
                    break;
                case STONE_BUTTON:
                    if (BBSettings.buttonPress) {
                        ButtonPress buttonDataBlock = new ButtonPress(player.getName(), block, block.getWorld().getName());
                        buttonDataBlock.send();
                    }
                    break;
                case CHEST:
                    if (BBSettings.chestChanges) {
                        BBDataBlock chestDataBlock = new ChestOpen(player.getName(), block, block.getWorld().getName());
                        chestDataBlock.send();
                    }
                    break;
                }
            }
        }
    }

    public void onLeavesDecay(LeavesDecayEvent event) {
        if (BBSettings.leafDrops && !event.isCancelled()) {
            // TODO try to find a player that did it.
            final Block block = event.getBlock();
            BBDataBlock dataBlock = LeafDecay.create(block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
        if (BBSettings.fire && event.getCause() == IgniteCause.FLINT_AND_STEEL && !event.isCancelled()) {
            final Block block = event.getBlock();
            BBDataBlock dataBlock = new FlintAndSteel(event.getPlayer().getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    public void onBlockBurn(BlockBurnEvent event) {
        if (BBSettings.fire && !event.isCancelled()) {
            final Block block = event.getBlock();
            BBDataBlock dataBlock = BlockBurn.create(block, block.getWorld().getName());
            dataBlock.send();
        }
    }
}

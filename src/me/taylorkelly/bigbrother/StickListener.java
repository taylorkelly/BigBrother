package me.taylorkelly.bigbrother;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.ButtonPress;
import me.taylorkelly.bigbrother.datablock.ChestOpen;
import me.taylorkelly.bigbrother.datablock.DoorOpen;
import me.taylorkelly.bigbrother.datablock.LeverSwitch;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;

public class StickListener extends BlockListener {

    private BigBrother plugin;

    public StickListener(BigBrother plugin) {
        this.plugin = plugin;
    }

    public void onBlockRightClick(BlockRightClickEvent event) {
        Player player = event.getPlayer();
        if (BBPermissions.info(player) && plugin.hasStick(player, player.getItemInHand())) {
            plugin.stick(player, event.getBlock());
        }
    }

    public void onBlockInteract(BlockInteractEvent event) {
        ArrayList<Material> nonInteracts = new ArrayList<Material>();
        nonInteracts.add(Material.WOOD_PLATE);
        nonInteracts.add(Material.STONE_PLATE);
        Block block = event.getBlock();
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!nonInteracts.contains(block)) {
                if (BBPermissions.info(player) && plugin.hasStick(player, player.getItemInHand())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (BBPermissions.info(player) && plugin.hasStick(player, event.getItemInHand())) {
            plugin.stick(player, event.getBlockPlaced());
            event.setCancelled(true);
        }
    }

}

package me.taylorkelly.bigbrother;

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
        if (BBPermissions.isAdmin(player) && plugin.hasStick(player) && event.getItemInHand().getType() == Material.STICK) {
            plugin.stick(player, event.getBlock());
        }
    }

    public void onBlockInteract(BlockInteractEvent event) {
        Block block = event.getBlock();
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (BBPermissions.isAdmin(player) && plugin.hasStick(player) && player.getItemInHand().getType() == Material.STICK) {
                System.out.println("sticking");
                event.setCancelled(true);
            }
        }
    }
    
    public void onBlockPlace(BlockPlaceEvent event) {
        System.out.println("WOAH! " + event.getItemInHand().getType());
        Player player = event.getPlayer();
        if (BBPermissions.isAdmin(player) && plugin.hasStick(player) && event.getItemInHand().getType() == Material.LOG) {
            System.out.println("sticking");
            plugin.stick(player, event.getBlockPlaced());
            event.setCancelled(true);
        }
    }

}

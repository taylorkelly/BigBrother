package me.taylorkelly.bigbrother;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class StickListener extends BlockListener {

    private BigBrother plugin;

    public StickListener(BigBrother plugin) {
        this.plugin = plugin;
    }

    /*
    These are all Player events now.
    public void onBlockRightClick(BlockRightClickEvent event) {
        Player player = event.getPlayer();
        if (BBPermissions.info(player) && plugin.hasStick(player, player.getItemInHand()) && plugin.rightClickStick(player)) {
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
            if (!nonInteracts.contains(block.getType())) {
                if (BBPermissions.info(player) && plugin.hasStick(player, player.getItemInHand()) && plugin.rightClickStick(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    */

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (BBPermissions.info(player) && plugin.hasStick(player, event.getItemInHand())) {
            plugin.stick(player, event.getBlockPlaced());
            event.setCancelled(true);
        }
    }

}

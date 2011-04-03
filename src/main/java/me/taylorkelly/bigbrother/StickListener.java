package me.taylorkelly.bigbrother;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class StickListener extends BlockListener {

    private BigBrother plugin;

    public StickListener(BigBrother plugin) {
        this.plugin = plugin;
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (BBPermissions.info(player) && plugin.hasStick(player, event.getItemInHand())) {
            plugin.stick(player, event.getBlockPlaced(),false);
            event.setCancelled(true);
        }
    }

}

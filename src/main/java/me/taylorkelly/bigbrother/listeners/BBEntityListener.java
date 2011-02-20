package me.taylorkelly.bigbrother.listeners;

import java.util.List;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.explosions.CreeperExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.MiscExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;

import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class BBEntityListener extends EntityListener {
    private BigBrother plugin;
    private List<World> worlds;

    public BBEntityListener(BigBrother bigBrother) {
        this.plugin = bigBrother;
        this.worlds = plugin.getServer().getWorlds();
    }

    public void onEntityExplode(EntityExplodeEvent event) {
        // Err... why is this null when it's a TNT?
        // Need a fix to get location.
        if (event.getEntity() == null) {
            if (BBSettings.tntExplosions) {
                TNTLogger.createTNTDataBlock(event.blockList(), worlds.indexOf(event.getLocation().getWorld()));
            }
        } else if (event.getEntity() instanceof CraftLivingEntity) {
            if (BBSettings.creeperExplosions) {
                CreeperExplosion.create(event.getEntity().getLocation(), event.blockList(), worlds.indexOf(event.getLocation().getWorld()));
            }
        } else if (BBSettings.miscExplosions) {
            MiscExplosion.create(event.getEntity().getLocation(), event.blockList(), worlds.indexOf(event.getLocation().getWorld()));
        }

    }
}

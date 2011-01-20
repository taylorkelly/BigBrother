package me.taylorkelly.bigbrother.listeners;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.explosions.CreeperExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.MiscExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;

import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class BBEntityListener extends EntityListener {
    private BigBrother plugin;

    public BBEntityListener(BigBrother bigBrother) {
        this.plugin = bigBrother;
    }

    public void onEntityExplode(EntityExplodeEvent event) {
        // Err... why is this null when it's a TNT?
        // Need a fix to get location.
        if (!event.isCancelled() && BBSettings.blockBreak) {
            if (event.getEntity() == null) {
                System.out.println("TNT exploded");
                BBDataBlock dataBlock = TNTLogger.createTNTDataBlock(event.blockList());
                dataBlock.send();
            } else if (event.getEntity() instanceof CraftLivingEntity) {
                System.out.println("Creeper exploded");
                BBDataBlock dataBlock = CreeperExplosion.create(event.getEntity().getLocation(), event.blockList());
                dataBlock.send();
            } else {
                System.out.println("Misc. explosion");
                BBDataBlock dataBlock = MiscExplosion.create(event.getEntity().getLocation(), event.blockList());
                dataBlock.send();
            }
        }
    }
}

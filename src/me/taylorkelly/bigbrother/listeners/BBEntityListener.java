package me.taylorkelly.bigbrother.listeners;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BrokenBlock;

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
        if (event.getEntity() == null) {
            System.out.println("TNT exploded");
            // Must be a creeper...
        } else if (event.getEntity() instanceof CraftLivingEntity) {
            System.out.println("Creeper exploded");

            //Other shit?
        } else {
            System.out.println("Misc. explosion");

        }

        /*
         * if (event.getDamageLevel() == BlockDamageLevel.BROKEN &&
         * !event.isCancelled()) { Player player = event.getPlayer(); if
         * (BBSettings.blockBreak && plugin.watching(player)) { Block block =
         * event.getBlock(); BrokenBlock dataBlock = new BrokenBlock(player,
         * block); dataBlock.send(); } }
         */
    }
}

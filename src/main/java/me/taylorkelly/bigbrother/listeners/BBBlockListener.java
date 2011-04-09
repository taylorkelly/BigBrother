package me.taylorkelly.bigbrother.listeners;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.BlockBurnLogger;
import me.taylorkelly.bigbrother.LavaFlowLogger;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.BrokenBlock;
import me.taylorkelly.bigbrother.datablock.CreateSignText;
import me.taylorkelly.bigbrother.datablock.FlintAndSteel;
import me.taylorkelly.bigbrother.datablock.LavaFlow;
import me.taylorkelly.bigbrother.datablock.LeafDecay;
import me.taylorkelly.bigbrother.datablock.PlacedBlock;
import me.taylorkelly.bigbrother.datablock.SignDestroyed;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;

public class BBBlockListener extends BlockListener {

    private BigBrother plugin;

    public BBBlockListener(BigBrother plugin) {
        this.plugin=plugin;
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
        BBLogging.debug("onBlockDamage");
        if (event.getBlock().getType() == Material.TNT) {
            TNTLogger.log(event.getPlayer().getName(), event.getBlock());
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            BBLogging.debug("onBlockBreak");
            Player player = event.getPlayer();
            BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            plugin.closeChestIfOpen(pi);
            if (BBSettings.blockBreak && pi.getWatched()) {
                Block block = event.getBlock();
                BrokenBlock dataBlock = new BrokenBlock(player.getName(), block, block.getWorld().getName());
                dataBlock.send();
            }
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (BBSettings.blockPlace && pi.getWatched() && !event.isCancelled()) {
            BBLogging.debug("onBlockPlace");
            Block block = event.getBlockPlaced();
            if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
                LavaFlowLogger.log(block, player.getName());
            }
            PlacedBlock dataBlock = new PlacedBlock(player.getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    /*
    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        Block block = event.getBlock();
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (pi.getWatched() && !event.isCancelled()) {
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
    */
    
    @Override
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (BBSettings.leafDrops && !event.isCancelled()) {
            // TODO try to find a player that did it.
            final Block block = event.getBlock();
            BBDataBlock dataBlock = LeafDecay.create(block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (BBSettings.fire && event.getCause() == IgniteCause.FLINT_AND_STEEL && !event.isCancelled()) {
            final Block block = event.getBlock();
            BBDataBlock dataBlock = new FlintAndSteel(event.getPlayer().getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    @Override
    public void onBlockBurn(BlockBurnEvent event) {
        if (BBSettings.fire && !event.isCancelled()) {
            final Block block = event.getBlock();
            BBDataBlock dataBlock = BlockBurnLogger.create(block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    @Override
    public void onBlockFromTo(BlockFromToEvent event) {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        boolean lava = blockFrom.getType() == Material.LAVA || blockFrom.getType() == Material.STATIONARY_LAVA;
        if (!event.isCancelled() && lava && BBSettings.lavaFlow) {
            LavaFlow dataBlock = LavaFlowLogger.getFlow(blockFrom, blockTo);
            dataBlock.send();
        }
    }

    @Override
    public void onSignChange(SignChangeEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getBlock().getState();
            boolean oldText = false;
            for (String line : sign.getLines()) {
                if (!line.equals("")) {
                    oldText = true;
                }
            }
            if (oldText) {
                SignDestroyed dataBlock = new SignDestroyed(event.getPlayer().getName(), event.getBlock().getTypeId(), event.getBlock().getData(),(Sign) event.getBlock().getState(), event.getBlock().getWorld().getName());
                dataBlock.send();
            }
        }
        if (!event.isCancelled() && BBSettings.blockPlace) {
            CreateSignText dataBlock = new CreateSignText(event.getPlayer().getName(), event.getLines(), event.getBlock());
            dataBlock.send();
        }
    }
}

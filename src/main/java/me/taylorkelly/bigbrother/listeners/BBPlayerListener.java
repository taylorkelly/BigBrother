package me.taylorkelly.bigbrother.listeners;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.LavaFlowLogger;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.BrokenBlock;
import me.taylorkelly.bigbrother.datablock.ButtonPress;
import me.taylorkelly.bigbrother.datablock.Chat;
import me.taylorkelly.bigbrother.datablock.ChestOpen;
import me.taylorkelly.bigbrother.datablock.Command;
import me.taylorkelly.bigbrother.datablock.Disconnect;
import me.taylorkelly.bigbrother.datablock.DoorOpen;
import me.taylorkelly.bigbrother.datablock.DropItem;
import me.taylorkelly.bigbrother.datablock.LeverSwitch;
import me.taylorkelly.bigbrother.datablock.Login;
import me.taylorkelly.bigbrother.datablock.PickupItem;
import me.taylorkelly.bigbrother.datablock.PlacedBlock;
import me.taylorkelly.bigbrother.datablock.Teleport;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BBPlayerListener extends PlayerListener {
    
    private BigBrother plugin;
    
    public BBPlayerListener(BigBrother plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        //plugin.processPsuedotick();
        Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (BBSettings.commands && pi.getWatched()) {
            Command dataBlock = new Command(player, event.getMessage(), player.getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        //plugin.processPsuedotick();
        final Player player = event.getPlayer();
        
        BBUsersTable.getInstance().addOrUpdateUser(player);
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        
        /*
        if (!plugin.haveSeen(player)) {
            plugin.markSeen(player);
            if (BBSettings.autoWatch) {
                plugin.watchPlayer(player);
            }
        }
        */
        if (BBSettings.login && pi.getWatched()) {
            Login dataBlock = new Login(player, player.getWorld().getName());
            dataBlock.send();
        }
        
        BBLogging.debug(player.getName() + " has Permissions: ");
        BBLogging.debug("- Watching privileges: " + BBPermissions.watch(player));
        BBLogging.debug("- Info privileges: " + BBPermissions.info(player));
        BBLogging.debug("- Rollback privileges: " + BBPermissions.rollback(player));
        BBLogging.debug("- Cleansing privileges: " + BBPermissions.cleanse(player));
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        //plugin.processPsuedotick();
        final Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (BBSettings.disconnect && pi.getWatched()) {
            Disconnect dataBlock = new Disconnect(player.getName(), player.getLocation(), player.getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        //plugin.processPsuedotick();
        Location from = event.getFrom();
        Location to = event.getTo();
        
        final Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (BBSettings.teleport && pi.getWatched() && distance(from, to) > 5 && !event.isCancelled()) {
            Teleport dataBlock = new Teleport(player.getName(), event.getTo());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        //plugin.processPsuedotick();
        final Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (BBSettings.chat && pi.getWatched()) {
            Chat dataBlock = new Chat(player, event.getMessage(), player.getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (BBSettings.pickupItem && pi.getWatched()) {
            PickupItem dataBlock = new PickupItem(player.getName(), event.getItem(), event.getItem().getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (BBSettings.dropItem && pi.getWatched()) {
            DropItem dataBlock = new DropItem(player.getName(), event.getItemDrop(), event.getItemDrop().getWorld().getName());
            dataBlock.send();
        }
    }
    
    /*
     * 
    public void onBlockRightClick(BlockRightClickEvent event) {
        Player player = event.getPlayer();
        if (BBPermissions.info(player) && plugin.hasStick(player, player.getItemInHand()) && plugin.rightClickStick(player)) {
            plugin.stick(player, event.getBlock());
        }
    }
    public void onBlockInteract(BlockInteractEvent event) {
    }
    */
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        //plugin.processPsuedotick();
        if(event.isCancelled()) return;
        
        Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        
        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (BBPermissions.info(player) && plugin.hasStick(player, player.getItemInHand()) && plugin.leftClickStick(player)) {
                // Process left-clicks (punch action on log, etc)
                plugin.stick(player, event.getClickedBlock(),true);
                
                event.setCancelled(true); // Cancel in case of 1-hit breakable stuff like flowers.
            }
        }
        
        // Process right-clicking stuff.
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            // Process stick/log events first.
            if (BBPermissions.info(player) && plugin.hasStick(player, player.getItemInHand()) && plugin.rightClickStick(player)) {
                // Get info
                plugin.stick(player, event.getClickedBlock(),false);
                
                // Cancel any interactions.
                ArrayList<Material> nonInteracts = new ArrayList<Material>();
                nonInteracts.add(Material.WOOD_PLATE);
                nonInteracts.add(Material.STONE_PLATE);
                if (!nonInteracts.contains(event.getClickedBlock().getType())) {
                    event.setCancelled(true);
                }
                // Otherwise...
            } else if (BBSettings.blockPlace && pi.getWatched()) {
                int x;
                int y;
                int z;
                int type;
                PlacedBlock dataBlock;
                World world;
                Block block = event.getClickedBlock();
                
                plugin.closeChestIfOpen(pi);
                if(block.getState() instanceof Chest) {
                    Chest chest = ((Chest)block.getState());
                    // OH SHI-
                    BBUsersTable.getInstance().userOpenedChest(player.getName(),chest,chest.getInventory().getContents());
                    return;
                }
                switch (event.getMaterial()) {
                //TODO Door logging
                case LAVA_BUCKET:
                    x = event.getClickedBlock().getX() + event.getBlockFace().getModX();
                    y = event.getClickedBlock().getY() + event.getBlockFace().getModY();
                    z = event.getClickedBlock().getZ() + event.getBlockFace().getModZ();
                    type = Material.LAVA.getId();
                    world = event.getClickedBlock().getWorld();
                    dataBlock = new PlacedBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) 0);
                    LavaFlowLogger.log(new Location(world, x, y, z), event.getPlayer().getName());
                    dataBlock.send();
                    break;
                case WATER_BUCKET:
                    x = event.getClickedBlock().getX() + event.getBlockFace().getModX();
                    y = event.getClickedBlock().getY() + event.getBlockFace().getModY();
                    z = event.getClickedBlock().getZ() + event.getBlockFace().getModZ();
                    type = Material.WATER.getId();
                    world = event.getClickedBlock().getWorld();
                    dataBlock = new PlacedBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) 0);
                    dataBlock.send();
                    break;
                case SIGN:
                    x = event.getClickedBlock().getX() + event.getBlockFace().getModX();
                    y = event.getClickedBlock().getY() + event.getBlockFace().getModY();
                    z = event.getClickedBlock().getZ() + event.getBlockFace().getModZ();
                    world = event.getClickedBlock().getWorld();
                    
                    int data = 0;
                    switch (event.getBlockFace()) {
                    case UP:
                        type = Material.SIGN_POST.getId();
                        break;
                    case NORTH:
                        data = 4;
                        type = Material.WALL_SIGN.getId();
                        break;
                    case SOUTH:
                        data = 5;
                        type = Material.WALL_SIGN.getId();
                        break;
                    case EAST:
                        data = 2;
                        type = Material.WALL_SIGN.getId();
                        break;
                    case WEST:
                        data = 3;
                        type = Material.WALL_SIGN.getId();
                        break;
                    default:
                        type = Material.SIGN.getId();
                    }
                    dataBlock = new PlacedBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) data);
                    dataBlock.send();
                    break;
                case BUCKET:
                    BrokenBlock dataBlock2;
                    world = event.getClickedBlock().getWorld();
                    switch (event.getClickedBlock().getType()) {
                    case STATIONARY_LAVA:
                    case LAVA:
                        x = event.getClickedBlock().getX();
                        y = event.getClickedBlock().getY();
                        z = event.getClickedBlock().getZ();
                        type = Material.LAVA.getId();
                        dataBlock2 = new BrokenBlock(BBUsersTable.getInstance().getUserByName(event.getPlayer().getName()), world.getName(), x, y, z, type, (byte) 0);
                        dataBlock2.send();
                        break;
                    case STATIONARY_WATER:
                    case WATER:
                        x = event.getClickedBlock().getX();
                        y = event.getClickedBlock().getY();
                        z = event.getClickedBlock().getZ();
                        type = Material.WATER.getId();
                        dataBlock2 = new BrokenBlock(BBUsersTable.getInstance().getUserByName(event.getPlayer().getName()), world.getName(), x, y, z, type, (byte) 0);
                        dataBlock2.send();
                    }
                    break;
                default:
                    
                    switch (event.getClickedBlock().getType()) {
                    case WOODEN_DOOR:
                        //case IRON_DOOR:
                        if (BBSettings.doorOpen) {
                            DoorOpen doorDataBlock = new DoorOpen(event.getPlayer().getName(), block, block.getWorld().getName());
                            doorDataBlock.send();
                        }
                        break;
                    case LEVER:
                        if (BBSettings.leverSwitch) {
                            LeverSwitch leverDataBlock = new LeverSwitch(event.getPlayer().getName(), block, block.getWorld().getName());
                            leverDataBlock.send();
                        }
                        break;
                    case STONE_BUTTON:
                        if (BBSettings.buttonPress) {
                            ButtonPress buttonDataBlock = new ButtonPress(event.getPlayer().getName(), block, block.getWorld().getName());
                            buttonDataBlock.send();
                        }
                        break;
                    case CHEST:
                        if (BBSettings.chestChanges) {
                            BBDataBlock chestDataBlock = new ChestOpen(event.getPlayer().getName(), block, block.getWorld().getName());
                            chestDataBlock.send();
                        }
                        break;
                    }
                    break;
                }
            }
        }
    }

    private double distance(Location from, Location to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }
}

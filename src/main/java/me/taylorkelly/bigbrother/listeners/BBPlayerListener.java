package me.taylorkelly.bigbrother.listeners;

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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class BBPlayerListener extends PlayerListener {
    
    private BigBrother plugin;
    
    public BBPlayerListener(BigBrother plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerCommandPreprocess(PlayerChatEvent event) {
        //plugin.processPsuedotick();
        Player player = event.getPlayer();
        if (BBSettings.commands && plugin.watching(player)) {
            Command dataBlock = new Command(player, event.getMessage(), player.getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerJoin(PlayerEvent event) {
        //plugin.processPsuedotick();
        final Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUser(player.getName());
        
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
    public void onPlayerQuit(PlayerEvent event) {
        //plugin.processPsuedotick();
        final Player player = event.getPlayer();
        if (BBSettings.disconnect && plugin.watching(player)) {
            Disconnect dataBlock = new Disconnect(player.getName(), player.getLocation(), player.getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerTeleport(PlayerMoveEvent event) {
        //plugin.processPsuedotick();
        Location from = event.getFrom();
        Location to = event.getTo();
        
        final Player player = event.getPlayer();
        if (BBSettings.teleport && plugin.watching(player) && distance(from, to) > 5 && !event.isCancelled()) {
            Teleport dataBlock = new Teleport(player.getName(), event.getTo());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        //plugin.processPsuedotick();
        final Player player = event.getPlayer();
        if (BBSettings.chat && plugin.watching(player)) {
            Chat dataBlock = new Chat(player, event.getMessage(), player.getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        if (BBSettings.pickupItem && plugin.watching(player)) {
            PickupItem dataBlock = new PickupItem(player.getName(), event.getItem(), event.getItem().getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (BBSettings.dropItem && plugin.watching(player)) {
            DropItem dataBlock = new DropItem(player.getName(), event.getItemDrop(), event.getItemDrop().getWorld().getName());
            dataBlock.send();
        }
    }
    
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        //plugin.processPsuedotick();
        if (BBSettings.blockPlace && plugin.watching(event.getPlayer()) && !event.isCancelled()) {
            int x;
            int y;
            int z;
            int type;
            PlacedBlock dataBlock;
            World world;
            Block block = event.getClickedBlock();
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
                    dataBlock2 = new BrokenBlock(BBUsersTable.getInstance().getUser(event.getPlayer().getName()), world.getName(), x, y, z, type, (byte) 0);
                    dataBlock2.send();
                    break;
                case STATIONARY_WATER:
                case WATER:
                    x = event.getClickedBlock().getX();
                    y = event.getClickedBlock().getY();
                    z = event.getClickedBlock().getZ();
                    type = Material.WATER.getId();
                    dataBlock2 = new BrokenBlock(BBUsersTable.getInstance().getUser(event.getPlayer().getName()), world.getName(), x, y, z, type, (byte) 0);
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
    
    
    private double distance(Location from, Location to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }
}

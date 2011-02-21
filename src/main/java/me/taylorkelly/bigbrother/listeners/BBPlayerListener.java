package me.taylorkelly.bigbrother.listeners;

import java.util.List;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BrokenBlock;
import me.taylorkelly.bigbrother.datablock.Chat;
import me.taylorkelly.bigbrother.datablock.Command;
import me.taylorkelly.bigbrother.datablock.Disconnect;
import me.taylorkelly.bigbrother.datablock.Login;
import me.taylorkelly.bigbrother.datablock.PlacedBlock;
import me.taylorkelly.bigbrother.datablock.Teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BBPlayerListener extends PlayerListener {

    private BigBrother plugin;
    private List<World> worlds;

    public BBPlayerListener(BigBrother plugin) {
        this.plugin = plugin;
        this.worlds = plugin.getServer().getWorlds();
    }

    public void onPlayerCommand(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (BBSettings.commands && plugin.watching(player)) {
            Command dataBlock = new Command(player, event.getMessage(), player.getWorld().getName());
            dataBlock.send();
        }
    }

    public void onPlayerJoin(PlayerEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.haveSeen(player)) {
            plugin.markSeen(player);
            if (BBSettings.autoWatch) {
                plugin.watchPlayer(player);
            }
        }

        if (BBSettings.login && plugin.watching(player)) {
            Login dataBlock = new Login(player, player.getWorld().getName());
            dataBlock.send();
        }
    }

    public void onPlayerQuit(PlayerEvent event) {
        final Player player = event.getPlayer();
        if (BBSettings.disconnect && plugin.watching(player)) {
            Disconnect dataBlock = new Disconnect(player.getName(), player.getLocation(), player.getWorld().getName());
            dataBlock.send();
        }
    }

    public void onPlayerTeleport(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        final Player player = event.getPlayer();
        if (BBSettings.teleport && plugin.watching(player) && distance(from, to) > 5 && !event.isCancelled()) {
            Teleport dataBlock = new Teleport(player.getName(), event.getTo());
            dataBlock.send();
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (BBSettings.chat && plugin.watching(player)) {
            Chat dataBlock = new Chat(player, event.getMessage(), player.getWorld().getName());
            dataBlock.send();
        }
    }

    public void onPlayerItem(PlayerItemEvent event) {
        if (BBSettings.blockPlace && plugin.watching(event.getPlayer()) && !event.isCancelled()) {
            int x;
            int y;
            int z;
            int type;
            PlacedBlock dataBlock;
            World world;
            switch (event.getMaterial()) {
                //TODO Door logging
                case LAVA_BUCKET:
                    x = event.getBlockClicked().getX() + event.getBlockFace().getModX();
                    y = event.getBlockClicked().getY() + event.getBlockFace().getModY();
                    z = event.getBlockClicked().getZ() + event.getBlockFace().getModZ();
                    type = Material.LAVA.getId();
                    world = event.getBlockClicked().getWorld();
                    dataBlock = new PlacedBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) 0);
                    dataBlock.send();
                    break;
                case WATER_BUCKET:
                    x = event.getBlockClicked().getX() + event.getBlockFace().getModX();
                    y = event.getBlockClicked().getY() + event.getBlockFace().getModY();
                    z = event.getBlockClicked().getZ() + event.getBlockFace().getModZ();
                    type = Material.WATER.getId();
                    world = event.getBlockClicked().getWorld();
                    dataBlock = new PlacedBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) 0);
                    dataBlock.send();
                    break;
                case SIGN:
                    x = event.getBlockClicked().getX() + event.getBlockFace().getModX();
                    y = event.getBlockClicked().getY() + event.getBlockFace().getModY();
                    z = event.getBlockClicked().getZ() + event.getBlockFace().getModZ();
                    world = event.getBlockClicked().getWorld();

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
                    world = event.getBlockClicked().getWorld();
                    switch (event.getBlockClicked().getType()) {
                        case STATIONARY_LAVA:
                        case LAVA:
                            x = event.getBlockClicked().getX();
                            y = event.getBlockClicked().getY();
                            z = event.getBlockClicked().getZ();
                            type = Material.LAVA.getId();
                            dataBlock2 = new BrokenBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) 0);
                            dataBlock2.send();
                            break;
                        case STATIONARY_WATER:
                        case WATER:
                            x = event.getBlockClicked().getX();
                            y = event.getBlockClicked().getY();
                            z = event.getBlockClicked().getZ();
                            type = Material.WATER.getId();
                            dataBlock2 = new BrokenBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) 0);
                            dataBlock2.send();
                    }
                    break;
            }
        }
    }

    private double distance(Location from, Location to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }
}

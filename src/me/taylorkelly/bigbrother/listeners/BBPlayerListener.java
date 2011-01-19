package me.taylorkelly.bigbrother.listeners;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

public class BBPlayerListener extends PlayerListener {
    private BigBrother plugin;

    public BBPlayerListener(BigBrother plugin) {
        this.plugin = plugin;
    }

    public void onPlayerCommand(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (BBSettings.commands && plugin.watching(player)) {
            Command dataBlock = new Command(player, event.getMessage());
            dataBlock.send();
        }
    }

    public void onPlayerJoin(PlayerEvent event) {
        if (!plugin.haveSeen(event.getPlayer())) {
            plugin.markSeen(event.getPlayer());
            if (BBSettings.autoWatch) {
                plugin.watchPlayer(event.getPlayer());
            }
        }

        if (BBSettings.login && plugin.watching(event.getPlayer())) {
            Login dataBlock = new Login(event.getPlayer());
            dataBlock.send();
        }
    }

    public void onPlayerQuit(PlayerEvent event) {
        if (BBSettings.disconnect && plugin.watching(event.getPlayer())) {
            Disconnect dataBlock = new Disconnect(event.getPlayer());
            dataBlock.send();
        }
    }

    public void onPlayerTeleport(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (BBSettings.teleport && plugin.watching(event.getPlayer()) && distance(from, to) > 5) {
            Teleport dataBlock = new Teleport(event.getPlayer(), event.getTo());
            dataBlock.send();
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        if (BBSettings.chat && plugin.watching(event.getPlayer())) {
            Chat dataBlock = new Chat(event.getPlayer(), event.getMessage());
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
            switch (event.getMaterial()) {
            case LAVA_BUCKET:
                x = event.getBlockClicked().getX() + event.getBlockFace().getModX();
                y = event.getBlockClicked().getY() + event.getBlockFace().getModY();
                z = event.getBlockClicked().getZ() + event.getBlockFace().getModZ();
                type = Material.LAVA.getId();
                dataBlock = new PlacedBlock(event.getPlayer(), x, y, z, type, 0);
                dataBlock.send();
                break;
            case WATER_BUCKET:
                x = event.getBlockClicked().getX() + event.getBlockFace().getModX();
                y = event.getBlockClicked().getY() + event.getBlockFace().getModY();
                z = event.getBlockClicked().getZ() + event.getBlockFace().getModZ();
                type = Material.WATER.getId();
                dataBlock = new PlacedBlock(event.getPlayer(), x, y, z, type, 0);
                dataBlock.send();
                break;
            case BUCKET:
                BrokenBlock dataBlock2;

                switch (event.getBlockClicked().getType()) {
                case STATIONARY_LAVA:
                case LAVA:
                    x = event.getBlockClicked().getX();
                    y = event.getBlockClicked().getY();
                    z = event.getBlockClicked().getZ();
                    type = Material.LAVA.getId();
                    dataBlock2 = new BrokenBlock(event.getPlayer(), x, y, z, type, 0);
                    dataBlock2.send();
                    break;
                case STATIONARY_WATER:
                case WATER:
                    x = event.getBlockClicked().getX();
                    y = event.getBlockClicked().getY();
                    z = event.getBlockClicked().getZ();
                    type = Material.WATER.getId();
                    dataBlock2 = new BrokenBlock(event.getPlayer(), x, y, z, type, 0);
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

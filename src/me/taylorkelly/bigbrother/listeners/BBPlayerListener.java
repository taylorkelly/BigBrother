package me.taylorkelly.bigbrother.listeners;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.*;
import net.minecraft.server.MinecraftServer;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
        String[] split = event.getMessage().split(" ");
        MinecraftServer server = ((CraftPlayer) player).getHandle().b;

        //TODO Take out!
        if (BBPermissions.rollback(player)) {
            if (split[0].equalsIgnoreCase("/ban-ip")) {
                if (split.length < 2) {
                    player.sendMessage(ChatColor.RED + "Correct usage is: /ban-ip [player] <reason> (optional) NOTE: this permabans IPs.");
                    return;
                }

                Player player2 = plugin.getServer().getPlayer(split[1]);

                if (player2 != null) {
                    String address = player2.getAddress().getAddress().getHostAddress();
                    server.f.c(address);
                    player.sendMessage(ChatColor.RED + "Banning " + player2.getName() + " @ " + address);
                    Logger.getLogger("Minecraft").log(Level.INFO, "IP Banning " + player2.getName() + " (IP: " + address + ")");

                    if (split.length > 2) {
                        player2.kickPlayer(combine(2, split, " "));
                    } else {
                        player2.kickPlayer("Fuck you. -Love SSMP staff");
                    }
                    plugin.getServer().broadcastMessage(ChatColor.RED + player2.getName() + " was IP banned. Enjoy the rest of your day");  

                } else {
                    player.sendMessage(ChatColor.RED + "Can't find user " + split[1] + ".");
                }
            } else if (split[0].equalsIgnoreCase("/ban")) {
                if (split.length < 2) {
                    player.sendMessage(ChatColor.RED + "Correct usage is: /ban [player] <reason> (optional)");
                    return;
                }

                Player player2 = plugin.getServer().getPlayer(split[1]);

                if (player2 != null) {
                    server.f.a(player2.getName());

                    if (split.length > 2) {
                        player2.kickPlayer(combine(2, split, " "));
                    } else {
                        player2.kickPlayer("Fuck you. -Love SSMP staff");
                    }
                    plugin.getServer().broadcastMessage(ChatColor.RED + player2.getName() + " was banned. Enjoy the rest of your day");  
                    Logger.getLogger("Minecraft").log(Level.INFO, "Banning " + player2.getName());
                    player.sendMessage(ChatColor.RED + "Banning " + player2.getName());
                } else {
                    server.f.a(split[1]);
                    Logger.getLogger("Minecraft").log(Level.INFO, "Banning " + split[1]);
                    player.sendMessage(ChatColor.RED + "Banning " + split[2]);
                }
            } else if (split[0].equalsIgnoreCase("/unban")) {
                if (split.length != 2) {
                    player.sendMessage(ChatColor.RED + "Correct usage is: /unban [player]");
                    return;
                }
                server.f.b(split[1]);
                player.sendMessage(ChatColor.RED + "Unbanned " + split[1]);
            } else if (split[0].equalsIgnoreCase("/unban-ip")) {
                if (split.length != 2) {
                    player.sendMessage(ChatColor.RED + "Correct usage is: /unban-ip [ip]");
                    return;
                }
                server.f.d(split[1]);
                player.sendMessage(ChatColor.RED + "Unbanned " + split[1]);
            } else if (split[0].equalsIgnoreCase("/kick")) {
                if (split.length < 2) {
                    player.sendMessage(ChatColor.RED + "Correct usage is: /kick [player] <reason> (optional)");
                    return;
                }

                Player player2 = plugin.getServer().getPlayer(split[1]);

                if (player2 != null) {

                    if (split.length > 2) {
                        player2.kickPlayer(combine(2, split, " "));
                        plugin.getServer().broadcastMessage(ChatColor.RED + player2.getName() + " was kicked for " + combine(2, split, " "));  
                    } else {
                        player2.kickPlayer("Quit testing me bitch! -SSMP Staff");
                        plugin.getServer().broadcastMessage(ChatColor.RED + player2.getName() + " was kicked for being a little bitch");  
                    }
                    Logger.getLogger("Minecraft").log(Level.INFO, "Kicking " + player2.getName());
                    player.sendMessage(ChatColor.RED + "Kicking " + player2.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "Can't find user " + split[1] + ".");
                }
            }
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

        if (BBSettings.teleport && plugin.watching(event.getPlayer()) && distance(from, to) > 5 && !event.isCancelled()) {
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
            case SIGN:
                x = event.getBlockClicked().getX() + event.getBlockFace().getModX();
                y = event.getBlockClicked().getY() + event.getBlockFace().getModY();
                z = event.getBlockClicked().getZ() + event.getBlockFace().getModZ();
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
                dataBlock = new PlacedBlock(event.getPlayer(), x, y, z, type, data);
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

    private static String combine(int start, String[] split, String spacing) {
        String name = "";
        for (int i = 2; i < split.length; i++) {
            name += split[i];
            if (i + 1 < split.length)
                name += spacing;
        }
        return name;
    }

    private double distance(Location from, Location to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }
}

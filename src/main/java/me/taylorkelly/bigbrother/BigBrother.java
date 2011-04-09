/*
 * BigBrother (http://github.com/tkelly910/BigBrother)
 * Copyright (C) 2010 Taylor Kelly (tkelly), OniTux, N3X15
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.taylorkelly.bigbrother;

import java.io.File;
import java.sql.Connection;

import me.taylorkelly.bigbrother.commands.CleanseCommand;
import me.taylorkelly.bigbrother.commands.ConfirmCommand;
import me.taylorkelly.bigbrother.commands.DeleteCommand;
import me.taylorkelly.bigbrother.commands.DoneCommand;
import me.taylorkelly.bigbrother.commands.FindCommand;
import me.taylorkelly.bigbrother.commands.HelpCommand;
import me.taylorkelly.bigbrother.commands.HereCommand;
import me.taylorkelly.bigbrother.commands.LogCommand;
import me.taylorkelly.bigbrother.commands.RollbackCommand;
import me.taylorkelly.bigbrother.commands.StickCommand;
import me.taylorkelly.bigbrother.commands.UndoCommand;
import me.taylorkelly.bigbrother.commands.UnwatchedCommand;
import me.taylorkelly.bigbrother.commands.UpdateCommand;
import me.taylorkelly.bigbrother.commands.VersionCommand;
import me.taylorkelly.bigbrother.commands.WatchCommand;
import me.taylorkelly.bigbrother.commands.WatchedCommand;
import me.taylorkelly.bigbrother.datablock.DeltaChest;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.bigbrother.datasource.DataBlockSender;
import me.taylorkelly.bigbrother.finder.Sticker;
import me.taylorkelly.bigbrother.fixes.Fix;
import me.taylorkelly.bigbrother.fixes.Fix1;
import me.taylorkelly.bigbrother.fixes.Fix2;
import me.taylorkelly.bigbrother.fixes.Fix3;
import me.taylorkelly.bigbrother.fixes.Fix4;
import me.taylorkelly.bigbrother.fixes.Fix5;
import me.taylorkelly.bigbrother.griefcraft.util.Updater;
import me.taylorkelly.bigbrother.listeners.BBBlockListener;
import me.taylorkelly.bigbrother.listeners.BBEntityListener;
import me.taylorkelly.bigbrother.listeners.BBPlayerListener;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BigBrother extends JavaPlugin {

    private BBPlayerListener playerListener;
    private BBBlockListener blockListener;
    private BBEntityListener entityListener;
    private StickListener stickListener;
    private Watcher watcher;
    public Sticker sticker;
    public WorldManager worldManager;
    public static String name;
    public static String version;
    public final static String premessage = ChatColor.AQUA + "[BBROTHER]: " + ChatColor.WHITE;
    private Updater updater;

    @Override
    public void onLoad() {
        // Don't need.
    }
    
    @Override
    public void onDisable() {
        DataBlockSender.disable(this);
    }

    @Override
    public void onEnable() {
        BBLogging.debug("Debug Mode enabled");
        
        // Stuff that was in Constructor
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        
        if(version.endsWith("SNAPSHOT")) {
            BBLogging.info("------------------------------------");
            BBLogging.info("Hello, and thank you for using the TESTING version of BigBrother!");
            BBLogging.info("Please note that, since this is far from complete, there will be many bugs.");
            BBLogging.info("IF YOU FIND ANY BUGS, PLEASE REPORT THEM ON https://github.com/tkelly910/BigBrother/issues");
            BBLogging.info("Please stay tuned in irc.esper.net #bigbrother for updates and build notifications.");
            BBLogging.info("------------------------------------");
        }

        // Initialize Settings - Needs to come pretty much first
        BBSettings.initialize(getDataFolder());

        // Download dependencies...
        if (BBSettings.libraryAutoDownload) {
            updater = new Updater();
            try {
                updater.check();
                updater.update();
            } catch (Throwable e) {
                BBLogging.severe("Could not download dependencies", e);
            }
        } else {
            BBLogging.debug("Downloading libraries was skipped");
        }

        // Create Connection
        if (!ConnectionManager.setupConnection()) {
            BBLogging.severe("Error getting a connection, disabling BigBrother...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Connection conn = ConnectionManager.getFirstConnection();
        if (conn == null) {
            BBLogging.severe("Could not establish SQL connection. Disabling BigBrother");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            ConnectionManager.cleanup("onEnable", conn, null, null);
        }
        ConnectionManager.cleanup("onEnable", conn, null, null);
        

        // Initialize tables
        BBLogging.info(BBDataTable.getInstance().toString()+" loaded!");
        worldManager = new WorldManager();
        BBPlayerInfo.ENVIRONMENT = new BBPlayerInfo("Environment");

        // Initialize Listeners
        playerListener = new BBPlayerListener(this);
        blockListener = new BBBlockListener(this);
        entityListener = new BBEntityListener(this);
        stickListener = new StickListener(this);
        sticker = new Sticker(getServer(), worldManager);

        // Update settings from old versions of BB
        if (new File("BigBrother").exists()) {
            updateSettings(getDataFolder());
        } else if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Apply fixes to DB for old BB
        Fix fix = new Fix1(getDataFolder());
        fix.apply();
        Fix fix2 = new Fix2(getDataFolder());
        fix2.apply();
        Fix fix3 = new Fix3(getDataFolder()); // 26 Feb
        fix3.apply();
        Fix fix4 = new Fix4(getDataFolder()); // 5 March, 2011 - N3X
        fix4.apply();
        Fix fix5 = new Fix5(getDataFolder()); // 24 March, 2011 - N3X
        fix5.apply();

        // Initialize Permissions, Help
        BBPermissions.initialize(getServer());
        BBHelp.initialize(this);

        // Register Events
        registerEvents();

        // Initialize Player Watching
        watcher = BBSettings.getWatcher(getServer(), getDataFolder());

        // Initialize DataBlockSender
        DataBlockSender.initialize(this, getDataFolder(), worldManager);

        // Initialize Cleanser
        Cleanser.initialize(this);

        // Done!
        BBLogging.info(name + " " + version + " enabled");
    }

    private void updateSettings(File dataFolder) {
        File oldDirectory = new File("BigBrother");
        dataFolder.getParentFile().mkdirs();
        oldDirectory.renameTo(dataFolder);
    }

    private void registerEvents() {
        // TODO Only register events that are being listened to
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);

        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.LEAVES_DECAY, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Monitor, this);

        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Monitor, this);

        // These events are used for Super Sticks
        // Moved to playerListener.  God help us. - N3X15
        //pm.registerEvent(Event.Type.PLAYER_INTERACT, stickListener, Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, stickListener, Priority.Low, this);
        
        
        BBCommand bbc = new BBCommand(this);
        bbc.registerExecutor("version", new VersionCommand(this));
        bbc.registerExecutor("update", new UpdateCommand(this));
        bbc.registerExecutor("watch", new WatchCommand(this));
        bbc.registerExecutor("watched", new WatchedCommand(this));
        bbc.registerExecutor("unwatched", new UnwatchedCommand(this));
        bbc.registerExecutor("cleanse", new CleanseCommand(this));
        bbc.registerExecutor("rollback", new RollbackCommand(this));
        bbc.registerExecutor("confirm", new ConfirmCommand(this));
        bbc.registerExecutor("delete", new DeleteCommand(this));
        bbc.registerExecutor("undo", new UndoCommand(this));
        bbc.registerExecutor("stick", new StickCommand(this));
        bbc.registerExecutor("log", new LogCommand(this));
        bbc.registerExecutor("done", new DoneCommand(this));
        bbc.registerExecutor("here", new HereCommand(this));
        bbc.registerExecutor("find", new FindCommand(this));
        bbc.registerExecutor("help", new HelpCommand(this));
        getCommand("bb").setExecutor(bbc);
    } 
    
    public boolean watching(Player player) {
        return watcher.watching(player);
    }

    public boolean toggleWatch(String player) {
        return watcher.toggleWatch(player);
    }

    public String getWatchedPlayers() {
        return watcher.getWatchedPlayers();
    }

    public boolean haveSeen(Player player) {
        return watcher.haveSeen(player);
    }

    public void watchPlayer(Player player) {
        watcher.watchPlayer(player);
    }

    public String getUnwatchedPlayers() {
        return watcher.getUnwatchedPlayers();
    }

    /**
     * Tell the user what mode their stick is.
     *
     * Better than having this copypasted 8 times
     * @param player Player to talk to about their stick/log
     * @author N3X15
     */
    public void reportStickMode(Player player, int stickLevel) {
        if (stickLevel > 0) {
            player.sendMessage(BigBrother.premessage + "Your current stick mode is " + sticker.descMode(player));
            player.sendMessage("Use " + ChatColor.RED + "/bb stick 0" + ChatColor.WHITE + " to turn it off");
        }
    }

    public boolean hasStick(Player player, ItemStack itemStack) {
        return sticker.hasStick(player, itemStack);
    }

    public void stick(Player player, Block block, boolean leftclick) {
        sticker.stick(player, block, leftclick);
    }

    public boolean rightClickStick(Player player) {
        return sticker.rightClickStick(player);
    }

    public boolean leftClickStick(Player player) {
        return sticker.leftClickStick(player);
    }
    
    public void closeChestIfOpen(BBPlayerInfo pi) {
        if(pi.hasOpenedChest()) {
            if(BBSettings.chestChanges) {
                World world = pi.getOpenedChest().getWorld();
                int x = pi.getOpenedChest().getX();
                int y = pi.getOpenedChest().getY();
                int z = pi.getOpenedChest().getZ();
                if(world.getBlockAt(x, y, z).getState() instanceof Chest) {
                    Chest chest = (Chest)world.getBlockAt(x, y, z).getState();
                    ItemStack[] orig = pi.getOldChestContents();
                    ItemStack[] latest = chest.getInventory().getContents();
                    DeltaChest dc = new DeltaChest(pi.getName(), chest, orig, latest);
                    dc.send();
                }
            }
            BBUsersTable.getInstance().userOpenedChest(pi.getName(), null, null); // Chest closed.
        }
    }
}

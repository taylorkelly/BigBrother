package me.taylorkelly.bigbrother;

import java.util.List;

import me.taylorkelly.bigbrother.finder.Finder;
import me.taylorkelly.bigbrother.rollback.Rollback;
import me.taylorkelly.bigbrother.rollback.RollbackConfirmation;
import me.taylorkelly.bigbrother.rollback.RollbackInterpreter;
import me.taylorkelly.util.Numbers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BBCommand implements CommandExecutor {
    
    private BigBrother plugin;

    public BBCommand(BigBrother plugin) {
           this.plugin=plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // TODO: Make this more modular.  If-trees drive me nuts. - N3X
            if (commandName.equals("bb")) {
                if (split.length == 0) {
                    return false;
                } else if (split[0].equalsIgnoreCase("version")) {
                    player.sendMessage("You're running: " + ChatColor.AQUA.toString() + plugin.name + " " + plugin.version);
                } else if (split[0].equalsIgnoreCase("update")) {
                    Updatr.updateAvailable(player);
                } else if (split[0].equalsIgnoreCase("watch") && BBPermissions.watch(player)) {
                    if (split.length == 2) {
                        List<Player> targets = plugin.getServer().matchPlayer(split[1]);
                        Player watchee = null;
                        if (targets.size() == 1) {
                            watchee = targets.get(0);
                        }
                        String playerName = (watchee == null) ? split[1] : watchee.getName();

                        if (plugin.toggleWatch(playerName)) {
                            String status = (watchee == null) ? " (offline)" : " (online)";
                            player.sendMessage(BigBrother.premessage + "Now watching " + playerName + status);
                        } else {
                            String status = (watchee == null) ? " (offline)" : " (online)";
                            player.sendMessage(BigBrother.premessage + "No longer watching " + playerName + status);
                        }
                    } else {
                        player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb watch <player>");
                    }
                } else if (split[0].equalsIgnoreCase("watched") && BBPermissions.info(player)) {
                    String watchedPlayers = plugin.getWatchedPlayers();
                    if (watchedPlayers.equals("")) {
                        player.sendMessage(BigBrother.premessage + "Not watching anyone.");
                    } else {
                        player.sendMessage(BigBrother.premessage + "Now watching:");
                        player.sendMessage(watchedPlayers);
                    }
                } else if (split[0].equalsIgnoreCase("unwatched") && BBPermissions.info(player)) {
                    String unwatchedPlayers = plugin.getUnwatchedPlayers();
                    if (unwatchedPlayers.equals("")) {
                        player.sendMessage(BigBrother.premessage + "Everyone on is being watched.");
                    } else {
                        player.sendMessage(BigBrother.premessage + "Currently not watching:");
                        player.sendMessage(unwatchedPlayers);
                    }
                } else if (split[0].equalsIgnoreCase("cleanse") && BBPermissions.cleanse(player)) {
                    if (Cleanser.needsCleaning()) {
                        Cleanser.clean(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "No need to cleanse. Check your settings.");
                    }
                } else if (split[0].equalsIgnoreCase("rollback") && BBPermissions.rollback(player)) {
                    if (split.length > 1) {
                        RollbackInterpreter interpreter = new RollbackInterpreter(player, split, plugin.getServer(), plugin.worldManager, plugin);
                        Boolean passed = interpreter.interpret();
                        if (passed != null) {
                            if (passed) {
                                interpreter.send();
                            } else {
                                player.sendMessage(BigBrother.premessage + ChatColor.RED + "Warning: " + ChatColor.WHITE + "You are rolling back without a time or radius argument.");
                                player.sendMessage("Use " + ChatColor.RED + "/bb confirm" + ChatColor.WHITE + " to confirm the rollback.");
                                player.sendMessage("Use " + ChatColor.RED + "/bb delete" + ChatColor.WHITE + " to delete it.");
                                RollbackConfirmation.setRI(player, interpreter);
                            }
                        }
                    } else {
                        player.sendMessage(BigBrother.premessage + "Usage is: " + ChatColor.RED + "/bb rollback name1 [name2] [options]");
                        player.sendMessage(BigBrother.premessage + "Please read the full command documentation at https://github.com/tkelly910/BigBrother/wiki/Commands");
                    }
                } else if (split[0].equalsIgnoreCase("confirm") && BBPermissions.rollback(player)) {
                    if (split.length == 1) {
                        if (RollbackConfirmation.hasRI(player)) {
                            RollbackInterpreter interpret = RollbackConfirmation.getRI(player);
                            interpret.send();
                        } else {
                            player.sendMessage(BigBrother.premessage + "You have no rollback to confirm.");
                        }
                    } else {
                        player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb confirm");
                    }

                } else if (split[0].equalsIgnoreCase("delete") && BBPermissions.rollback(player)) {
                    if (split.length == 1) {
                        if (RollbackConfirmation.hasRI(player)) {
                            RollbackConfirmation.deleteRI(player);
                            player.sendMessage(BigBrother.premessage + "You have deleted your rollback.");
                        } else {
                            player.sendMessage(BigBrother.premessage + "You have no rollback to delete.");
                        }
                    } else {
                        player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb delete");
                    }
                    // Undo rollback.
                } else if (split[0].equalsIgnoreCase("undo") && BBPermissions.rollback(player)) {
                    if (split.length == 1) {
                        if (Rollback.canUndo()) {
                            int size = Rollback.undoSize();
                            player.sendMessage(BigBrother.premessage + "Undo-ing last rollback of " + size + " blocks");
                            Rollback.undo(plugin.getServer(), player);
                            player.sendMessage(BigBrother.premessage + "Undo successful");
                        } else {
                            player.sendMessage(BigBrother.premessage + "No rollback to undo");
                        }
                    } else {
                        player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb undo");
                    }
                    // Report changes made to solid block.
                    // /bb stick [1]
                } else if (split[0].equalsIgnoreCase("stick") && BBPermissions.info(player)) {
                    if (split.length == 1) {
                        plugin.sticker.setMode(player, 1);
                        plugin.reportStickMode(player, 1);
                    } else if (split.length == 2 && Numbers.isInteger(split[1])) {
                        plugin.sticker.setMode(player, Integer.parseInt(split[1]));
                        plugin.reportStickMode(player, Integer.parseInt(split[1]));
                    } else {
                        player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb stick [#]");
                    }
                    // Report changes made to non-solid block
                    // /bb log == /bb stick 2
                } else if (split[0].equalsIgnoreCase("log") && BBPermissions.info(player)) {
                    if (split.length == 1) {
                        plugin.sticker.setMode(player, 2);
                        plugin.reportStickMode(player, 2);
                    } else {
                        player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb log");
                    }
                } else if (split[0].equalsIgnoreCase("here") && BBPermissions.info(player)) {
                    if (split.length == 1) {
                        Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                        finder.addReciever(player);
                        finder.find();
                    } else if (Numbers.isNumber(split[1]) && split.length == 2) {
                        Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                        finder.setRadius(Double.parseDouble(split[1]));
                        finder.addReciever(player);
                        finder.find();
                    } else if (split.length == 2) {
                        Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                        finder.addReciever(player);
                        List<Player> targets = plugin.getServer().matchPlayer(split[1]);
                        Player findee = null;
                        if (targets.size() == 1) {
                            findee = targets.get(0);
                        }
                        finder.find((findee == null) ? split[1] : findee.getName());
                    } else if (Numbers.isNumber(split[2]) && split.length == 3) {
                        Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                        finder.setRadius(Double.parseDouble(split[2]));
                        finder.addReciever(player);
                        List<Player> targets = plugin.getServer().matchPlayer(split[1]);
                        Player findee = null;
                        if (targets.size() == 1) {
                            findee = targets.get(0);
                        }
                        finder.find((findee == null) ? split[1] : findee.getName());
                    } else {
                        player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb here");
                        player.sendMessage("or " + ChatColor.RED + "/bb here <radius>");
                        player.sendMessage("or " + ChatColor.RED + "/bb here <name>");
                        player.sendMessage("or " + ChatColor.RED + "/bb here <name> <radius>");
                    }
                } else if (split[0].equalsIgnoreCase("find") && BBPermissions.info(player)) {
                    if (split.length == 4 && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3])) {
                        World currentWorld = player.getWorld();
                        Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                        Finder finder = new Finder(loc, plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                        finder.addReciever(player);
                        finder.find();
                    } else if (split.length == 5 && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3]) && Numbers.isNumber(split[4])) {
                        World currentWorld = player.getWorld();
                        Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                        Finder finder = new Finder(loc, plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                        finder.setRadius(Double.parseDouble(split[4]));
                        finder.addReciever(player);
                        finder.find();
                    } else if (split.length == 5 && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3])) {
                        World currentWorld = player.getWorld();
                        Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                        Finder finder = new Finder(loc, plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                        finder.addReciever(player);
                        List<Player> targets = plugin.getServer().matchPlayer(split[4]);
                        Player findee = null;
                        if (targets.size() == 1) {
                            findee = targets.get(0);
                        }
                        finder.find((findee == null) ? split[4] : findee.getName());
                    } else if (split.length == 6 && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3]) && Numbers.isNumber(split[5])) {
                        World currentWorld = player.getWorld();
                        Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                        Finder finder = new Finder(loc, plugin.getServer().getWorlds(), plugin.worldManager, plugin);
                        finder.setRadius(Double.parseDouble(split[5]));
                        finder.addReciever(player);
                        List<Player> targets = plugin.getServer().matchPlayer(split[4]);
                        Player findee = null;
                        if (targets.size() == 1) {
                            findee = targets.get(0);
                        }
                        finder.find((findee == null) ? split[4] : findee.getName());
                    } else {
                        player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb find <x> <y> <z>");
                        player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <radius>");
                        player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <name>");
                        player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <name> <radius>");
                    }
                } else if (split[0].equalsIgnoreCase("help")) {
                    // TODO: Modular help system, prereq: modular commands
                    player.sendMessage(BigBrother.premessage + "BigBrother version " + plugin.version + " help");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb watch (name)" + ChatColor.WHITE + " - Toggles the whether BB is watching (name)");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb watched" + ChatColor.WHITE + " - Shows which users are being watched by BB");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb unwatched" + ChatColor.WHITE + " - Shows which users that are logged in are unwatched");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb stick (0|1|2)" + ChatColor.WHITE + " - Gives you a stick (1), a log you can place (2), or disables either (0).");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb here" + ChatColor.WHITE + " - See changes that took place in the area you are standing in.");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb find x y z" + ChatColor.WHITE + " - Get the history of an area at a specific coordinate");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb rollback (name1) [name2] [options]" + ChatColor.WHITE + " - A command you should study in length via our helpful online wiki.");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb undo" + ChatColor.WHITE + " - Great for fixing bad rollbacks. It's like it never happened!");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb confirm" + ChatColor.WHITE + " - Confirms your rollback (if applicable).");
                    player.sendMessage(BigBrother.premessage + " " + ChatColor.RED + "/bb delete" + ChatColor.WHITE + " - Delete your rollback (if applicable).");
                } else {
                    return false;
                }
                return true;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (commandName.equals("bb")) {
                ConsoleCommandSender console = (ConsoleCommandSender) sender;
                if (split.length == 0) {
                    return false;
                } else if (split[0].equalsIgnoreCase("version")) {
                    console.sendMessage("You're running: " + ChatColor.AQUA.toString() + plugin.name + " " + plugin.version);
                } else if (split[0].equalsIgnoreCase("update")) {
                    Updatr.updateAvailable(console);
                }
                return true;
            }
            return false;
        }
        return false;
    }
    
}

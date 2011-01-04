package me.taylorkelly.bigbrother;

import java.util.ArrayList;

import org.bukkit.*;

public class Teleporter {
	private Location destination;
	private ArrayList<Player> players;
	
	public Teleporter(Location location) {
		this.destination = location;
		this.players = new ArrayList<Player>();
	}

	public void teleport() {
		World world = destination.getWorld();
		double x = destination.getX();
		double y = destination.getY();
		double z = destination.getZ();
		if (y < 1)
			y = 1;
		// TODO Chunk loading stuffs
		// if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(), destination.getBlockZ())))
		// 		world.loadChunk(world.getChunkAt(destination.getBlockX(), destination.getBlockZ()));

		while (blockIsAboveAir(world, x, y, z)) {
			y--;
		}
		while (!blockIsSafe(world, x, y, z)) {
			y++;
		}
		if (destination.getY() != y) {
			for (Player player : players) {
				player.sendMessage(BigBrother.premessage + "Supplied y location not safe.");
				player.sendMessage("Teleporting you to (" + (int) x + ", " + (int) y + ", " + (int) z + ")");
			}
		} else {
			for (Player player : players)
				player.sendMessage(BigBrother.premessage + "Teleporting you to (" + (int) x + ", " + (int) y + ", " + (int) z + ")");
		}
		for (Player player : players)
			player.teleportTo(new Location(world, x, y, z));
	}

	private boolean blockIsAboveAir(World world, double x, double y, double z) {
		return (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1), (int) Math.floor(z)).getType() == Material.Air);
	}

	public void addTeleportee(Player player) {
		players.add(player);

	}

	public boolean blockIsSafe(World world, double x, double y, double z) {
		return world.getBlockAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)).getType() == Material.Air
				&& world.getBlockAt((int) Math.floor(x), (int) Math.floor(y + 1), (int) Math.floor(z)).getType() == Material.Air;
	}
}

package me.taylorkelly.bigbrother;

import org.bukkit.*;

public class Teleporter {
	public static Server server = etc.getServer();

	private double x;
	private double y;
	private double z;
	private Player player;

	public Teleporter(Player player, String x, String y, String z) {
		this.x = Double.parseDouble(x);
		this.y = Double.parseDouble(y);
		if(this.y < 1) this.y = 1;
		this.z = Double.parseDouble(z);
		this.player = player;
	}

	public void teleport() {
		if (!server.isChunkLoaded((int) Math.floor(x), (int) Math.floor(y),
				(int) Math.floor(z)))
			server.loadChunk((int) Math.floor(x), (int) Math.floor(y),
					(int) Math.floor(z));
		double y = this.y;
		while (blockIsAboveAir(x, y, z)) {
			y--;
		}
		while (!blockIsSafe(x, y, z)) {
			y++;
		}
		if (this.y != y) {
			player.sendMessage(BigBrother.premessage
					+ "Supplied y location not safe...");
			player.sendMessage("Teleporting you to (" + (int) x + ", "
					+ (int) y + ", " + (int) z + ")");
		} else
			player.sendMessage(BigBrother.premessage + "Teleporting you to ("
					+ (int) x + ", " + (int) y + ", " + (int) z + ")");
		player.teleportTo(x, y, z, 0, 0);
	}

	private boolean blockIsAboveAir(double x2, double y2, double z2) {
		return (server.getBlockAt((int) Math.floor(x2),
				(int) Math.floor(y2 - 1), (int) Math.floor(z2)).getType() == 0);
	}

	public static boolean blockIsSafe(double x2, double y2, double z2) {
		return (server.getBlockAt((int) Math.floor(x2), (int) Math.floor(y2),
				(int) Math.floor(z2)).getType() == 0 && server.getBlockAt(
				(int) Math.floor(x2), (int) Math.floor(y2 + 1),
				(int) Math.floor(z2)).getType() == 0);
	}

}

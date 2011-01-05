package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class SignText extends BBDataBlock {
	public SignText(Player player, Sign sign) {
		//TODO Better World support
		//TODO creating text vs destroying text?
		super(player.getName(), 0, SIGN_TEXT, sign.getX(), sign.getY(), sign.getZ(),
				getText(sign));
	}

	private static String getText(Sign sign) {
		String message = "";
		for (int i = 0; i < 4; i++) {
			message += sign.getText(i) + "\u0095";
		}
		return message;
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, String data) {
		return new SignText(player, world, x, y, z, data);
	}

	private SignText(String player, int world, int x, int y, int z, String data) {
		super(player, SIGN_TEXT, world, x, y, z, data);
	}

	public void rollback(Server server) {
		// TODO Chunk loading stuffs
		// if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(), destination.getBlockZ())))
		// 		world.loadChunk(world.getChunkAt(destination.getBlockX(), destination.getBlockZ()));

		String[] lines = data.split("\u0095"); 
		//if block at x, y, z is a sign
		//get sign
		for (int i = 0; i < 4; i++) {
			sign.setText(lines[i]);
		}
	}
	
	public void redo(Server server) {
		// TODO Chunk loading stuffs
		// if (!world.isChunkLoaded(world.getChunkAt(destination.getBlockX(), destination.getBlockZ())))
		// 		world.loadChunk(world.getChunkAt(destination.getBlockX(), destination.getBlockZ()));

		//funky stuff.
	}
}

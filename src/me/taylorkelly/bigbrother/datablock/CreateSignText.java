package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;
import org.bukkit.block.Sign;

public class CreateSignText extends BBDataBlock {
	public CreateSignText(Player player, Sign sign) {
		//TODO Better World support
		super(player.getName(), 0, CREATE_SIGN_TEXT, sign.getX(), sign.getY(), sign.getZ(), 323, getText(sign));
	}

	private static String getText(Sign sign) {
		String message = "";
		for (int i = 0; i < 4; i++) {
			message += sign.getText(i) + "\u0095";
		}
		return message;
	}
	
	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
		return new CreateSignText(player, world, x, y, z, type, data);
	}

	private CreateSignText(String player, int world, int x, int y, int z, int type, String data) {
		super(player, CREATE_SIGN_TEXT, world, x, y, z, type, data);
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

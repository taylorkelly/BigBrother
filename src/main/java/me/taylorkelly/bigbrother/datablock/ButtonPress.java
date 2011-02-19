package me.taylorkelly.bigbrother.datablock;

import org.bukkit.Server;
import org.bukkit.block.Block;

public class ButtonPress extends BBDataBlock {

	public ButtonPress(String player, Block button) {
		//TODO Better world support
		super(player, Action.BUTTON_PRESS, 0, button.getX(), button.getY(), button.getZ(), 77, Byte.toString(button.getData()));
	}

	public void rollback(Server server) {}
	public void redo(Server server) {}

	public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
		return new ButtonPress(player, world, x, y, z, type, data);
	}

	private ButtonPress(String player, int world, int x, int y, int z, int type, String data) {
		super(player, Action.BUTTON_PRESS, world, x, y, z, type, data);
	}

}

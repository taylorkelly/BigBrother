package me.taylorkelly.bigbrother.datablock;

import org.bukkit.*;

public class SignText extends BBDataBlock {
	public SignText(Player player, Sign sign) {
		//TODO Better World support
		super(player.getName(), 0, SIGN_TEXT, sign.getX(), sign.getY(), sign.getZ(),
				getText(sign));
	}

	private static String getText(Sign sign) {
		String message = "";
		for (int i = 0; i < 4; i++) {
			if (!sign.getText(i).equals(""))
				message += sign.getText(i) + " ";
		}
		return message;
	}
}

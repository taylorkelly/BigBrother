package me.taylorkelly.bigbrother;

public class BBReporter {
	public static Server server = etc.getServer();

	
	public static void reportLogin(String player) {
		if (BBSettings.verbose) {
			BBLogger.log(player, BBSettings.getTime() + ": " + player + " has logged in\n");
			BBNotify.notify(player + "has logged in");
		} else {
			BBLogger.log(player, BBSettings.getTime() + ": LogIn\n");
			BBNotify.notify(player + " login");
		}
	}

	public static void reportDisconnect(String player) {
		if (BBSettings.verbose) {
			BBLogger.log(player, BBSettings.getTime() + ": " + player
					+ " has disconnected\n");
			BBNotify.notify(player + "has disconnected");
		} else {
			BBLogger.log(player, BBSettings.getTime() + ": Disconnect\n");
			BBNotify.notify(player + " disconnect");
		}
	}

	public static void reportCommand(String player, String[] split) {
		String command = "";
		for (String str : split)
			command += str + " ";
		if (BBSettings.verbose) {
			BBLogger.log(player, BBSettings.getTime() + ": " + player + " used command: "
					+ command + "\n");
			BBNotify.notify(player + " used command: " + command);
		} else {
			BBLogger.log(player, BBSettings.getTime() + ": Command: " + command + "\n");
			BBNotify.notify(player + " command: " + command);
		}
	}

	public static void reportBlockDestroy(Player player, Block block) {
		if (BBSettings.verbose) {
			String item = etc.getDataSource().getItem(player.getItemInHand());
			if (item.equals("-1"))
				item = "hand";
			BBLogger.log(player.getName(),
					BBSettings.getTime() + ": " + player.getName() + " destroyed block "
							+ etc.getDataSource().getItem(block.getType())
							+ " @(" + block.getX() + ", " + block.getY() + ", "
							+ block.getZ() + ")" + " with a " + item + "\n");
			BBNotify.notify(player.getName() + " destroyed block "
					+ etc.getDataSource().getItem(block.getType()) + " @("
					+ block.getX() + ", " + block.getY() + ", " + block.getZ()
					+ ")" + " with a " + item);
		} else {
			BBLogger.log(
					player.getName(),
					BBSettings.getTime() + ": Destroy " + block.getType() + " @("
							+ block.getX() + ", " + block.getY() + ", "
							+ block.getZ() + ")\n");
			BBNotify.notify(player.getName() + " destroy " + block.getType()
					+ " @(" + block.getX() + ", " + block.getY() + ", "
					+ block.getZ() + ")");
		}
	}

	public static void reportBlockPlacing(String player, Block block) {
		if (BBSettings.verbose) {
			BBLogger.log(player, BBSettings.getTime() + ": " + player
					+ " placed watched block type "
					+ etc.getDataSource().getItem(block.getType()) + " @("
					+ block.getX() + ", " + block.getY() + ", " + block.getZ()
					+ ")\n");
			BBNotify.notify(player + " placed watched block type "
					+ etc.getDataSource().getItem(block.getType()) + " @("
					+ block.getX() + ", " + block.getY() + ", " + block.getZ()
					+ ")");
		} else {
			BBLogger.log(
					player,
					BBSettings.getTime() + ": Place " + block.getType() + " @("
							+ block.getX() + ", " + block.getY() + ", "
							+ block.getZ() + ")\n");
			BBNotify.notify(player + " place " + block.getType() + " @("
					+ block.getX() + ", " + block.getY() + ", " + block.getZ()
					+ ")");
		}
	}

	public static void reportTeleportation(String player, Location from, Location to) {
		if (BBSettings.verbose) {
			BBLogger.log(player, BBSettings.getTime() + ": " + player + " teleported to ("
					+ (int) Math.floor(to.x) + ", " + (int) Math.floor(to.y)
					+ ", " + (int) Math.floor(to.z) + ")\n");
			BBNotify.notify(player + " teleported to ("
					+ (int) Math.floor(to.x) + ", " + (int) Math.floor(to.y)
					+ ", " + (int) Math.floor(to.z) + ")");
		} else {
			BBLogger.log(
					player,
					BBSettings.getTime() + ": TP (" + (int) Math.floor(to.x) + ", "
							+ (int) Math.floor(to.y) + ", "
							+ (int) Math.floor(to.z) + ")\n");
			BBNotify.notify(player + " TP (" + (int) Math.floor(to.x) + ", "
					+ (int) Math.floor(to.y) + ", " + (int) Math.floor(to.z)
					+ ")");
		}
	}

	public static void reportChat(String player, String message) {
		if (BBSettings.verbose) {
			BBLogger.log(player, BBSettings.getTime() + ": " + player + " said: "
					+ message + "\n");
		} else {
			BBLogger.log(
					player,
					BBSettings.getTime() + ": \"" + message + "\"\n");
		}
	}
	
	public static void reportSign(String player, Sign block) {
		String message = block.getText(0) + " " + block.getText(1) + " "
		+ block.getText(2) + " " + block.getText(3);
		
		if (BBSettings.verbose) {
			BBLogger.log(player, BBSettings.getTime() + ": " + player
					+ " made a sign that said: " + message + "\n");
			BBNotify.notify( player
					+ " made a sign that said: " + message);
		} else {
			BBLogger.log(
					player,
					BBSettings.getTime() + ": Sign \"" + message + "\"\n");
			BBNotify.notify( player
					+ ": Sign \"" + message + "\"");
		}
	}

	public static void torchCheck(Player player, Block block) {
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		Block torchTop = server.getBlockAt(x, y+1, z);
		if(torchTop.getType()==50 && torchTop.getData()==5) {
			reportBlockDestroy(player, torchTop);
		}
		Block torchNorth = server.getBlockAt(x+1, y, z);
		if(torchNorth.getType()==50 && torchNorth.getData()==1) {
			reportBlockDestroy(player, torchNorth);
		}
		Block torchSouth = server.getBlockAt(x-1, y, z);
		if(torchSouth.getType()==50 && torchSouth.getData()==2) {
			reportBlockDestroy(player, torchSouth);
		}
		Block torchEast = server.getBlockAt(x, y, z+1);
		if(torchEast.getType()==50 && torchEast.getData()==3) {
			reportBlockDestroy(player, torchEast);
		}
		Block torchWest = server.getBlockAt(x, y, z-1);
		if(torchWest.getType()==50 && torchWest.getData()==4) {
			reportBlockDestroy(player, torchWest);
		}

	}
}

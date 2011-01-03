public class BBListener extends PluginListener {
	public static Server server = etc.getServer();

	public boolean onCommand(Player player, String[] split) {
		boolean working = false;
		if (split[0].equalsIgnoreCase("/watchplayer")
				&& player.canUseCommand("/watchplayer")) {
			if (split.length > 1) {
				Player watchee = server.matchPlayer(split[1]);
				String playerName = (watchee == null) ? split[1] : watchee
						.getName();

				if (BigBrother.toggleWatch(playerName)) {
					if (watchee == null)
						player.sendMessage(BigBrother.premessage
								+ "Now watching " + playerName
								+ " (not logged in)");
					else
						player.sendMessage(BigBrother.premessage
								+ "Now watching " + playerName + " (logged in)");
				} else {
					player.sendMessage(BigBrother.premessage
							+ "No longer watching " + playerName);
				}

				working = true;
			} else
				working = false;
		} else if (split[0].equalsIgnoreCase("/watchedplayers")
				&& player.canUseCommand("/watchedplayers")) {
			String message = "Now watching:";
			for (String playerName : BBSettings.watchList) {
				message += " " + playerName + ",";
			}
			player.sendMessage(BigBrother.premessage
					+ message.substring(0, message.length() - 1));
			working = true;
		} else if (split[0].equalsIgnoreCase("/rollback")
				&& player.canUseCommand("/rollback")) {
			if (split.length > 1) {
				Player rollbacker = server.matchPlayer(split[1]);
				String playerName = (rollbacker == null) ? split[1]
						: rollbacker.getName();

				player.sendMessage(BigBrother.premessage
						+ BBRollback.rollback(playerName));
				working = true;
			} else
				working = false;
		} else if (split[0].equalsIgnoreCase("/bbtp")
				&& player.canUseCommand("/bbtp")) {
			if (split.length == 4 && isNumber(split[1]) && isNumber(split[2])
					&& isNumber(split[3])) {
				BBTP tp = new BBTP(player, split[1], split[2], split[3]);
				tp.teleport();
			} else
				working = false;
		} else if (split[0].equalsIgnoreCase("/bboptout")
				&& player.canUseCommand("/bboptout")) {
			if (BBSettings.optedOut.contains(player.getName())) {
				BBSettings.optedOut.remove(player.getName());
				player.sendMessage(BigBrother.premessage
						+ "You'll be notified of activity.");
			} else {
				BBSettings.optedOut.add(player.getName());
				player.sendMessage(BigBrother.premessage
						+ "You'll no longer receive updates.");
			}
			working = true;
		} else if (split[0].equalsIgnoreCase("/bbhere")
				&& player.canUseCommand("/bbhere")) {
			if (split.length == 1) {
				BBFinder.bbfind(player, player.getLocation(), 5);
				working = true;
			} else if (isNumber(split[1])) {
				BBFinder.bbfind(player, player.getLocation(),
						Double.parseDouble(split[1]));
				working = true;
			}
		} else if (split[0].equalsIgnoreCase("/bbfind")
				&& player.canUseCommand("/bbfind")) {
			if (split.length == 4 && isNumber(split[1]) && isNumber(split[2])
					&& isNumber(split[3])) {
				Location loc = new Location(Double.parseDouble(split[1]),
						Double.parseDouble(split[2]),
						Double.parseDouble(split[3]));
				BBFinder.bbfind(player, loc, 5);
				working = true;
			} else if (split.length == 5 && isNumber(split[1])
					&& isNumber(split[2]) && isNumber(split[3])
					&& isNumber(split[4])) {
				Location loc = new Location(Double.parseDouble(split[1]),
						Double.parseDouble(split[2]),
						Double.parseDouble(split[3]));
				BBFinder.bbfind(player, loc,
						Double.parseDouble(split[4]));
				working = true;
			}
			working = false;
			return true;
		}
		if (BBSettings.commands
				&& BBSettings.watchList.contains(player.getName())) {
			BBReporter.reportCommand(player.getName(), split);
		}
		return working;
	}

	public void onLogin(Player player) {
		if (BBSettings.login && BBSettings.watchList.contains(player.getName())) {
			BBReporter.reportLogin(player.getName());
		}

		if (!BBSettings.seenPlayers.contains(player.getName())) {
			BBSettings.seenPlayers.add(player.getName());
			BigBrother.saveSeenPlayers();
			if (BBSettings.autoWatch) {
				BigBrother.watch(player.getName());
			}
		}
	}

	public void onDisconnect(Player player) {
		if (BBSettings.logout
				&& BBSettings.watchList.contains(player.getName())) {
			BBReporter.reportDisconnect(player.getName());
		}
	}

	public boolean onTeleport(Player player, Location from, Location to) {
		if (BBSettings.position
				&& BBSettings.watchList.contains(player.getName())) {
			BBReporter.reportTeleportation(player.getName(), from, to);
		}
		return false;
	}

	public boolean onBlockBreak(Player player, Block block) {
		if (BBSettings.blockDestroying
				&& BBSettings.watchList.contains(player.getName())) {
			BBReporter.torchCheck(player, block);
			BBReporter.reportBlockDestroy(player, block);
		}

		return false;
	}

	public boolean onBlockPlace(Player player, Block blockPlaced,
			Block blockClicked, Item itemInHand) {
		if (BBSettings.blockPlacing
				&& BBSettings.watchList.contains(player.getName())) {
			if (BBSettings.watchedBlocks.size() == 0
					|| BBSettings.watchedBlocks.contains(blockPlaced.getType()))
				BBReporter.reportBlockPlacing(player.getName(), blockPlaced);
		}
		return false;
	}

	public boolean onChat(Player player, java.lang.String message) {
		if (BBSettings.chat && BBSettings.watchList.contains(player.getName())) {
			BBReporter.reportChat(player.getName(), message);
		}
		return false;
	}
	
	public boolean onSignChange(Player player, Sign sign) {
		if (BBSettings.blockPlacing
				&& BBSettings.watchList.contains(player.getName())) {
			if (BBSettings.watchedBlocks.size() == 0
					|| BBSettings.watchedBlocks.contains(323))
				BBReporter.reportBlockPlacing(player.getName(),
						new Block(323, sign.getX(), sign.getY(),
								sign.getZ()));
		}
		BBReporter.reportSign(player.getName(), sign);
		return false;
	}

	public static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isNumber(String string) {
		try {
			Double.parseDouble(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
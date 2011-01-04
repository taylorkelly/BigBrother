package me.taylorkelly.bigbrother;

import java.io.IOException;

import org.bukkit.Player;

public class BBWatcher {
	static boolean toggleWatch(String player) {
		boolean watching = false;

		if (BBSettings.watchList.contains(player)) {
			BBSettings.watchList.remove(player);
			if (BBSettings.verbose) {
				BBLogger.log(player, (BBSettings.getTime()
						+ ": No longer watching " + player + "\n"));
				BBNotify.notify("No longer watching " + player + "\n");
			} else {
				BBLogger.log(player, BBSettings.getTime() + ": NotWatching\n");
				BBNotify.notify("Not watching " + player + "\n");
			}
			saveWatchList();
		} else {
			watch(player);
			watching = true;
		}
		return watching;
	}

	static void watch(String player) {
		if (!BBSettings.watchList.contains(player)) {
			BBSettings.watchList.add(player);
			if (BBSettings.verbose) {
				BBLogger.log(player, (BBSettings.getTime() + ": Now watching "
						+ player + "\n"));
				BBNotify.notify("Now watching " + player + "\n");
			} else {
				BBLogger.log(player, BBSettings.getTime() + ": NotWatching\n");
				BBNotify.notify("Watching " + player + "\n");
			}
			saveWatchList();
		}
	}

	private static void saveWatchList() {
		String list = "";

		for (String player : BBSettings.watchList) {
			list += player + ",";
		}

		if (list.startsWith(","))
			list = list.substring(1);
		if (list.endsWith(","))
			list = list.substring(0, list.length() - 1);

		PropertiesFile pf = new PropertiesFile("bigbrother.txt");
		try {
			pf.load();
		} catch (IOException e) {
		}
		pf.setString("watchedplayers", list);
		pf.save();
	}

	static void saveSeenPlayers() {
		String list = "";

		for (String player : BBSettings.seenPlayers) {
			list += player + ",";
		}

		if (list.startsWith(","))
			list = list.substring(1);
		if (list.endsWith(","))
			list = list.substring(0, list.length() - 1);

		PropertiesFile pf = new PropertiesFile("bigbrother.txt");
		try {
			pf.load();
		} catch (IOException e) {
		}
		pf.setString("seenPlayers", list);
		pf.save();
	}

	public boolean verifyAdmin(String apt, String input, Player heir) {
		return (heir.canUseCommand(apt) && input.equalsIgnoreCase(apt));
	}

	public static String fixName(String player) {
		return player.replace(".", "").replace(":", "").replace("<", "")
				.replace(">", "").replace("*", "").replace("\\", "")
				.replace("/", "").replace("?", "").replace("\"", "")
				.replace("|", "");
	}
}

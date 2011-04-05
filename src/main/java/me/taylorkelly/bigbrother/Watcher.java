package me.taylorkelly.bigbrother;

import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Watcher {

	private Server server;

    public Watcher(Server server) {
	    this.server=server;
	}

	public boolean watching(Player player) {
		return BBUsersTable.getInstance().getUserByName(player.getName()).getWatched();
	}

	public boolean toggleWatch(String player) {
		BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player);
		pi.setWatched(!pi.getWatched());
		return pi.getWatched();
	}

	public String getWatchedPlayers() {
		StringBuilder list = new StringBuilder();
		for (BBPlayerInfo pi : BBUsersTable.getInstance().knownPlayers.values()) {
		    if(pi.getWatched()) {
		        list.append(pi.getName());
		        list.append(", ");
		    }
		}
		if (list.toString().contains(","))
			list.delete(list.lastIndexOf(","), list.length());
		return list.toString();
	}

	public boolean haveSeen(Player player) {
		return BBUsersTable.getInstance().knownNames.containsKey(player.getName());
	}

	public void watchPlayer(Player player) {
		watchPlayer(player.getName());
	}

	public void watchPlayer(String player) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player);
        pi.setWatched(true);
	}

	public String getUnwatchedPlayers() {
		Player[] playerList = server.getOnlinePlayers();
        StringBuilder list = new StringBuilder();
        for (Player name : playerList) {
            BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(name.getName());
            if(pi.getWatched()) {
                list.append(pi.getName());
                list.append(", ");
            }
        }
        if (list.toString().contains(","))
            list.delete(list.lastIndexOf(","), list.length());
        return list.toString();
	}
}

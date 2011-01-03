
public class BBNotify {
	public static Server server = etc.getServer();
	public static void notify(String message) {
		for(Player player: server.getPlayerList()) {
			if(BBSettings.notifyMods && player.canUseCommand("/bboptout")  && !BBSettings.optedOut.contains(player.getName())) {
				player.sendMessage(BigBrother.premessage + message);
			}
		}
	}
	

}

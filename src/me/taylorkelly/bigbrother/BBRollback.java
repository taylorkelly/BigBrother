package me.taylorkelly.bigbrother;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;


public class BBRollback {
	private static Server server = etc.getServer();
	public static String directory = "bigbrother";

	
	static String rollback(String player) {
		String playerFix = BigBrother.fixName(player);
		Stack<Block> stack = new Stack<Block>();
		File file = new File(directory, playerFix + ".txt");
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("destroyed block ")) {
					int typeStart = line.indexOf("block ") + "block ".length();
					int typeEnd = line.indexOf("@(") - 1;
					int type = etc.getDataSource().getItem(
							line.substring(typeStart, typeEnd));
					line = line.substring(typeEnd + 3);
					int xEnd = line.indexOf(",");
					int x = Integer.parseInt(line.substring(0, xEnd));
					line = line.substring(xEnd + 1);
					int yEnd = line.indexOf(",");
					int y = Integer.parseInt(line.substring(1, yEnd));
					line = line.substring(yEnd + 1);
					int zEnd = line.indexOf(")");
					int z = Integer.parseInt(line.substring(1, zEnd));
					stack.push(new Block(type, x, y, z));
				} else if (line.contains("placed watched block")) {
					int typeEnd = line.indexOf("@(") - 1;
					line = line.substring(typeEnd + 3);
					int xEnd = line.indexOf(",");
					int x = Integer.parseInt(line.substring(0, xEnd));
					line = line.substring(xEnd + 1);
					int yEnd = line.indexOf(",");
					int y = Integer.parseInt(line.substring(1, yEnd));
					line = line.substring(yEnd + 1);
					int zEnd = line.indexOf(")");
					int z = Integer.parseInt(line.substring(1, zEnd));
					stack.push(new Block(0, x, y, z));
				} else if (line.contains("Destroy")) {
					int typeStart = line.indexOf("Destroy ")
							+ "Destroy ".length();
					int typeEnd = line.indexOf("@(") - 1;
					int type = Integer.parseInt(line.substring(typeStart,
							typeEnd));
					line = line.substring(typeEnd + 3);
					int xEnd = line.indexOf(",");
					int x = Integer.parseInt(line.substring(0, xEnd));
					line = line.substring(xEnd + 1);
					int yEnd = line.indexOf(",");
					int y = Integer.parseInt(line.substring(1, yEnd));
					line = line.substring(yEnd + 1);
					int zEnd = line.indexOf(")");
					int z = Integer.parseInt(line.substring(1, zEnd));
					stack.push(new Block(type, x, y, z));
				} else if (line.contains("Place")) {
					int typeEnd = line.indexOf("@(") - 1;
					line = line.substring(typeEnd + 3);
					int xEnd = line.indexOf(",");
					int x = Integer.parseInt(line.substring(0, xEnd));
					line = line.substring(xEnd + 1);
					int yEnd = line.indexOf(",");
					int y = Integer.parseInt(line.substring(1, yEnd));
					line = line.substring(yEnd + 1);
					int zEnd = line.indexOf(")");
					int z = Integer.parseInt(line.substring(1, zEnd));
					stack.push(new Block(0, x, y, z));
				} else if (line.contains("Rollback")) {
					stack.clear();
				}
			}
		} catch (FileNotFoundException e) {
			return player + "'s log not found";
		} catch (Exception e) {
			System.out.println("[BBROTHER]: Log parsing error occurred.");
		}

		if (stack.size() == 0)
			return "Nothing to rollback";
		while (!stack.isEmpty()) {
			Block block = stack.pop();
			if(!server.isChunkLoaded(block)) {
				server.loadChunk(block);
			}
			server.setBlockAt(block.getType(), block.getX(), block.getY(),
					block.getZ());
		}
		
		if (BBSettings.verbose) {
			BBLogger.log(player, BBSettings.getTime() + ": Rollbacked " + player
					+ "'s changes\n");
			BBNotify.notify(player + "has been rollbacked");
		} else {
			BBLogger.log(player, BBSettings.getTime() + ": Rollback\n");
			BBNotify.notify(player + " rollback");
		}

		return "Successfully rolled back " + player;
	}

}

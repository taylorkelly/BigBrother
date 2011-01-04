package me.taylorkelly.bigbrother;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.Player;

public class Finder {
	public static String directory = "bigbrother";

	public Finder(Location location) {
		// TODO Auto-generated constructor stub
	}

	public static void bbfind(Player p, Location loc, double dist) {

		File folder = new File(directory);
		File[] files = folder.listFiles(new LogFilter());

		p.sendMessage(BigBrother.premessage + "Changes:");

		for (File file : files) {
			Stack<Block> playerStack = new Stack<Block>();
			try {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					try {
						String line = sc.nextLine();
						if (line.contains("destroyed block ")) {
							int typeStart = line.indexOf("block ") + "block ".length();
							int typeEnd = line.indexOf("@(") - 1;
							int type = etc.getDataSource().getItem(line.substring(typeStart, typeEnd));
							line = line.substring(typeEnd + 3);
							int xEnd = line.indexOf(",");
							int x = Integer.parseInt(line.substring(0, xEnd));
							line = line.substring(xEnd + 1);
							int yEnd = line.indexOf(",");
							int y = Integer.parseInt(line.substring(1, yEnd));
							line = line.substring(yEnd + 1);
							int zEnd = line.indexOf(")");
							int z = Integer.parseInt(line.substring(1, zEnd));
							Block block = new Block(type, x, y, z);
							if (distance(new Location(block.getX(), block.getY(), block.getZ()), loc) <= dist) {
								block.setStatus(3);
								playerStack.push(block);
							}
						} else if (line.contains("placed watched block")) {
							int typeStart = line.indexOf("block ") + "block ".length();
							int typeEnd = line.indexOf("@(") - 1;
							int type = etc.getDataSource().getItem(line.substring(typeStart, typeEnd));
							line = line.substring(typeEnd + 3);
							int xEnd = line.indexOf(",");
							int x = Integer.parseInt(line.substring(0, xEnd));
							line = line.substring(xEnd + 1);
							int yEnd = line.indexOf(",");
							int y = Integer.parseInt(line.substring(1, yEnd));
							line = line.substring(yEnd + 1);
							int zEnd = line.indexOf(")");
							int z = Integer.parseInt(line.substring(1, zEnd));
							Block block = new Block(type, x, y, z);

							if (distance(new Location(block.getX(), block.getY(), block.getZ()), loc) <= dist) {
								block.setStatus(0);
								playerStack.push(block);
							}
						} else if (line.contains("Destroy")) {
							int typeStart = line.indexOf("Destroy ") + "Destroy ".length();
							int typeEnd = line.indexOf("@(") - 1;
							int type = Integer.parseInt(line.substring(typeStart, typeEnd));
							line = line.substring(typeEnd + 3);
							int xEnd = line.indexOf(",");
							int x = Integer.parseInt(line.substring(0, xEnd));
							line = line.substring(xEnd + 1);
							int yEnd = line.indexOf(",");
							int y = Integer.parseInt(line.substring(1, yEnd));
							line = line.substring(yEnd + 1);
							int zEnd = line.indexOf(")");
							int z = Integer.parseInt(line.substring(1, zEnd));
							Block block = new Block(type, x, y, z);
							if (distance(new Location(block.getX(), block.getY(), block.getZ()), loc) <= dist) {
								block.setStatus(3);
								playerStack.push(block);
							}
						} else if (line.contains("Place")) {
							int typeStart = line.indexOf("Place ") + "Place ".length();
							int typeEnd = line.indexOf("@(") - 1;
							int type = Integer.parseInt(line.substring(typeStart, typeEnd));
							line = line.substring(typeEnd + 3);
							int xEnd = line.indexOf(",");
							int x = Integer.parseInt(line.substring(0, xEnd));
							line = line.substring(xEnd + 1);
							int yEnd = line.indexOf(",");
							int y = Integer.parseInt(line.substring(1, yEnd));
							line = line.substring(yEnd + 1);
							int zEnd = line.indexOf(")");
							int z = Integer.parseInt(line.substring(1, zEnd));
							Block block = new Block(type, x, y, z);
							if (distance(new Location(block.getX(), block.getY(), block.getZ()), loc) <= dist) {
								block.setStatus(0);
								playerStack.push(block);
							}
						} else if (line.contains("Rollback")) {
							playerStack.clear();
						}
					} catch (Exception e) {
					}
				}
			} catch (FileNotFoundException e) {
			}
			while (!playerStack.isEmpty()) {
				Block block = playerStack.pop();
				String playerName = file.getName().replace(".txt", "");
				String didWhat = (block.getStatus() == 0) ? "placed" : "destroyed";
				p.sendMessage(playerName + " " + didWhat + " " + etc.getDataSource().getItem(block.getType()));
			}
		}
	}

	public static double distance(Location loc1, Location loc2) {
		return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2) + Math.pow(loc1.getY() - loc2.getY(), 2) + Math.pow(loc1.getZ() - loc2.getZ(), 2));
	}

	public void setRadius(double defaultRadius) {
		// TODO Auto-generated method stub

	}

	public void setReciever(Player player) {
		// TODO Auto-generated method stub

	}

	public void find() {
		// TODO Auto-generated method stub

	}

}

package me.taylorkelly.bigbrother;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class BBLogger {
	public static String directory = "bigbrother";

	public static void log(String player, String message) {
		String playerFix = BigBrother.fixName(player);
		File file = new File(directory, playerFix + ".txt");
		try {
			FileWriter fwriter = new FileWriter(file, true);
			BufferedWriter bwriter = new BufferedWriter(fwriter);
			bwriter.write(message);
			bwriter.close();
		} catch (IOException e) {
			System.out.println("[BBROTHER]: Error writing to file." + file.toString());
		}
	}
	
	public static void initialize() {
		if (!new File(directory).exists()) {
			try {
				(new File(directory)).mkdir();
			} catch (Exception e) {
				System.out.println("[BBROTHER]: Can't create bigbrother/ directory...");
			}
		}
	}
}

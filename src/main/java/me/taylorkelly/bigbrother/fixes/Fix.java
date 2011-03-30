package me.taylorkelly.bigbrother.fixes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import me.taylorkelly.bigbrother.BBLogging;

public abstract class Fix {
    protected int currVersion = -1;
    protected final int LATEST_VERSION=5;
    protected File dataFolder;

    public Fix(File dataFolder) {
        this.dataFolder = dataFolder;
    }
    public abstract void apply();

    public boolean needsUpdate(int version) {
        if (currVersion == -1)
            currVersion = getCurrVersion();
        if (currVersion >= version) {
            return false;
        } else
            return true;
    }

    private int getCurrVersion() {
        File file = new File(dataFolder, "VERSION");
        if (!file.exists()) {
            return LATEST_VERSION; // If the file's inexistant, just shut up and create the tables :|
        } else {
            try {
                Scanner scan = new Scanner(file);
                String version = scan.nextLine();
                try {
                    int numVersion = Integer.parseInt(version);
                    return numVersion;
                } catch (Exception e) {
                    return 0;
                }

            } catch (FileNotFoundException e) {
                return 0;
            }
        }
    }

    protected void updateVersion(int version) {
        File file = new File(dataFolder, "VERSION");
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            if (!file.exists())
                file.createNewFile();
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            bwriter.write(version + "");
            bwriter.flush();
        } catch (IOException e) {
            BBLogging.severe("IO Exception with file " + file.getName());
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null) {
                    fwriter.close();
                }
            } catch (IOException e) {
                BBLogging.severe("IO Exception with file " + file.getName() + " (on close)");
            }
        }
    }
}

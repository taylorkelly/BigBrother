package me.taylorkelly.bigbrother.fixes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

import me.taylorkelly.bigbrother.BigBrother;

public abstract class Fix {
    protected String currVersion;
    
    public abstract void apply();
    
    public boolean needsUpdate(String version) {
        if(currVersion == null) currVersion = getCurrVersion();
        if(currVersion.equalsIgnoreCase(version)) {
            return false;
        } else return true;
    }
    
    private String getCurrVersion() {
        File file = new File(BigBrother.directory, "VERSION");
        if(!file.exists()) {
            return "";
        } else {
            try {
                Scanner scan = new Scanner(file);
                return scan.nextLine();
            } catch (FileNotFoundException e) {
                return "";
            }
        }
    }
    
    protected void updateVersion(String version) {
        File file = new File(BigBrother.directory, "VERSION");
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            if (!file.exists())
                file.createNewFile();
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            bwriter.write(version);
            bwriter.flush();
        } catch (IOException e) {
            BigBrother.log.log(Level.SEVERE, "[BBROTHER]: IO Exception with file " + file.getName());
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                } if (fwriter != null) {
                    fwriter.close();
                }
            } catch (IOException e) {
                BigBrother.log.log(Level.SEVERE, "[BBROTHER]: IO Exception with file " + file.getName() + " (on close)");
            }
        }
    }
}

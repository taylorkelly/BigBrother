package me.taylorkelly.bigbrother.tests;

import java.io.File;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;

import org.bukkit.util.config.Configuration;
import org.junit.*;

public class TestBigBrother {
    private File testFolder;

    @Before
    public void setup() {
        this.testFolder=new File("tests");
    }
    
    @Test
    public void configGeneration() {
        File dataFolder = new File(testFolder,"configGeneration");
        dataFolder.mkdirs();
        File settingsFile =new File(dataFolder,"BigBrother.yml");
        BBSettings.initialize(dataFolder);
        Assert.assertTrue(settingsFile.exists());
    }
    
    @Test
    public void configLoading() {
        File dataFolder = new File(testFolder,"configLoading");
        dataFolder.mkdirs();
        File settingsFile =new File(dataFolder,"BigBrother.yml");
        
        Configuration cfg = new Configuration(settingsFile);
        cfg.setProperty("database.type", "MYSQL");

        BBSettings.initialize(dataFolder);
        
        Assert.assertTrue(BBSettings.usingDBMS(DBMS.MYSQL));
    }
}

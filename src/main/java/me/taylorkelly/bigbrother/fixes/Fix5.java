package me.taylorkelly.bigbrother.fixes;

import java.io.File;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

/**
 * Normalize players
 * @author N3X15
 *
 */
public class Fix5 extends Fix {

    public Fix5(File dataFolder) {
        super(dataFolder);
    }
    protected int version = 5;

    @Override
    public void apply() {
        if (needsUpdate(version)) {
            BBLogging.info("Updating tables for 1.7.2");
            if(BBUsersTable.getInstance().importRecords())
                updateVersion(version);
            //else
            //    System.exit(0);
        }
    }

}

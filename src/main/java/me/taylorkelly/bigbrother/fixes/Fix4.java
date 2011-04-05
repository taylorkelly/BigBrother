package me.taylorkelly.bigbrother.fixes;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

/**
 * Drop ungrouped x, y, and z indices and then create posIndex index.
 * @author N3X15
 *
 */
public class Fix4 extends Fix {

    public Fix4(File dataFolder) {
        super(dataFolder);
    }
    protected int version = 4;
    /**
     * TODO MySQL if statement? On new tables this crashes because they have no
     * `x`, `y`, or `z` INDEXES to drop.
     */
    public static final String UPDATE_SQL[] = {
        "ALTER TABLE `bbdata` DROP INDEX `x`;",
        "ALTER TABLE `bbdata` DROP INDEX `y`;",
        "ALTER TABLE `bbdata` DROP INDEX `z`;",
        "ALTER TABLE `bbdata` ADD INDEX `posIndex` (`x`, `y`, `z`);"
    };

    @Override
    public void apply() {
        if (needsUpdate(version)) {
            BBLogging.info("Updating table for 1.7");
            boolean sqlite = BBSettings.databaseSystem == DBMS.H2;

            updateVersion(version);
            updateTable(sqlite);
        }
    }

    private static boolean updateTable(boolean sqlite) {
        // SQLite sucks, just skip it.
        if (sqlite) {
            return true;
        } else {
            Connection conn = null;
            Statement st = null;
            try {
                conn = ConnectionManager.getConnection();
                if(conn==null) return false;
                st = conn.createStatement();
                for (String update : UPDATE_SQL) {
                    st.executeUpdate(update);
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                BBLogging.severe("[Fix4] Unable to remove/create new indices.  However, this shouldn't be a problem except performance-wise.");
                return false;
            } finally {
                ConnectionManager.cleanup("Fix 4", conn, st, null);
            }
        }
    }
}

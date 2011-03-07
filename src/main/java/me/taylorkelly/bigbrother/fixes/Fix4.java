package me.taylorkelly.bigbrother.fixes;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

/**
 * Drop ungrouped x, y, and z indices and then create posIndex index.
 * @author Rob
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
            BBLogging.info("Updating table for 1.6.4");
            boolean sqlite = !BBSettings.mysql;

            if (updateTable(sqlite)) {
                updateVersion(version);
            }
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
                st = conn.createStatement();
                for (String update : UPDATE_SQL) {
                    st.executeUpdate(update);
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                BBLogging.severe("[Fix4] Unable to remove/create new indices.", e);
                return false;
            } finally {
                try {
                    if (st != null) {
                        st.close();
                    }
                } catch (SQLException e) {
                    BBLogging.severe("Fix 4 failed.");
                }
            }
        }
    }
}

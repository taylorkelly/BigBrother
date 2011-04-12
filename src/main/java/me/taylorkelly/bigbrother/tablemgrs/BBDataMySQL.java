package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

/**
 * BBDataTable, but for MySQL
 * 
 * @author N3X15
 */
public class BBDataMySQL extends BBDataTable {
    public final int revision = 1;
    public String toString() {
        return "BBData MySQL Driver r"+Integer.valueOf(revision);
    }
    


    /**
     * Returns "LOW_PRIORITY" for MySQL when mysqlLowPrioInserts is set.
     * 
     * @return LOW_PRIORITY | ""
     */
    public static String getMySQLIgnore() {
        if (BBSettings.mysqlLowPrioInserts) {
            return " LOW_PRIORITY ";
        } else {
            return " ";
        }
    }
    
    @Override
    public String getPreparedDataBlockStatement() throws SQLException {
        return "INSERT "+getMySQLIgnore()+" INTO " + getTableName()
                + " (date, player, action, world, x, y, z, type, data, rbacked) VALUES (?,?,?,?,?,?,?,?,?,0)";
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE `"+getTableName()+"` ("
        + "`id` INT NOT NULL AUTO_INCREMENT," 
        + "`date` INT UNSIGNED NOT NULL DEFAULT '0'," 
        + "`player` INT UNSIGNED NOT NULL DEFAULT 0," 
        + "`action` tinyint NOT NULL DEFAULT '0'," 
        + "`world` tinyint NOT NULL DEFAULT '0'," 
        + "`x` int NOT NULL DEFAULT '0'," 
        + "`y` tinyint UNSIGNED NOT NULL DEFAULT '0'," 
        + "`z` int NOT NULL DEFAULT '0'," 
        + "`type` smallint NOT NULL DEFAULT '0',"
        + "`data` varchar(500) NOT NULL DEFAULT '',"
        + "`rbacked` boolean NOT NULL DEFAULT '0',"
        + "PRIMARY KEY (`id`)," 
        + "INDEX(`world`)," 
        + "INDEX(`x`,`y`,`z`)," 
        + "INDEX(`player`),"
        + "INDEX(`action`)," 
        + "INDEX(`date`)," 
        + "INDEX(`type`)," 
        + "INDEX(`rbacked`)" 
        + ")";
    }
    // GoMySQL suggested partitioning. Currently, I like the following modifier,
    // but it doesn't work on MySQL < 5.1. So we need to add a setting for
    // "enableMySQLPartitioning" or something
    // PARTITION BY LINEAR KEY(date) PARTITIONS 12;
    // Another stupid idea: use table comments for tracking table revision.
    

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#onLoad()
     */
    @Override
    public void onLoad() {
        // bbdata needs to be MyISAM, but the conversion takes forever.
        checkDBEngine(getTableName(), "MyISAM", true);
    }
    
    /**
     * Ensure we're using the right storage engine.
     *
     * @param tableName
     * @param requiredEngine
     */
    private void checkDBEngine(String tableName, String requiredEngine, boolean optional) {
        String engine = getEngine(tableName);
        if (engine == null) {
            return; // Error.
        }
        if (!engine.equalsIgnoreCase(requiredEngine)) {
            if (!optional) {
                BBLogging.warning("Changing " + tableName + " so that it uses " + requiredEngine + " instead of " + engine + ". THIS MAY TAKE A WHILE!");
                setEngine(tableName, requiredEngine);
            } else {
                BBLogging.warning("Table " + tableName + " uses the MySQL storage engine " + engine + ".");
                BBLogging.info("For optimal performance, we suggest changing to " + requiredEngine + " via the following SQL statement:");
                BBLogging.info("  ALTER TABLE " + tableName + " ENGINE = " + requiredEngine + ";");
                BBLogging.info("Please note that, on many tables, this could take a very long time.");
            }
        }
    }

    private void setEngine(String tableName, String engine) {
        Connection conn = null;
        Statement st = null;
        try {
            conn = ConnectionManager.getConnection();
            if(conn==null) return;
            st = conn.createStatement();
            st.executeUpdate("ALTER TABLE " + tableName + " ENGINE = " + engine);
            conn.commit();
        } catch (SQLException e) {
            BBLogging.severe("Altering " + tableName + " to use " + engine + " triggered an exception.", e);
        } finally {
            ConnectionManager.cleanup( "setEngine",  conn, st, null );
        }
    }

    private String getEngine(String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;
        String engine = null;
        try {
            conn = ConnectionManager.getConnection();
            if(conn==null) return null;
            stmt = conn.createStatement();
            if (!stmt.execute("SHOW TABLE STATUS WHERE Name = '" + tableName + "'")) {
                BBLogging.severe("Could not fetch table information for table " + tableName);
                return null;
            }
            rs = stmt.getResultSet();
            rs.first();
            engine = rs.getString("Engine");
        } catch (SQLException e) {
            BBLogging.severe("Could not retreive table information.", e);
        } finally {
            ConnectionManager.cleanup( "getEngine",  conn, stmt, rs );
        }
        return engine;
    }



	@Override
	public String getCleanseAged(Long timeAgo, long deletesPerCleansing) {
		String cleansql = "DELETE FROM `"+getTableName()+"` WHERE date < " + timeAgo;
        if (BBSettings.deletesPerCleansing > 0) {
            cleansql += " LIMIT " + Long.valueOf(BBSettings.deletesPerCleansing);
        }
        cleansql += ";";
        return cleansql;
	}



	@Override
	public String getCleanseByLimit(Long maxRecords, long deletesPerCleansing) {
		String cleansql = "DELETE FROM `"+getTableName()+"` LEFT OUTER JOIN (SELECT `id` FROM `bbdata` ORDER BY `id` DESC LIMIT 0,"
	    	+ maxRecords
	    	+ ") AS `savedValues` ON `savedValues`.`id`=`bbdata`.`id` WHERE `savedValues`.`id` IS NULL";
	    if (deletesPerCleansing > 0) {
	        cleansql += " LIMIT " + deletesPerCleansing;
	    }
	    cleansql += ";";
    	return cleansql;
	}


}

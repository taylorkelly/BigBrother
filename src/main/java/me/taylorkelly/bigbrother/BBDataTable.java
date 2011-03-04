
package me.taylorkelly.bigbrother;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

public class BBDataTable {
    public final static String BBDATA_NAME = "bbdata";
    private final static String BBDATA_TABLE_SQLITE = "CREATE TABLE `bbdata` (" + "`id` INTEGER PRIMARY KEY," + "`date` INT UNSIGNED NOT NULL DEFAULT '0',"
            + "`player` varchar(32) NOT NULL DEFAULT 'Player'," + "`action` tinyint NOT NULL DEFAULT '0'," + "`world` tinyint NOT NULL DEFAULT '0',"
            + "`x` int NOT NULL DEFAULT '0'," + "`y` tinyint UNSIGNED NOT NULL DEFAULT '0'," + "`z` int NOT NULL DEFAULT '0',"
            + "`type` smallint NOT NULL DEFAULT '0'," + "`data` varchar(500) NOT NULL DEFAULT ''," + "`rbacked` boolean NOT NULL DEFAULT '0'" + ");"
            + "CREATE INDEX dateIndex on bbdata (date);" + "CREATE INDEX playerIndex on bbdata (player);" + "CREATE INDEX actionIndex on bbdata (action);"
            + "CREATE INDEX worldIndex on bbdata (world);" + "CREATE INDEX xIndex on bbdata (x);" + "CREATE INDEX yIndex on bbdata (y);"
            + "CREATE INDEX zIndex on bbdata (z);" + "CREATE INDEX typeIndex on bbdata (type);" + "CREATE INDEX rbackedIndex on bbdata (rbacked);";
    
    // Switched to MyISAM so we use an engine that keeps COUNT(*) cached.
    // We also don't need transactions.
    // Also made a compound index on x,y,z, as suggested by Cuddlefish.
    public static String BBDATA_TABLE_MYSQL = "CREATE TABLE `bbdata` (" + "`id` INT NOT NULL AUTO_INCREMENT,"
            + "`date` INT UNSIGNED NOT NULL DEFAULT '0'," + "`player` varchar(32) NOT NULL DEFAULT 'Player'," + "`action` tinyint NOT NULL DEFAULT '0',"
            + "`world` tinyint NOT NULL DEFAULT '0'," + "`x` int NOT NULL DEFAULT '0'," + "`y` tinyint UNSIGNED NOT NULL DEFAULT '0',"
            + "`z` int NOT NULL DEFAULT '0'," + "`type` smallint NOT NULL DEFAULT '0'," + "`data` varchar(500) NOT NULL DEFAULT '',"
            + "`rbacked` boolean NOT NULL DEFAULT '0'," + "PRIMARY KEY (`id`)," + "INDEX(`world`)," + "INDEX(`x`,`y`,`z`),"
            + "INDEX(`player`(10))," + "INDEX(`action`)," + "INDEX(`date`)," + "INDEX(`type`)," + "INDEX(`rbacked`)" + ") ENGINE=MyISAM";
    
    // GoMySQL suggested partitioning.  Currently, I like the following modifier, but I'm unsure of support in prior versions of MySQL, or on MyISAM.
    //PARTITION BY LINEAR KEY(date) PARTITIONS 12;


    public static void initialize() {
        if (!bbdataTableExists(!BBSettings.mysql)) {
            BBLogging.info("Building `bbdata` table...");
            createBBDataTable(!BBSettings.mysql);
        }
        if(BBSettings.mysql) {
        	// MyISAM supports caching record COUNT(*), so we don't have to wait
        	// through the initial COUNT(*) of records that BB does.
        	// OPTIONAL, since this could take hours on larger databases. Throw a warning, though.
        	checkDBEngine(BBDATA_NAME,"MyISAM",true);
        }
    }

    /**
     * Ensure we're using the right storage engine.
     * @param tableName
     * @param requiredEngine
     */
    private static void checkDBEngine(String tableName, String requiredEngine, boolean optional) {
    	String engine = getEngine(tableName);
    	if(engine==null) return; // Error.
        if(engine!=requiredEngine) {
        	if(!optional) {
	        	BBLogging.warning("Changing "+tableName+" so that it uses "+requiredEngine+" instead of "+engine+". THIS MAY TAKE A WHILE!");
	        	setEngine(tableName,requiredEngine);
        	} else {
        		BBLogging.warning("Table "+tableName+" uses the MySQL storage engine "+engine+".");
        		BBLogging.info("For optimal performance, we suggest changing to "+requiredEngine+" via the following SQL statement:");
        		BBLogging.info("  ALTER TABLE "+tableName+" ENGINE = "+engine+";");
        		BBLogging.info("Please note that, on many tables, this could take a very long time.");
        	}
        }
	}

	private static void setEngine(String tableName, String engine) {
		Connection conn = null;
        Statement st = null;
        try {
            conn = ConnectionManager.getConnection();
            st = conn.createStatement();
            st.executeUpdate("ALTER TABLE "+tableName+" ENGINE = "+engine);
            conn.commit();
        } catch (SQLException e) {
            BBLogging.severe("Altering "+tableName+" to use "+engine+" triggered an exception.", e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                BBLogging.severe("Could not create the table (on close)");
            }
        }
	}

	private static String getEngine(String tableName) {
		Connection conn=null;
        ResultSet rs=null;
        Statement stmt=null;
        String engine=null;
        try {
        	conn=ConnectionManager.getConnection();
        	stmt=conn.createStatement();
        	if(!stmt.execute("SHOW TABLE STATUS WHERE Name = '"+tableName+"'"))
        	{
        		BBLogging.severe("Could not fetch table information for table "+tableName);
        		return null;
        	}
        	rs=stmt.getResultSet();
        	rs.first();
        	engine=rs.getString("Engine");
        } catch (SQLException e) {
			BBLogging.severe("Could not retreive table information.",e);
        } finally {
        	try {
        		if (rs != null) {
        			rs.close();
        		}
        		if (conn != null) {
        			conn.close();
        		}
        	} catch (SQLException e) {
    			BBLogging.severe("Could not retreive table information (On close).",e);
        	}
        }
        return engine;
	}

	private static boolean bbdataTableExists(boolean sqlite) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, BBDATA_NAME, null);
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (SQLException ex) {
            BBLogging.severe("Table Check SQL Exception" + ((sqlite) ? " sqlite" : " mysql"), ex);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BBLogging.severe("Table Check SQL Exception (on closing)");
            }
        }
    }

    private static void createBBDataTable(boolean sqlite) {
        Connection conn = null;
        Statement st = null;
        try {
            conn = ConnectionManager.getConnection();
            st = conn.createStatement();
            if (sqlite) {
                st.executeUpdate(BBDATA_TABLE_SQLITE);
            } else {
                st.executeUpdate(BBDATA_TABLE_MYSQL);
            }
            conn.commit();
        } catch (SQLException e) {
            BBLogging.severe("Create Table SQL Exception" + ((sqlite) ? " sqlite" : " mysql"), e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                BBLogging.severe("Could not create the table (on close)");
            }
        }
    }

}

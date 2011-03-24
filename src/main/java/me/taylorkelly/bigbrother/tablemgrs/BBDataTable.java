package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.SQLException;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;

/**
 * Handler class for the bbdata table
 *
 * @author tkelly
 * @todo Handle INSERT/UPDATE/DELETEs through here
 */
public abstract class BBDataTable extends DBTable {

    // Singletons :D
    private static BBDataTable instance=null;

    /**
     * Get table name + prefix
     */
    protected String getTableName() 
    {
        return "bbdata";
    }
    
    public static BBDataTable getInstance() {
        if(instance==null) {
            //BBLogging.info("BBSettings.databaseSystem="+BBSettings.databaseSystem.toString());
            if(BBSettings.usingDBMS(DBMS.MYSQL))
                instance=new BBDataMySQL();
            else
                instance=new BBDataSQLite();
        }
        return instance;
    }
    
    public BBDataTable() {
        if (!tableExists()) {
            BBLogging.info("Building `"+getRealTableName()+"` table...");
            createTable();
        } else {
            BBLogging.debug("`"+getRealTableName()+"` table already exists");

        }
        
        onLoad();
    }
    
    public String getPreparedDataBlockStatement(Connection conn) throws SQLException {
        return "INSERT INTO " + getRealTableName()
                + " (date, player, action, world, x, y, z, type, data, rbacked) VALUES (?,?,?,?,?,?,?,?,?,0)";
    }
}

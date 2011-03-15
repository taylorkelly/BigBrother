package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

/**
 * Handler class for the bbdata table
 *
 * @author tkelly
 */
public abstract class BBDataTable {

    // Singletons :D
    private static BBDataTable instance=null;

    /**
     * Get table name + prefix
     */
    public static String getTableName() 
    {
        return BBSettings.mysqlPrefix+"bbdata";
    }
    
    public static BBDataTable getInstance() {
        if(instance==null) {
            BBLogging.info("BBSettings.databaseSystem="+BBSettings.databaseSystem.toString());
            if(BBSettings.usingDBMS(DBMS.MYSQL))
                instance=new BBDataMySQL();
            else
                instance=new BBDataSQLite();
        }
        return instance;
    }
    
    public BBDataTable() {
        if (!bbdataTableExists()) {
            BBLogging.info("Building `"+getTableName()+"` table...");
            createBBDataTable();
        } else {
            BBLogging.debug("`"+getTableName()+"` table already exists");

        }
        
        onLoad();
    }
    
    /**
     * Specify your on-load procedures in here.
     */
    public abstract void onLoad();
    
    /**
     * Specify the CREATE TABLE syntax here.
     * @return
     */
    public abstract String getCreateSyntax();

    

    private boolean bbdataTableExists() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, getTableName(), null);
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (SQLException ex) {
            BBLogging.severe("Couldn't check if "+getTableName()+" exists!", ex);
            return false;
        } finally {
            ConnectionManager.cleanup( "Table Check",  conn, null, rs );
        }
    }

    private void createBBDataTable() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = ConnectionManager.getConnection();
            st = conn.createStatement();
            st.executeUpdate(getCreateSyntax());
            conn.commit();
        } catch (SQLException e) {
            BBLogging.severe("Can't create the "+getTableName()+" table", e);
        } finally {
            ConnectionManager.cleanup( "Create Table",  conn, st, null );
        }
    }

    public PreparedStatement getPreparedDataBlockStatement(Connection conn) throws SQLException {
        return conn.prepareStatement("INSERT INTO " + BBDataTable.getTableName()
                + " (date, player, action, world, x, y, z, type, data, rbacked) VALUES (?,?,?,?,?,?,?,?,?,0)");
    }
}

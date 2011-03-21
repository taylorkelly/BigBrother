package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

public abstract class DBTable {

    /**
     * Get the name of the table, minus prefix.
     * @return
     */
    protected abstract String getTableName();
    
    /**
     * Specify your on-load procedures in here.
     */
    protected abstract void onLoad();
    
    /**
     * Specify the CREATE TABLE syntax here.
     * @return
     */
    public abstract String getCreateSyntax();
    
    public String getRealTableName() {
        return BBSettings.mysqlPrefix+getTableName();
    }
    
    public boolean tableExists() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, getRealTableName(), null);
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (SQLException ex) {
            BBLogging.severe("Couldn't check if "+getRealTableName()+" exists!", ex);
            return false;
        } finally {
            ConnectionManager.cleanup( "Table Check",  conn, null, rs );
        }
    }

    protected void createTable() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = ConnectionManager.getConnection();
            st = conn.createStatement();
            st.executeUpdate(getCreateSyntax());
            conn.commit();
        } catch (SQLException e) {
            BBLogging.severe("Can't create the "+getRealTableName()+" table", e);
        } finally {
            ConnectionManager.cleanup( "Create Table",  conn, st, null );
        }
    }

}

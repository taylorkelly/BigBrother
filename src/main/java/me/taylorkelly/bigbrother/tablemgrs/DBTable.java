package me.taylorkelly.bigbrother.tablemgrs;

import java.io.File;
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

public abstract class DBTable {

    /**
     * Get the name of the table, minus prefix.
     * @return
     */
    protected abstract String getActualTableName();
    
    /**
     * Specify your on-load procedures in here.
     */
    protected abstract void onLoad();
    
    /**
     * Specify the CREATE TABLE syntax here.
     * @return
     */
    public abstract String getCreateSyntax();
    
    public String getTableName() {
        return BBSettings.applyPrefix(getActualTableName());
    }
    
    public boolean tableExists() {
        // Workaround for H2 wonkiness regarding table metadata.
        if(BBSettings.usingDBMS(DBMS.H2))
        {
            File f = new File("plugins/BigBrother/bigbrother.h2.db");
            return f.exists();
        }
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

    protected void createTable() {
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

    protected boolean executeUpdate(String desc, String sql, Object[] args) {
        BBLogging.debug(sql);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            for(int i = 1;i<args.length+1;i++)
                ps.setObject(i, args[i]);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            BBLogging.severe("Could not executeUpdate for "+desc+":", e);
        } finally {
            ConnectionManager.cleanup(desc,conn, ps, null );
        }
        return false;
    }
    


    protected boolean executeUpdate(String desc, String sql) {
        return executeUpdate(desc,sql,new Object[]{});
    }
}

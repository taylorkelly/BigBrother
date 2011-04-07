package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

public abstract class BBWorldsTable extends DBTable {
	
	// Singletons :D
    private static BBWorldsTable instance=null;

	@Override
	protected String getActualTableName() {
		return "bbworlds";
	}
	
    public static BBWorldsTable getInstance() {
        if(instance==null) {
            BBLogging.debug("BBSettings.databaseSystem="+BBSettings.databaseSystem.toString());
            if(BBSettings.usingDBMS(DBMS.MYSQL))
                instance=new BBWorldsMySQL();
            else if(BBSettings.usingDBMS(DBMS.POSTGRES))
                instance=new BBWorldsPostgreSQL();
            else
                instance=new BBWorldsH2();
        }
        return instance;
    }

	
	
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE "+getTableName()+" ("
            + "id INTEGER PRIMARY KEY,"
            + "name varchar(50) NOT NULL DEFAULT 'world');";
    }

	public HashMap<String, Integer> getWorlds() {
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
        Connection conn = null;
        Statement statement = null;
        ResultSet set = null;
        try {
            conn = ConnectionManager.getConnection();
            if(conn!=null) { 
                
                statement = conn.createStatement();
                set = statement.executeQuery(getSelectWorldsQuery());
                int size = 0;
                while (set.next()) {
                    size++;
                    int index = set.getInt("id");
                    String name = set.getString("name");
                    ret.put(name, index);
                }
            }
        } catch (SQLException ex) {
            BBLogging.severe("World Load Exception", ex);
        } finally {
            ConnectionManager.cleanup( "World Load",  conn, statement, set );
        }

        BBLogging.debug("Loaded worlds: " + ret.keySet().toString());
        return ret;
	}

	protected abstract String getSelectWorldsQuery();

	public boolean insertWorld(int index, String world) {
		Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            if(conn==null) return false;
            ps = conn.prepareStatement(getInsertWorldQuery());
            ps.setInt(1, index);
            ps.setString(2, world);
            ps.executeUpdate();
            conn.commit();
            return true;
        } catch (SQLException ex) {
            BBLogging.severe("World Insert Exception", ex);
            return false;
        } finally {
            ConnectionManager.cleanup( "World Insert",  conn, ps, null );
        }
	}

	protected abstract String getInsertWorldQuery();

}

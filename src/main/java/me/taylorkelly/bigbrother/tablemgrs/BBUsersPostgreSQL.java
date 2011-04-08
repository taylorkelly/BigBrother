package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;


public class BBUsersPostgreSQL extends BBUsersMySQL {
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
    	return "CREATE TABLE \""+getTableName()+"\" ("
			+ "\"id\" SERIAL,"
			+ "\"name\" varchar(32) NOT NULL DEFAULT 'Player',\"flags\" INT NOT NULL DEFAULT '0',"
			+ "PRIMARY KEY (\"id\"),"
			+ "UNIQUE(\"name\"));";
    }
    
    /**
     * @see BBUsersMySQL
     * changed quotes
     */
    @Override
    public BBPlayerInfo getUserFromDB(String name) {

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            if(conn==null) return null;
            String sql = "SELECT id,name,flags FROM "+getTableName()+" WHERE \"name\"=?";
            BBLogging.debug(sql);
            ps = conn.prepareStatement(sql);
            ps.setString(1,name);
            rs=ps.executeQuery();
            conn.commit();
            
            if(!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Error trying to find the user `"+name+"`.", e);
        } finally {
            ConnectionManager.cleanup( "BBUsersMySQL.getUserFromDB(string)",conn, ps, rs );
        }
        return null;
    }
    
    @Override
    public BBPlayerInfo getUserFromDB(int id) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT id,name,flags FROM " + getTableName() + " WHERE \"id\"=?;";
            conn = ConnectionManager.getConnection();
            if(conn==null) return null;
            BBLogging.debug(sql);
            ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            rs=ps.executeQuery();
            conn.commit();
            if(!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Can't find user #"+id+".", e);
        } finally {
            ConnectionManager.cleanup( "BBUsersMySQL.getUserFromDB(int)",conn, ps, rs );
        }
        return null;
    }
    
    @Override
    protected void do_addOrUpdatePlayer(BBPlayerInfo pi) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            if(conn==null) return;
            if(pi.getNew() && getUserFromDB(pi.getName())==null) {
            	/*String statement = "IF EXISTS (SELECT \"name\" FROM \""+getTableName()+"\" WHERE \"name\" = ?) THEN"
            		+ " UPDATE \""+getTableName()+"\" SET \"flags\" = ? WHERE \"name\" = ?;"
            		+ " ELSE "
            		+ " INSERT INTO \""+getTableName()+"\" (name,flags) VALUES(?,?)"
            		+ " END IF;";
            	ps = conn.prepareStatement(statement);*/
            	ps = conn.prepareStatement("INSERT INTO "+getTableName()+" (name,flags) VALUES (?,?)");
                
                
                ps.setString(1,pi.getName());
                ps.setInt(2,pi.getFlags());
                
            } else {
                ps = conn.prepareStatement("UPDATE "+getTableName()+" SET flags = ? WHERE id=?");
                ps.setInt(1, pi.getFlags());
                ps.setInt(2, pi.getID());
            }
            BBLogging.debug(ps.toString());
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            BBLogging.severe("Can't update the user `"+pi.getName()+"`.", e);
        } finally {
            ConnectionManager.cleanup( "BBUsersMySQL.do_addOrUpdatePlayer",conn, ps, null );
        }
    }
    
    // FIXME: super.importRecords() will not work
}

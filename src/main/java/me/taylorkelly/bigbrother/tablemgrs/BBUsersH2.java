package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

public class BBUsersH2 extends BBUsersTable {
    public final int revision = 1;
    public String toString() {
        return "BBUsers H2 Driver r"+Integer.valueOf(revision);
    }
    
    @Override
    protected void onLoad() {
    }
    
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE `" + getTableName() + "` (" 
        + "`id` INT PRIMARY KEY," 
        + "`name` varchar(32) NOT NULL DEFAULT 'Player'," 
        + "`flags` INT NOT NULL DEFAULT '0');" 
        + "CREATE UNIQUE INDEX idxUsername ON `" + getTableName() + "` (`name`)"; // ANSI
    }

    @Override
    public BBPlayerInfo getUserFromDB(String name) {

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = "SELECT id,name,flags FROM "+getTableName()+" WHERE `name`=?";
            BBLogging.debug(sql);
            ps = conn.prepareStatement(sql);
            ps.setString(1,name.toLowerCase());
            rs=ps.executeQuery();
            
            if(!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Error trying to find the user `"+name+"`.", e);
        } finally {
            ConnectionManager.cleanup( "BBUsersH2.getUserFromDB(string)",conn, ps, rs );
        }
        return null;
    }

    @Override
    protected void do_addOrUpdatePlayer(BBPlayerInfo pi) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            if(pi.getNew()) {
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
        } catch (SQLException e) {
            BBLogging.severe("Can't update the user `"+pi.getName()+"`.", e);
        } finally {
            ConnectionManager.cleanup( "BBUsersH2.do_addOrUpdatePlayer",conn, ps, null );
        }
    }

    @Override
    public BBPlayerInfo getUserFromDB(int id) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT id,name,flags FROM " + getTableName() + " WHERE `id`=?";
            conn = ConnectionManager.getConnection();
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
            ConnectionManager.cleanup( "BBUsersH2.getUserFromDB(int)",conn, ps, rs );
        }
        return null;
    }

    


    @Override
    protected void loadCache() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = "SELECT id,name,flags FROM "+getTableName();
            BBLogging.debug(sql);
            ps = conn.prepareStatement(sql);
            rs=ps.executeQuery();
            
            while(rs.next()){
                BBPlayerInfo pi = new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
                this.knownPlayers.put(pi.getID(), pi);
                this.knownNames.put(pi.getName(),pi.getID());
            }
        } catch (SQLException e) {
            BBLogging.severe("Error trying to load the user/name cache.", e);
        } finally {
            ConnectionManager.cleanup( "BBUsersH2.getUserFromDB(string)",conn, ps, rs );
        }
    }

    @Override
    public void importRecords() {
    }
    
}

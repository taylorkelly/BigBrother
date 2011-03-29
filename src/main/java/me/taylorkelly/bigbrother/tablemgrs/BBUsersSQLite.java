package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

public class BBUsersSQLite extends BBUsersTable {
    
    @Override
    protected BBPlayerInfo getUserFromDB(String name) {
        
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("SELECT id,name,flags FROM " + getTableName() + " WHERE LOWER(`name`)=LOWER(?);");
            ps.setString(0, name);
            rs = ps.executeQuery();
            
            if (!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt(0), rs.getString(1), rs.getInt(2));
            
        } catch (SQLException e) {
            BBLogging.severe("Can't find the user `" + name + "`.", e);
        } finally {
            ConnectionManager.cleanup("BBUsersSQLite.getUserFromDB(string)", conn, ps, rs);
        }
        return null;
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
    protected void do_addOrUpdatePlayer(BBPlayerInfo pi) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            if (pi.getNew()) {
                ps = conn.prepareStatement("INSERT INTO " + getTableName() + " (name,flags) VALUES (?,?)");
                ps.setString(0, pi.getName());
                ps.setInt(1, pi.getFlags());
            } else {
                ps = conn.prepareStatement("UPDATE " + getTableName() + " SET flags = ? WHERE id=?");
                ps.setInt(0, pi.getFlags());
                ps.setInt(1, pi.getID());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            BBLogging.severe("Can't update the user `" + pi.getName() + "`.", e);
        } finally {
            ConnectionManager.cleanup("BBUsersSQLite.do_addOrUpdatePlayer", conn, ps, null);
        }
    }
    
    @Override
    protected BBPlayerInfo getUserFromDB(int id) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("SELECT id,name,flags FROM " + getTableName() + " WHERE `id`=?;");
            ps.setInt(0, id);
            rs = ps.executeQuery();
            
            if (!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt(0), rs.getString(1), rs.getInt(2));
            
        } catch (SQLException e) {
            BBLogging.severe("Can't find user #" + id + ".", e);
        } finally {
            ConnectionManager.cleanup("BBUsersSQLite.getUserFromDB(int)", conn, ps, rs);
        }
        return null;
    }
    
    /**
     * SQLite is why you don't do drugs, kids.
     */
    @Override
    public void importRecords() {
        BBLogging.info("Importing users into new table!");
        
        BBLogging.info(" * Stage 1/8: Create temporary table");
        if (!executeUpdate("importRecords(sqlite) - Create temporary table", "CREATE TEMPORARY TABLE " + BBSettings.applyPrefix("bbdata_backup") + "(id,date,player,action,world,x,y,z,type,data,rbacked);"))
            return;
        
        BBLogging.info(" * Stage 2/8: Make backup.");
        if (!executeUpdate("importRecords(sqlite) - Make backup table", "INSERT INTO " + BBSettings.applyPrefix("bbdata_backup") + " SELECT id,date,player,action,world,x,y,z,type,data,rbacked FROM " + BBSettings.applyPrefix("bbdata") + ";"))
            return;
        
        BBLogging.info(" * Stage 3/8: DROP old table.");
        if (!executeUpdate("importRecords(sqlite) - Drop old table", "DROP TABLE " + BBSettings.applyPrefix("bbdata") + ";"))
            return;
        
        BBLogging.info(" * Stage 4/8: Creating new bbdata table.");
        if (!executeUpdate("importRecords(sqlite) - Create bbdata", 
                "CREATE TABLE `" + getTableName() 
                + "` (" 
                + "`id` INTEGER PRIMARY KEY," 
                + "`date` INT UNSIGNED NOT NULL DEFAULT '0'," 
                + "`player` INT UNSIGNED NOT NULL DEFAULT '0'," 
                + "`action` tinyint NOT NULL DEFAULT '0'," 
                + "`world` tinyint NOT NULL DEFAULT '0'," 
                + "`x` int NOT NULL DEFAULT '0'," 
                + "`y` tinyint UNSIGNED NOT NULL DEFAULT '0'," 
                + "`z` int NOT NULL DEFAULT '0'," 
                + "`type` smallint NOT NULL DEFAULT '0'," 
                + "`data` varchar(500) NOT NULL DEFAULT ''," 
                + "`rbacked` boolean NOT NULL DEFAULT '0'" 
                + ");" 
                + "CREATE INDEX dateIndex on bbdata (date);" 
                + "CREATE INDEX playerIndex on bbdata (player);" 
                + "CREATE INDEX actionIndex on bbdata (action);" 
                + "CREATE INDEX worldIndex on bbdata (world);" 
                + "CREATE INDEX posIndex on bbdata (x,y,z);" 
                + "CREATE INDEX typeIndex on bbdata (type);" 
                + "CREATE INDEX rbackedIndex on bbdata (rbacked);"))
            return;
        
        BBLogging.info(" * Stage 5/8: Creating new bbusers table.");
        if (!executeUpdate("importRecords(sqlite) - Create bbusers", getCreateSyntax()))
            return;
        
        BBLogging.info(" * Stage 6/8: Importing users");
        if (!executeUpdate("importRecords(sqlite) - Import users", "INSERT INTO " + BBSettings.applyPrefix("bbusers") + " SELECT player AS name FROM " + BBSettings.applyPrefix("bbdata_backup") + ";"))
            return;
        
        BBLogging.info(" * Stage 7/8: Importing old records and user IDs");
        if (!executeUpdate("importRecords(sqlite) - Import data", "INSERT INTO " + BBSettings.applyPrefix("bbdata") + " SELECT dat.id, date, ply.id, action, world, x, y, z, type, data, rbacked AS name FROM " + BBSettings.applyPrefix("bbdata_backup") + " AS dat INNER JOIN " + BBSettings.applyPrefix("bbusers") + " as ply;"))
            return;
        
        BBLogging.info(" * Stage 8/8: Drop backup.");
        if (!executeUpdate("importRecords(sqlite) - Drop bbdata_backup", "DROP TABLE " + BBSettings.applyPrefix("bbdata_backup") + ";"))
            return;
        
    }
    
}

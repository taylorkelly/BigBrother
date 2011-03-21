/**
 * 
 */
package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

/**
 * @author Rob
 *
 */
public class BBUsersMySQL extends BBUsersTable {
    public final int revision = 1;
    public String toString() {
        return "BBUsers MySQL Driver r"+Integer.valueOf(revision);
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#onLoad()
     */
    @Override
    protected void onLoad() {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE `"+getRealTableName()+"` ("
        + "`id` INT NOT NULL AUTO_INCREMENT," 
        + "`name` varchar(32) NOT NULL DEFAULT 'Player'," 
        + "`flags` INT NOT NULL DEFAULT '0',"
        + "PRIMARY KEY (`id`));" //Engine doesn't matter, really.
        + "CREATE UNIQUE INDEX idxUsername ON `name`;";
    }

    @Override
    protected BBPlayerInfo getUserFromDB(String name) {

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("SELECT id,name,flags FROM "+getRealTableName()+" WHERE LOWER(`name`)=LOWER(?);");
            ps.setString(0,name);
            rs=ps.executeQuery();
            
            if(!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt(0),rs.getString(1),rs.getInt(2));
            
        } catch (SQLException e) {
            BBLogging.severe("Can't find the user `"+name+"`.", e);
        } finally {
            ConnectionManager.cleanup( "BBUsersMySQL.getUserFromDB",conn, ps, rs );
        }
        return null;
    }
    
}

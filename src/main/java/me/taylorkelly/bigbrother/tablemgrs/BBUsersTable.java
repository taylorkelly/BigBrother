package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Hashtable;

import org.bukkit.entity.Player;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;

/**
 * Handle the user tracking table.
 * BBUsers(_id_,name,flags)
 * @author Rob
 * @todo Handle INSERT/SELECT/DELETE stuff through here.
 */
public abstract class BBUsersTable extends DBTable {
    
    public Hashtable<String,BBPlayerInfo> knownPlayers = new Hashtable<String,BBPlayerInfo>();
    
    
    // Singletons :D
    private static BBUsersTable instance=null;
    
    /**
     * Get table name + prefix
     */
    public String getTableName() 
    {
        return "bbdata";
    }
    
    public static BBUsersTable getInstance() {
        if(instance==null) {
            //BBLogging.info("BBSettings.databaseSystem="+BBSettings.databaseSystem.toString());
            if(BBSettings.usingDBMS(DBMS.MYSQL))
                instance=new BBUsersMySQL();
            else
                instance=new BBUsersSQLite();
        }
        return instance;
    }
    
    public BBUsersTable() {
        if (!tableExists()) {
            BBLogging.info("Building `"+getTableName()+"` table...");
            createTable();
        } else {
            BBLogging.debug("`"+getTableName()+"` table already exists");

        }
        
        onLoad();
    }
    
    public BBPlayerInfo getUser(String name) {
        name=name.toLowerCase();
        
        // Check cache first.
        if(knownPlayers.containsKey(name))
            return knownPlayers.get(name);
        
        return getUserFromDB(name);
    }
    
    public void addOrUpdateUser(Player p) {
        String name=p.getName().toLowerCase();
        
        BBPlayerInfo pi = null;
        // Check cache first.
        if(knownPlayers.containsKey(name))
        {
            pi = knownPlayers.get(name);
            knownPlayers.remove(name);
        } else {
            pi = new BBPlayerInfo(name);
        }
        
        do_addOrUpdatePlayer(pi);
    }

    protected abstract void do_addOrUpdatePlayer(BBPlayerInfo pi);

    /**
     * Get user from the database based on name.
     * @param name
     * @return
     */
    protected abstract BBPlayerInfo getUserFromDB(String name);
}

package me.taylorkelly.bigbrother.tablemgrs;

import java.util.Hashtable;

import org.bukkit.entity.Player;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;

/**
 * Handle the user tracking table.
 * BBUsers(_id_,name,flags)
 * @author N3X15
 * @todo Handle INSERT/SELECT/DELETE stuff through here.
 */
public abstract class BBUsersTable extends DBTable {
    
    public Hashtable<Integer,BBPlayerInfo> knownPlayers = new Hashtable<Integer,BBPlayerInfo>();
    public Hashtable<String,Integer> knownNames = new Hashtable<String,Integer>();
    
    // Singletons :D
    private static BBUsersTable instance=null;
    
    /**
     * Get table name
     */
    public String getActualTableName() 
    {
        return "bbusers";
    }
    public static BBUsersTable getInstance() {
        if(instance==null) {
            BBLogging.debug("BBSettings.databaseSystem="+BBSettings.databaseSystem.toString());
            if(BBSettings.usingDBMS(DBMS.MYSQL))
                instance=new BBUsersMySQL();
            else
                instance=new BBUsersH2();
            instance.loadCache();
        }
        return instance;
    }
    
    protected abstract void loadCache();
    
    public BBUsersTable() {
        if (!tableExists()) {
            BBLogging.info("Building `"+getTableName()+"` table...");
            createTable();
        } else {
            BBLogging.debug("`"+getTableName()+"` table already exists");

        }
        
        onLoad();
    }
    
    public abstract boolean importRecords();

    public BBPlayerInfo getUser(String name) {
        name=name.toLowerCase();
        
        // Check cache first.
        if(knownNames.containsKey(name))
            return getUser(knownNames.get(name));

        BBPlayerInfo pi = getUserFromDB(name);
        if(pi==null) {
            pi=new BBPlayerInfo(name);
        }
        return pi;
    }
    
    public void addOrUpdateUser(Player p) {
        String name=p.getName().toLowerCase();
        
        BBPlayerInfo pi = null;
        // Check cache first.
        if(knownNames.containsKey(name))
        {
            int id = knownNames.get(name);
            pi = knownPlayers.get(id);
            knownPlayers.remove(id);
            knownNames.remove(name);
            pi.setNew(false); // If we're getting it from cache, it ain't new.
        } else {
            pi = new BBPlayerInfo(name);
        }
        
        do_addOrUpdatePlayer(pi);
        pi.refresh();
        knownPlayers.put(pi.getID(), pi);
        knownNames.put(name, pi.getID());
    }

    public void addOrUpdatePlayer(BBPlayerInfo pi) {
        // Update cache
        if(pi.getID()!=-1)
            if(knownPlayers.containsKey(pi.getID()))
            {
                pi = knownPlayers.get(pi.getID());
                knownPlayers.remove(pi.getID());
            }
        
        do_addOrUpdatePlayer(pi);
        knownPlayers.put(pi.getID(), pi);
    }

    /**
     * UPDATE or INSERT user.
     * @param pi
     */
    protected abstract void do_addOrUpdatePlayer(BBPlayerInfo pi);

    /**
     * Get user from the database based on name.
     * @param name
     * @return
     */
    public abstract BBPlayerInfo getUserFromDB(String name);

    public abstract BBPlayerInfo getUserFromDB(int id);

    public BBPlayerInfo getUser(int id) {
        if(!knownPlayers.containsKey(id))
            return knownPlayers.get(id);
        return this.getUserFromDB(id);
    }
}

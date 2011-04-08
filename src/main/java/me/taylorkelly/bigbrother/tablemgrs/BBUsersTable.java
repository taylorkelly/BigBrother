package me.taylorkelly.bigbrother.tablemgrs;

import java.util.Hashtable;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;

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
            else if(BBSettings.usingDBMS(DBMS.POSTGRES))
                instance=new BBUsersPostgreSQL();
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

    public BBPlayerInfo getUserByName(String name) {
        if(name.equalsIgnoreCase(BBDataBlock.ENVIRONMENT))
            return BBPlayerInfo.ENVIRONMENT;
        
        // Check cache first.
        if(knownNames.containsKey(name))
            return getUserByID(knownNames.get(name));

        BBPlayerInfo pi = getUserFromDB(name);
        if(pi==null) {
            pi=new BBPlayerInfo(name);
        }
        knownPlayers.put(pi.getID(), pi);
        knownNames.put(pi.getName(), pi.getID());
        return pi;
    }
    
    public void addOrUpdateUser(Player p) {
        String name=p.getName();
        
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
        knownNames.put(pi.getName(), pi.getID());
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

    public BBPlayerInfo getUserByID(int id) {
        if(knownPlayers.containsKey(id))
            return knownPlayers.get(id);
        BBPlayerInfo pi= this.getUserFromDB(id);
        if(pi!=null) {
            knownPlayers.put(pi.getID(), pi);
            knownNames.put(pi.getName(), pi.getID());
        }
        return pi;
    }
    public void userOpenedChest(String player, Chest c, ItemStack[] contents) {
        BBPlayerInfo pi = getUserByName(player);
        pi.setHasOpenedChest(c,contents);
        knownPlayers.put(pi.getID(),pi);
    }
}

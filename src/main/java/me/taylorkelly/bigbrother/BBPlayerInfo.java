/**
 * 
 */
package me.taylorkelly.bigbrother;

import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 * @author N3X15
 * 
 */
public class BBPlayerInfo {
    enum PlayerField {
        WATCHED
    }

    public static BBPlayerInfo ENVIRONMENT;
    
    /**
     * New guy? (INSERT instead of UPDATE)
     */
    private boolean isNew = true;
    
    /**
     * Are we waiting for this guy to do something after he opens a chest? (Workaround for lack of inventory update events)
     */
    private ItemStack[] chestContents=null;
    
    private String name = "";
    private int flags = 0; // bitfield flags
    private int id = -1;

    private Chest myOpenChest=null;
    
    /**
     * For caching a new player.
     * 
     * @param name
     */
    public BBPlayerInfo(String name) {
        this.name = name;
        setNew(true); // Only really used to determine if we need to INSERT or
                      // UPDATE.
        if (BBSettings.autoWatch)
            setWatched(true);

        BBUsersTable.getInstance().addOrUpdatePlayer(this);
        refresh(); // Get ID#
        setNew(false);
        BBLogging.debug("New user: "+name+" -> #"+id);
    }
    
    /**
     * For bringing in a user from the database.
     * @param id
     * @param name
     * @param flags
     */
    public BBPlayerInfo(int id,String name,int flags) {
        this.id=id;
        this.name=name;
        this.flags=flags;
    }
    
    private void setFlag(PlayerField fld, boolean on) {
        if (!on)
            flags &= ~(1 << fld.ordinal());
        else
            flags |= (1 << fld.ordinal());
        if(id!=-1) {
            BBUsersTable.getInstance().addOrUpdatePlayer(this);
        }
    }
    
    /**
     * Reload from the database.
     */
    public void refresh() {
        BBPlayerInfo clone;
        BBLogging.debug("BBPlayerInfo.refresh(): "+name+"#"+Integer.valueOf(id));
        if(id>-1)
            clone = BBUsersTable.getInstance().getUserFromDB(id);
        else
            clone = BBUsersTable.getInstance().getUserFromDB(name); 
        this.id=clone.id;
        this.flags=clone.flags;
        this.name=clone.name;
    }

    private boolean getFlag(PlayerField fld) {
        int f = (1 << fld.ordinal());
        return (flags & f) == f;
    }
    
    public int getID() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getFlags() {
        return flags;
    }
    
    /**
     * Used for tracking whether a user is new to the database or not.
     * @param b
     */
    public void setNew(boolean b) {
        isNew=b;
    }
    
    /**
     * 
     * @return
     */
    public boolean getNew() {
        return isNew;
    }
    
    /**
     * @param b
     */
    public void setWatched(boolean b) {
        setFlag(PlayerField.WATCHED, true);
    }
    
    /**
     * Are we tracking this user?
     * @return
     */
    public boolean getWatched() {
        return getFlag(PlayerField.WATCHED);
    }
    
    /**
     * Set true when user has opened a chest.
     * Set false when they move/do stuff that can only be done outside of inventory.
     * @param b
     */
    public void setHasOpenedChest(Chest c,ItemStack[] contents) {
        myOpenChest=c;
        chestContents=contents;
    }
    
    /**
     * True if the user is most likely messing around with their chest inventory.
     * @return
     */
    public boolean hasOpenedChest() {
        return chestContents!=null;
    }
    
    /**
     * Format username, colorize if necessary
     */
    public String toString() {
        String player=this.getName();
        /* TODO: Future consideration, working to get this hunk of bugs out the door atm. - N3X
        if(BBSettings.colorPlayerNames) {
            player=BBPermissions.getPrefix(player)+player+BBPermissions.getSuffix(player);
        }
        */
        return player;
    }

    public ItemStack[] getOldChestContents() {
        if(chestContents==null) {
            BBLogging.severe("getOldChestContents is about to return a null.  Please report this.");
        }
        return chestContents;
    }

    public Chest getOpenedChest() {
        return myOpenChest;
    }
}

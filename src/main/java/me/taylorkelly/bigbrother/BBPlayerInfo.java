/**
 * 
 */
package me.taylorkelly.bigbrother;

/**
 * @author Rob
 * 
 */
public class BBPlayerInfo {
    enum PlayerField {
        WATCHED, NEW
    }
    
    private String name = "";
    private int flags = 0; // bitfield flags
    private int id = 0;
    
    /**
     * For caching a new player.
     * 
     * @param name
     */
    public BBPlayerInfo(String name) {
        this.name = name;
        if (BBSettings.autoWatch)
            setWatched(true);
        setNew(true); // Only really used to determine if we need to INSERT or
                      // UPDATE.
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
    
    private void setNew(boolean b) {
        setFlag(PlayerField.NEW, true);
    }
    
    public boolean getNew() {
        return getFlag(PlayerField.NEW);
    }
    
    public void setWatched(boolean b) {
        setFlag(PlayerField.WATCHED, true);
    }
    
    public boolean getWatched() {
        return getFlag(PlayerField.WATCHED);
    }
    
}

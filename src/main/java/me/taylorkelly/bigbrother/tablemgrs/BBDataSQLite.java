/**
 * 
 */
package me.taylorkelly.bigbrother.tablemgrs;

/**
 * @author Rob
 *
 */
public class BBDataSQLite extends BBDataTable {

    public final int revision = 1;
    public String toString() {
        return "BBData SQLite Driver r"+Integer.valueOf(revision);
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.BBDataTable#onLoad()
     */
    @Override
    public void onLoad() {
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.BBDataTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
        // TODO Auto-generated method stub
        return 
        "CREATE TABLE `"+getTableName()+"` (" 
        + "`id` INTEGER PRIMARY KEY," 
        + "`date` INT UNSIGNED NOT NULL DEFAULT '0'," 
        + "`player` varchar(32) NOT NULL DEFAULT 'Player'," 
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
        + "CREATE INDEX rbackedIndex on bbdata (rbacked);";
    }
    
}

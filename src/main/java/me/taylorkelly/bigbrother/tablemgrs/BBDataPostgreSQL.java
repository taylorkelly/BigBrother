package me.taylorkelly.bigbrother.tablemgrs;


public class BBDataPostgreSQL extends BBDataMySQL {
    public static String getMySQLIgnore() {
        return " ";
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#onLoad()
     */
    @Override
    public void onLoad() {
        
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
    	return "CREATE TABLE \""+getTableName()+"\" ("
        + "\"id\" SERIAL,"
        + "\"date\" INT NOT NULL DEFAULT '0'," 
        + "\"player\" INT NOT NULL DEFAULT 0," 
        + "\"action\" smallint NOT NULL DEFAULT '0'," 
        + "\"world\" smallint NOT NULL DEFAULT '0'," 
        + "\"x\" int NOT NULL DEFAULT '0'," 
        + "\"y\" smallint NOT NULL DEFAULT '0'," 
        + "\"z\" int NOT NULL DEFAULT '0'," 
        + "\"type\" smallint NOT NULL DEFAULT '0',"
        + "\"data\" varchar(500) NOT NULL DEFAULT '',"
        + "\"rbacked\" boolean NOT NULL DEFAULT '0',"
        + "PRIMARY KEY (\"id\"))";
    	/*, 
        INDEX(\"world\"), 
        INDEX(\"x\",\"y\",\"z\"), 
        INDEX(\"player\"),
        INDEX(\"action\"), 
        INDEX(\"date\"), 
        INDEX(\"type\"), 
        INDEX(\"rbacked\")*/
    }
    
}

package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.SQLException;



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
    
    @Override
    public String getPreparedDataBlockStatement() throws SQLException {
    	// rbacked is truly boolean, unlike in MySQL
        return "INSERT INTO " + getTableName()
                + " (date, player, action, world, x, y, z, type, data, rbacked) VALUES (?,?,?,?,?,?,?,?,?,false)";
    }
    
	@Override
	public String getCleanseAged(Long timeAgo, long deletesPerCleansing) {
		String cleansql = "DELETE FROM \""+getTableName()+"\" WHERE ";
        if (deletesPerCleansing > 0) {
        	// LIMIT on DELETE is not supported by postgresql.
            cleansql += " id IN (SELECT id FROM \""+getTableName()+"\" WHERE date < " + timeAgo + " LIMIT " + deletesPerCleansing+")";
        } else {
        	cleansql += " date < " + timeAgo;
        }
        cleansql += ";";
        return cleansql;
	}
	
	@Override
	public String getCleanseByLimit(Long maxRecords, long deletesPerCleansing) {
		String cleansql = "DELETE FROM \""+getTableName()+"\" LEFT OUTER JOIN (SELECT \"id\" FROM \"bbdata\" ORDER BY \"id\" DESC LIMIT 0,"
	    	+ maxRecords
	    	+ ") AS \"savedValues\" ON \"savedValues.id\" = \"bbdata.id\" WHERE \"savedValues.id\" IS NULL";
	    if (deletesPerCleansing > 0) {
	        cleansql += " LIMIT " + deletesPerCleansing;
	    }
	    cleansql += ";";
    	return cleansql;
	}
	
}

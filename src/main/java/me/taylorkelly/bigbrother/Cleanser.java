package me.taylorkelly.bigbrother;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;
import me.taylorkelly.util.Time;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

// Rule 1 - If you end up using ResultSet, you're doing it wrong.
class Cleanser {

    static boolean needsCleaning() {
        return BBSettings.cleanseAge != -1 || BBSettings.maxRecords != -1;
    }

    static void clean() {
        if (BBSettings.cleanseAge != -1) {
            cleanByAge();
        }
        
        if(BBSettings.maxRecords != -1) {
            cleanByNumber();
        }
    }

    private static void cleanByAge() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.createStatement();
            int amount = stmt.executeUpdate("DELETE FROM `bbdata` WHERE date < " + Long.valueOf(Time.ago(BBSettings.cleanseAge)) + ";");
            BBLogging.info("Cleaned out " + Integer.valueOf(amount) + " records because of age");
            conn.commit();
        } catch (SQLException ex) {
            BBLogging.severe("Cleanse SQL exception (by age)", ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BBLogging.severe("Cleanse SQL exception (by age) (on close)", ex);
            }
        }
    }

	private static void cleanByNumber() {
        if (BBSettings.mysql) {
        	if(BBSettings.maxRecords<0)
        	{
        		// Fix exception caused when trying to delete -1 records.
        		BBLogging.info("Skipping; max-records is negative.");
        		return;
        	}
            Connection conn = null;
            Statement stmt = null;
            // Now we can pretend to use LIMIT.
            try {
            	conn = ConnectionManager.getConnection();
            	stmt = conn.createStatement();

                // Since MYSQL doesn't support LIMIT in subqueries, we have to create a variable.
                // I hate MySQL.
                stmt.executeUpdate("SET @i = 0;");
            } catch(SQLException e) {
            	BBLogging.severe("MySQL won't let us create a variable for faking the use of LIMIT in a subquery. :<", e);
            } finally {
            	try {
            		if (stmt != null) {
            			stmt.close();
            		}
            		if (conn != null) {
            			conn.close();
            		}
            		return;
            	} catch (SQLException ex) {
            		BBLogging.severe("Cleanse SQL exception (by #) (on var close)", ex);
            	}
            }
            try {
            	conn = ConnectionManager.getConnection();
            	stmt = conn.createStatement();
            	int amount = stmt.executeUpdate("DELETE FROM `bbdata` WHERE id NOT IN(SELECT `id` FROM `bbdata` WHERE ( @i := ( @i +1 ) ) <= "+Long.valueOf(BBSettings.maxRecords)+" ORDER BY `id` DESC );");
            	BBLogging.info("Cleaned out " + Integer.valueOf(amount) + " records because there are too many");
            	conn.commit();
            } catch (SQLException ex) {
            	BBLogging.severe("Cleanse SQL exception (by #)", ex);
            } finally {
            	try {
            		if (stmt != null) {
            			stmt.close();
            		}
            		if (conn != null) {
            			conn.close();
            		}
            	} catch (SQLException ex) {
            		BBLogging.severe("Cleanse SQL exception (by #) (on close)", ex);
            	}
            }

        } else {
            BBLogging.info("SQLite can't cleanse by # of records.");
        }
    }

    static void clean(Player player) {
        if (BBSettings.cleanseAge != -1) {
            cleanByAge(player);
        }
        cleanByNumber(player);


    }

    private static void cleanByAge(Player player) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.createStatement();
            int amount = stmt.executeUpdate("DELETE FROM `bbdata` WHERE date < " + Long.valueOf(Time.ago(BBSettings.cleanseAge)) + ";");
            player.sendMessage(ChatColor.BLUE + "Cleaned out " + Integer.valueOf(amount) + " records because of age");
            conn.commit();
        } catch (SQLException ex) {
            BBLogging.severe("Cleanse SQL exception (by age)", ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                BBLogging.severe("Cleanse SQL exception (by age) (on close)", ex);
            }
        }
    }

	private static void cleanByNumber(Player player) {
        if (BBSettings.mysql) {
        	if(BBSettings.maxRecords<0)
        	{
        		// Fix exception caused when trying to delete -1 records.
        		BBLogging.info("Skipping; max-records is negative.");
        		return;
        	}
            Connection conn = null;
            Statement stmt = null;
            try {
            	conn = ConnectionManager.getConnection();
            	stmt = conn.createStatement();
            	int amount = stmt.executeUpdate("DELETE FROM `bbdata` WHERE id NOT IN(SELECT `id` FROM `bbdata` ORDER BY `id` DESC LIMIT 0,"+Long.valueOf(BBSettings.maxRecords)+");");
            	player.sendMessage(ChatColor.BLUE + "Cleaned out " + Integer.valueOf(amount) + " records because there are too many");
            	conn.commit();
            } catch (SQLException ex) {
            	BBLogging.severe("Cleanse SQL exception (by #)", ex);
            } finally {
            	try {
            		if (stmt != null) {
            			stmt.close();
            		}
            		if (conn != null) {
            			conn.close();
            		}
            	} catch (SQLException ex) {
            		BBLogging.severe("Cleanse SQL exception (by #) (on close)", ex);
            	}
            }
            
        } else {
            player.sendMessage(ChatColor.RED + "SQLite can't cleanse by # of records.");
        }
    }
}

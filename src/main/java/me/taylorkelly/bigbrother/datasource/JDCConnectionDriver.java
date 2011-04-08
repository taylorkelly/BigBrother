package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <b>Purpose:</b>Wrapper for JDBCConnectionDriver.<br>
 * <b>Description:</b>http://java.sun.com/developer/onlineTraining/Programming/
 * JDCBook/ conpool.html<br>
 * <b>Copyright:</b>Licensed under the Apache License, Version 2.0.
 * http://www.apache.org/licenses/LICENSE-2.0<br>
 * <b>Company:</b> SIMPL<br>
 * 
 * @author schneimi
 * @version $Id: JDCConnectionDriver.java 1224 2010-04-28 14:17:34Z
 *          michael.schneidt@arcor.de $<br>
 * @link http://code.google.com/p/simpl09/
 */
public class JDCConnectionDriver implements Driver {
    public static final String URL_PREFIX = "jdbc:jdc:";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private ConnectionService pool;
    private String user;
    private String password;
    private String url;
    
    public JDCConnectionDriver(String driver, String url, String user, String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        DriverManager.registerDriver(this);
        Class.forName(driver).newInstance();
        reconnect();
    }
    
    /**
     * Added to support reconnects.
     */
    public void reconnect() {
        pool = new ConnectionService(url, user, password);
    }
    
    public Connection connect(String url, Properties props) throws SQLException {
        if (!url.startsWith(JDCConnectionDriver.URL_PREFIX)) {
            return null;
        }
        if(pool==null)
            reconnect();
        return pool.getConnection();
    }
    
    public boolean acceptsURL(String url) {
        return url.startsWith(JDCConnectionDriver.URL_PREFIX);
    }
    
    public int getMajorVersion() {
        return JDCConnectionDriver.MAJOR_VERSION;
    }
    
    public int getMinorVersion() {
        return JDCConnectionDriver.MINOR_VERSION;
    }
    
    public DriverPropertyInfo[] getPropertyInfo(String str, Properties props) {
        return new DriverPropertyInfo[0];
    }
    
    public boolean jdbcCompliant() {
        return false;
    }
}

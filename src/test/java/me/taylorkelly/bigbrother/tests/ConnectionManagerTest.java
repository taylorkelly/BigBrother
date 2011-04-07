/**
 * 
 */
package me.taylorkelly.bigbrother.tests;

import static org.junit.Assert.*;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.ConnectionManager;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Rob
 *
 */
public class ConnectionManagerTest {
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        BBSettings.databaseSystem=DBMS.H2;
        BBSettings.mysqlPersistant=true;
    }
    
    /**
     * Test method for {@link me.taylorkelly.bigbrother.datasource.ConnectionManager#getConnection()}.
     */
    @Test
    public void testGetConnection() {
        createConnection();
        if(ConnectionManager.getConnection()==null) {
            fail("Connection is null");
        }
    }
    
    private void createConnection() {
        if(!ConnectionManager.setupConnection())
            fail("Connection failed.");
    }
    
}

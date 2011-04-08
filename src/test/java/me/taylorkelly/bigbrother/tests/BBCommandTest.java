/**
 * 
 */
package me.taylorkelly.bigbrother.tests;

import static org.junit.Assert.*;

import me.taylorkelly.bigbrother.BBCommand;

import org.junit.Test;

/**
 * @author Rob
 *
 */
public class BBCommandTest {
    
    /**
     * Test method for {@link me.taylorkelly.bigbrother.BBCommand#groupArgs(java.lang.String[])}.
     */
    @Test
    public void testGroupArgs() {
        String[] input=new String[]{"\"Some","Guy's\"","name"};
        String[] expected=new String[]{"Some Guy's","name"};
        assertArrayEquals("Grouping function failed.",expected,BBCommand.groupArgs(input));
    }
    
}

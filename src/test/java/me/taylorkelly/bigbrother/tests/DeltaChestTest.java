/**
 * 
 */
package me.taylorkelly.bigbrother.tests;

import static org.junit.Assert.*;

import me.taylorkelly.bigbrother.datablock.DeltaChest;
import me.taylorkelly.bigbrother.datablock.DeltaChest.DeltaType;

import org.bukkit.inventory.ItemStack;
import org.junit.Test;

/**
 * @author Rob
 *
 */
public class DeltaChestTest {
    @Test
    public void testDelta() {
        ItemStack[] stackOrig,stackNew;
        // Size doesn't matter in unit tests.
        stackOrig=new ItemStack[]{
                new ItemStack(1, 15),
                new ItemStack(1, 16),
                new ItemStack(1, 64),
                new ItemStack(1, 64),
                new ItemStack(0)
        };
        // Same as above, EXCEPT...
        stackNew=new ItemStack[]{
                new ItemStack(1, 16), // added 1
                new ItemStack(1, 15), // removed 1
                new ItemStack(2, 64), // replaced with type:2
                new ItemStack(1, 64), // no change
                new ItemStack(0)      // no change
        };
        DeltaChest.DeltaEntry[] de = DeltaChest.processDeltaStream(5, DeltaChest.getInventoryDelta(stackOrig, stackNew));
        assertEquals("Slot 1 (Added 1)",de[0].Type,DeltaType.ADDED);
        assertEquals("Slot 2 (Removed 1)",de[1].Type,DeltaType.REMOVED);
        assertEquals("Slot 3 (Replaced)",de[2].Type,DeltaType.REPLACED);
        assertEquals("Slot 4 (No change)",de[3].Type,DeltaType.NO_CHANGE);
        assertEquals("Slot 5 (air slot, NC)",de[4].Type,DeltaType.NO_CHANGE);
    }
    

    
    /**
     * Test method for {@link me.taylorkelly.bigbrother.datablock.DeltaChest#rollback(org.bukkit.Server)}.
     */
    @Test
    public void testRollback() {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link me.taylorkelly.bigbrother.datablock.DeltaChest#redo(org.bukkit.Server)}.
     */
    @Test
    public void testRedo() {
        fail("Not yet implemented");
    }
    
}

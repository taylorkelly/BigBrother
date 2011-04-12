/**
 * 
 */
package me.taylorkelly.bigbrother.tests;

import static org.junit.Assert.*;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datablock.DeltaChest;
import me.taylorkelly.bigbrother.datablock.DeltaChest.DeltaType;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rob
 *
 */
public class DeltaChestTest {
    //private World world;
    private ItemStack[] stateA;
    private BBPlayerInfo douchebag;

    @Before
    public void setUp() {
        //this.world = TestUtils.createSimpleWorld();
    }
    
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
        assertEquals("Slot 1 (Added 1)",DeltaType.ADDED,de[0].Type);
        assertEquals("Slot 2 (Removed 1)",DeltaType.REMOVED,de[1].Type);
        assertEquals("Slot 3 (Replaced)",DeltaType.REPLACED,de[2].Type);
        assertEquals("Slot 4 (No change)",DeltaType.NO_CHANGE,de[3].Type);
        assertEquals("Slot 5 (air slot, NC)",DeltaType.NO_CHANGE,de[4].Type);
    }
    

    
    /**
     * Test method for {@link me.taylorkelly.bigbrother.datablock.DeltaChest#rollback(org.bukkit.Server)}.
     */
    /*@Test
    public void testRollback() {
        // Create chest
        Block blockA = world.getBlockAt(0,0,0);
        blockA.setTypeId(Material.CHEST.getId());
        Chest c = (Chest)blockA.getState();
        
        // Clear any leftovers
        c.getInventory().clear();
        
        // Add 64 gold ingots.
        c.getInventory().addItem(new ItemStack(Material.GOLD_INGOT.getId(),64));
        stateA=c.getInventory().getContents();
        
        //Create fake player.
        douchebag = new BBPlayerInfo(1, "Douchebag", 1); // ID #1: Douchebag (Watched)
        
        // Simulate player stealing gold.
        douchebag.setHasOpenedChest(c, stateA);
        c.getInventory().clear();
        
        // Record theft
        World world = douchebag.getOpenedChest().getWorld();
        int x = douchebag.getOpenedChest().getX();
        int y = douchebag.getOpenedChest().getY();
        int z = douchebag.getOpenedChest().getZ();
        Chest chest = (Chest)world.getBlockAt(x, y, z).getState();
        ItemStack[] orig = douchebag.getOldChestContents();
        ItemStack[] latest = chest.getInventory().getContents();
        DeltaChest dc = new DeltaChest(douchebag.getName(), chest, orig, latest);
        //dc.send();
        
        dc.rollback(world);
        
        assertArrayEquals("Rollback failed",stateA,c.getInventory().getContents());
    }
    */
    
    /**
     * Test method for {@link me.taylorkelly.bigbrother.datablock.DeltaChest#redo(org.bukkit.Server)}.
     */
    //@Test
    public void testRedo() {
        fail("Not yet implemented");
    }
    
}

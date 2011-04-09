package me.taylorkelly.bigbrother.tests;

import java.io.File;

import me.taylorkelly.bigbrother.BBLogging;
import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.ServerNBTManager;
import net.minecraft.server.WorldManager;
import net.minecraft.server.WorldServer;

import org.bukkit.World;

public class TestUtils {
    static WorldServer world=null;
    
    /** 
     * Initialize and populate a simple world. USED FOR JUnit TESTS ONLY
     * @return a 
     */
    public static World createSimpleWorld() {
        if(world==null) {
            // CraftBukkit start
            // TerraBukkit start
            boolean hell = false;
            boolean mobs = false;
            boolean animals = false;
            
            String worldname="testworld";
            long seed=1234;
            world = new WorldServer(null, new ServerNBTManager(new File("tests"),worldname, true), worldname, hell ? -1 : 0, seed, null, null);
            // TerraBukkit end
            world.a(new WorldManager(null, world));
            world.j = mobs ? 1 : 0;
            world.a(mobs, animals);
            // CraftBukkit end
            
            ChunkCoordinates chunkcoordinates = world.m(); // CraftBukkit

            short radius = 196;
            long j = System.currentTimeMillis();
            for (int k = -radius; k <= radius; k += 16) {
                for (int l = -radius; l <= radius; l += 16) {
                    long i1 = System.currentTimeMillis();

                    if (i1 < j) {
                        j = i1;
                    }

                    if (i1 > j + 1000L) {
                        int j1 = (radius * 2 + 1) * (radius * 2 + 1);
                        int k1 = (k + radius) * (radius * 2 + 1) + l + 1;
                        BBLogging.debug("Preparing spawn area: "+(k1 * 100 / j1)+"%");
                        j = i1;
                    }

                    world.u.c(chunkcoordinates.a + k >> 4, chunkcoordinates.c + l >> 4);
                    
                    while (world.f()) {
                        ;
                    }
                    
                }
            }
        }
        return (World)world.getWorld();
    }
    
}

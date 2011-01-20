package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.ArrayList;
import java.util.List;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.DestroySignText;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;

public abstract class Explosion extends BBDataBlock {
    private ArrayList<BBDataBlock> bystanders;
    
    public Explosion(int dataBlockType, String name, Block block) {
        super(name, dataBlockType, 0, block.getX(), block.getY(), block.getZ(), block.getTypeId(), block.getData() + "");
        bystanders = new ArrayList<BBDataBlock>();
        torchCheck(name, block);
        surroundingSignChecks(name, block);
        signCheck(name, block);
        checkGnomesLivingOnTop(name, block);
    }
    
    public void send() {
        for (BBDataBlock block : bystanders) {
            block.send();
        }
        super.send();
    }

    public void rollback(Server server) {
        World worldy = server.getWorlds()[world];
        if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        byte blockData = Byte.parseByte(data);
        worldy.getBlockAt(x, y, z).setTypeId(type);
        worldy.getBlockAt(x, y, z).setData(blockData);
    }

    public void redo(Server server) {
        World worldy = server.getWorlds()[world];
        if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        worldy.getBlockAt(x, y, z).setTypeId(0);
    }

    protected void torchCheck(String player, Block block) {
        ArrayList<Integer> torchTypes = new ArrayList<Integer>();
        torchTypes.add(50);
        torchTypes.add(75);
        torchTypes.add(76);

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);

        if (torchTypes.contains(torchTop.getTypeId()) && torchTop.getData() == 5) {
            bystanders.add(newInstance(player, torchTop));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeId()) && torchNorth.getData() == 1) {
            bystanders.add(newInstance(player, torchNorth));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeId()) && torchSouth.getData() == 2) {
            bystanders.add(newInstance(player, torchSouth));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeId()) && torchEast.getData() == 3) {
            bystanders.add(newInstance(player, torchEast));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeId()) && torchWest.getData() == 4) {
            bystanders.add(newInstance(player, torchWest));
        }
    }

    protected void surroundingSignChecks(String player, Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeId() == 63) {
            bystanders.add(newInstance(player, top));
        }
        Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if (north.getTypeId() == 68 && north.getData() == 5) {
            bystanders.add(newInstance(player, north));
        }
        Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if (south.getTypeId() == 68 && south.getData() == 4) {
            bystanders.add(newInstance(player, south));
        }
        Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if (east.getTypeId() == 68 && east.getData() == 3) {
            bystanders.add(newInstance(player, east));
        }
        Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if (west.getTypeId() == 68 && west.getData() == 2) {
            bystanders.add(newInstance(player, west));
        }
    }

    protected void signCheck(String player, Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new DestroySignText(player, sign));
        }
    }

    protected void checkGnomesLivingOnTop(String player, Block block) {
        ArrayList<Integer> gnomes = new ArrayList<Integer>();
        gnomes.add(6); // Sapling
        gnomes.add(37); // Yellow Flower
        gnomes.add(38); // Red Flower
        gnomes.add(39); // Brown Mushroom
        gnomes.add(40); // Red Mushroom
        gnomes.add(55); // Redstone
        gnomes.add(59); // Crops
        gnomes.add(64); // Wood Door
        gnomes.add(66); // Tracks
        gnomes.add(69); // Lever
        gnomes.add(70); // Stone pressure plate
        gnomes.add(71); // Iron Door
        gnomes.add(72); // Wood pressure ePlate
        gnomes.add(78); // Snow
        gnomes.add(81); // Cactus
        gnomes.add(83); // Reeds

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        Block mrGnome = block.getWorld().getBlockAt(x, y + 1, z);

        if (gnomes.contains(mrGnome.getTypeId())) {
            bystanders.add(newInstance(player, mrGnome));
        }
    }

    protected abstract Explosion newInstance(String player, Block block);
    
    protected Explosion(String player, int dataType, int world, int x, int y, int z, int type, String data) {
        super(player, dataType, world, x, y, z, type, data);
    }
}

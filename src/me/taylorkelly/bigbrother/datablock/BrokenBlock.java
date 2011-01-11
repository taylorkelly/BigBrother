package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;

public class BrokenBlock extends BBDataBlock {
    private ArrayList<BBDataBlock> bystanders;

    public BrokenBlock(Player player, Block block) {
        // TODO Better World support
        super(player.getName(), BLOCK_BROKEN, 0, block.getX(), block.getY(), block.getZ(), block.getTypeID(), block.getData() + "");
        bystanders = new ArrayList<BBDataBlock>();
        torchCheck(player, block);
        surroundingSignChecks(player, block);
        signCheck(player, block);
        checkGnomesLivingOnTop(player, block);
    }

    public BrokenBlock(Player player, int x, int y, int z, int type, int data) {
        super(player.getName(), BLOCK_BROKEN, 0, x, y, z, type, data + "");
        bystanders = new ArrayList<BBDataBlock>();
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
        worldy.getBlockAt(x, y, z).setTypeID(type);
        worldy.getBlockAt(x, y, z).setData(blockData);
    }

    public void redo(Server server) {
        World worldy = server.getWorlds()[world];
        if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        worldy.getBlockAt(x, y, z).setTypeID(0);
    }

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return new BrokenBlock(player, world, x, y, z, type, data);
    }

    private BrokenBlock(String player, int world, int x, int y, int z, int type, String data) {
        super(player, BLOCK_BROKEN, world, x, y, z, type, data);
    }

    private void torchCheck(Player player, Block block) {
        ArrayList<Integer> torchTypes = new ArrayList<Integer>();
        torchTypes.add(50);
        torchTypes.add(75);
        torchTypes.add(76);

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);

        if (torchTypes.contains(torchTop.getTypeID()) && torchTop.getData() == 5) {
            bystanders.add(new BrokenBlock(player, torchTop));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeID()) && torchNorth.getData() == 1) {
            bystanders.add(new BrokenBlock(player, torchNorth));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeID()) && torchSouth.getData() == 2) {
            bystanders.add(new BrokenBlock(player, torchSouth));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeID()) && torchEast.getData() == 3) {
            bystanders.add(new BrokenBlock(player, torchEast));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeID()) && torchWest.getData() == 4) {
            bystanders.add(new BrokenBlock(player, torchWest));
        }
    }

    private void surroundingSignChecks(Player player, Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeID() == 63) {
            bystanders.add(new BrokenBlock(player, top));
        }
        Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if (north.getTypeID() == 68 && north.getData() == 5) {
            bystanders.add(new BrokenBlock(player, north));
        }
        Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if (south.getTypeID() == 68 && south.getData() == 4) {
            bystanders.add(new BrokenBlock(player, south));
        }
        Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if (east.getTypeID() == 68 && east.getData() == 3) {
            bystanders.add(new BrokenBlock(player, east));
        }
        Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if (west.getTypeID() == 68 && west.getData() == 2) {
            bystanders.add(new BrokenBlock(player, west));
        }
    }

    private void signCheck(Player player, Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new DestroySignText(player, sign));
        }
    }

    private void checkGnomesLivingOnTop(Player player, Block block) {
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

        if (gnomes.contains(mrGnome.getTypeID())) {
            bystanders.add(new BrokenBlock(player, mrGnome));
        }
    }

}

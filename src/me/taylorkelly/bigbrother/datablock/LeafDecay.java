package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class LeafDecay extends BBDataBlock{
    private ArrayList<BBDataBlock> bystanders;

    public LeafDecay(Player player, Block block) {
        // TODO Better World support
        super(player.getName(), LEAF_DECAY, 0, block.getX(), block.getY(), block.getZ(), block.getTypeId(), block.getData() + "");
        bystanders = new ArrayList<BBDataBlock>();
        torchCheck(player, block);
        surroundingSignChecks(player, block);
        signCheck(player, block);
        checkGnomesLivingOnTop(player, block);
    }
    public LeafDecay(Block block) {
        // TODO Better World support
        super(ENVIRONMENT, LEAF_DECAY, 0, block.getX(), block.getY(), block.getZ(), block.getTypeId(), block.getData() + "");
        bystanders = new ArrayList<BBDataBlock>();
        torchCheck(block);
        surroundingSignChecks(block);
        signCheck(block);
        checkGnomesLivingOnTop(block);
    }

    public LeafDecay(Player player, int x, int y, int z, int type, int data) {
        super(player.getName(), LEAF_DECAY, 0, x, y, z, type, data + "");
        bystanders = new ArrayList<BBDataBlock>();
    }
    
    public static BBDataBlock create(Block block) {
        //TODO Player handling
        return new LeafDecay(block);
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

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return new LeafDecay(player, world, x, y, z, type, data);
    }

    private LeafDecay(String player, int world, int x, int y, int z, int type, String data) {
        super(player, LEAF_DECAY, world, x, y, z, type, data);
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

        if (torchTypes.contains(torchTop.getTypeId()) && torchTop.getData() == 5) {
            bystanders.add(new BrokenBlock(player, torchTop));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeId()) && torchNorth.getData() == 1) {
            bystanders.add(new BrokenBlock(player, torchNorth));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeId()) && torchSouth.getData() == 2) {
            bystanders.add(new BrokenBlock(player, torchSouth));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeId()) && torchEast.getData() == 3) {
            bystanders.add(new BrokenBlock(player, torchEast));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeId()) && torchWest.getData() == 4) {
            bystanders.add(new BrokenBlock(player, torchWest));
        }
    }

    private void surroundingSignChecks(Player player, Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeId() == 63) {
            bystanders.add(new BrokenBlock(player, top));
        }
        Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if (north.getTypeId() == 68 && north.getData() == 5) {
            bystanders.add(new BrokenBlock(player, north));
        }
        Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if (south.getTypeId() == 68 && south.getData() == 4) {
            bystanders.add(new BrokenBlock(player, south));
        }
        Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if (east.getTypeId() == 68 && east.getData() == 3) {
            bystanders.add(new BrokenBlock(player, east));
        }
        Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if (west.getTypeId() == 68 && west.getData() == 2) {
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

        if (gnomes.contains(mrGnome.getTypeId())) {
            bystanders.add(new BrokenBlock(player, mrGnome));
        }
    }
    
    private void torchCheck(Block block) {
        ArrayList<Integer> torchTypes = new ArrayList<Integer>();
        torchTypes.add(50);
        torchTypes.add(75);
        torchTypes.add(76);

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);

        if (torchTypes.contains(torchTop.getTypeId()) && torchTop.getData() == 5) {
            bystanders.add(new LeafDecay(torchTop));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeId()) && torchNorth.getData() == 1) {
            bystanders.add(new LeafDecay(torchNorth));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeId()) && torchSouth.getData() == 2) {
            bystanders.add(new LeafDecay(torchSouth));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeId()) && torchEast.getData() == 3) {
            bystanders.add(new LeafDecay(torchEast));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeId()) && torchWest.getData() == 4) {
            bystanders.add(new LeafDecay(torchWest));
        }
    }

    private void surroundingSignChecks(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeId() == 63) {
            bystanders.add(new LeafDecay(top));
        }
        Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if (north.getTypeId() == 68 && north.getData() == 5) {
            bystanders.add(new LeafDecay(north));
        }
        Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if (south.getTypeId() == 68 && south.getData() == 4) {
            bystanders.add(new LeafDecay(south));
        }
        Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if (east.getTypeId() == 68 && east.getData() == 3) {
            bystanders.add(new LeafDecay(east));
        }
        Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if (west.getTypeId() == 68 && west.getData() == 2) {
            bystanders.add(new LeafDecay(west));
        }
    }

    private void signCheck(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new DestroySignText(ENVIRONMENT, sign));
        }
    }

    private void checkGnomesLivingOnTop(Block block) {
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
            bystanders.add(new LeafDecay(mrGnome));
        }
    }
}

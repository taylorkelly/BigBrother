package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public class BrokenBlock extends BBDataBlock {

    private ArrayList<BBDataBlock> bystanders;

    public BrokenBlock(String player, Block block, String world) {
        this(player, block, world, true);
    }

    public BrokenBlock(String player, Block block, String world, boolean checks) {
        super(player, Action.BLOCK_BROKEN, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBDataBlock>();
        if (checks) {
            torchCheck(player, block);
            surroundingSignChecks(player, block);
            signCheck(player, block);
            chestCheck(player, block);
            checkGnomesLivingOnTop(player, block);
            bedCheck(player, block);
        }
    }

    private void chestCheck(String player, Block block) {
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            bystanders.add(new DeltaChest(player, chest, chest.getInventory().getContents(),new ItemStack[chest.getInventory().getSize()]));
        }
    }

    public BrokenBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, byte data) {
        super(pi, Action.BLOCK_BROKEN, world, x, y, z, type, Byte.toString(data));
        bystanders = new ArrayList<BBDataBlock>();
    }

    @Override
    public void send() {
        for (BBDataBlock block : bystanders) {
            block.send();
        }
        super.send();
    }

    public void rollback(World wld) {
        if (type != 51 || BBSettings.restoreFire) {
            World currWorld = wld;//server.getWorld(world);
            if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
                currWorld.loadChunk(x >> 4, z >> 4);
            }

            byte blockData = Byte.parseByte(data);
            currWorld.getBlockAt(x, y, z).setTypeId(type);
            currWorld.getBlockAt(x, y, z).setData(blockData);
        }
    }

    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        currWorld.getBlockAt(x, y, z).setTypeId(0);
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new BrokenBlock(pi, world, x, y, z, type, data);
    }

    private BrokenBlock(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, Action.BLOCK_BROKEN, world, x, y, z, type, data);
    }

    private void torchCheck(String player, Block block) {
        ArrayList<Integer> torchTypes = new ArrayList<Integer>();
        torchTypes.add(50); // Torch
        torchTypes.add(75); // Redstone torch (on)
        torchTypes.add(76); // Redstone torch (off)

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);

        if (torchTypes.contains(torchTop.getTypeId()) && torchTop.getData() == 5) {
            bystanders.add(new BrokenBlock(player, torchTop, world));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeId()) && torchNorth.getData() == 1) {
            bystanders.add(new BrokenBlock(player, torchNorth, world));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeId()) && torchSouth.getData() == 2) {
            bystanders.add(new BrokenBlock(player, torchSouth, world));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeId()) && torchEast.getData() == 3) {
            bystanders.add(new BrokenBlock(player, torchEast, world));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeId()) && torchWest.getData() == 4) {
            bystanders.add(new BrokenBlock(player, torchWest, world));
        }
    }

    private void surroundingSignChecks(String player, Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeId() == 63) {
            bystanders.add(new BrokenBlock(player, top, world));
        }
        Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if (north.getTypeId() == 68 && north.getData() == 5) {
            bystanders.add(new BrokenBlock(player, north, world));
        }
        Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if (south.getTypeId() == 68 && south.getData() == 4) {
            bystanders.add(new BrokenBlock(player, south, world));
        }
        Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if (east.getTypeId() == 68 && east.getData() == 3) {
            bystanders.add(new BrokenBlock(player, east, world));
        }
        Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if (west.getTypeId() == 68 && west.getData() == 2) {
            bystanders.add(new BrokenBlock(player, west, world));
        }
    }

    private void signCheck(String player, Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new SignDestroyed(player, block.getTypeId(), block.getData(), sign, world));
        }
    }

    private void checkGnomesLivingOnTop(String player, Block block) {
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
            bystanders.add(new BrokenBlock(player, mrGnome, world));
        }
    }

    private void bedCheck(String player, Block bed) {
        if (bed.getType() == Material.BED_BLOCK) {
            if (bed.getData() >= 8) { // Head of bed
                Block foot = null;
                switch (bed.getData() - 8) {
                    case (0): // Head is pointing West
                        foot = bed.getWorld().getBlockAt(x, y, z - 1);
                        bystanders.add(new BrokenBlock(player, foot, world, false));
                        break;
                    case (1): // Head is pointing North
                        foot = bed.getWorld().getBlockAt(x + 1, y, z);
                        bystanders.add(new BrokenBlock(player, foot, world, false));
                        break;
                    case (2): // Head is pointing East
                        foot = bed.getWorld().getBlockAt(x, y, z + 1);
                        bystanders.add(new BrokenBlock(player, foot, world, false));
                        break;
                    case (3): // Head is pointing South
                        foot = bed.getWorld().getBlockAt(x - 1, y, z);
                        bystanders.add(new BrokenBlock(player, foot, world, false));
                        break;
                }
            } else { // Foot of bed
                Block head = null;
                switch (bed.getData()) {
                    case (0): // Head is pointing West
                        head = bed.getWorld().getBlockAt(x, y, z + 1);
                        bystanders.add(new BrokenBlock(player, head, world, false));
                        break;
                    case (1): // Head is pointing North
                        head = bed.getWorld().getBlockAt(x - 1, y, z);
                        bystanders.add(new BrokenBlock(player, head, world, false));
                        break;
                    case (2): // Head is pointing East
                        head = bed.getWorld().getBlockAt(x, y, z - 1);
                        bystanders.add(new BrokenBlock(player, head, world, false));
                        break;
                    case (3): // Head is pointing South
                        head = bed.getWorld().getBlockAt(x + 1, y, z);
                        bystanders.add(new BrokenBlock(player, head, world, false));
                        break;
                }
            }
        }
    }
}

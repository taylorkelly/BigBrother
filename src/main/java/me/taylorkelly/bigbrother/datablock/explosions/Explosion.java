package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.DeltaChest;
import me.taylorkelly.bigbrother.datablock.DestroySignText;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.ItemStack;

public abstract class Explosion extends BBDataBlock {
    private ArrayList<BBDataBlock> bystanders;

    public Explosion(Action dataBlockType, String name, Block block, int world) {
        super(name, dataBlockType, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBDataBlock>();
        torchCheck(name, block);
        surroundingSignChecks(name, block);
        signCheck(name, block);
        chestCheck(name, block);
        checkGnomesLivingOnTop(name, block);
    }

	@Override
    public void send() {
        for (BBDataBlock block : bystanders) {
            block.send();
        }
        super.send();
    }

    private void chestCheck(String player, Block block) {
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < chest.getInventory().getSize(); i++) {
                ItemStack stack = chest.getInventory().getItem(i);
                if (stack != null && stack.getAmount() != 0) {
                    builder.append(stack.getTypeId());
                    if (stack.getData() != null && stack.getData().getData() != 0) {
                        builder.append(":");
                        builder.append(stack.getData().getData());
                    }
                    builder.append(",");
                    builder.append("-").append(stack.getAmount());
                }
                if (i + 1 < chest.getInventory().getSize())
                    builder.append(";");
            }
            bystanders.add(new DeltaChest(player, chest, builder.toString(), world));
        }
    }

    public void rollback(Server server) {
        if (type != 51 || BBSettings.restoreFire) {
            World worldy = server.getWorlds().get(world);
            if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
                ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
            }

            byte blockData = Byte.parseByte(data);
            worldy.getBlockAt(x, y, z).setTypeId(type);
            worldy.getBlockAt(x, y, z).setData(blockData);
        }
    }

    public void redo(Server server) {
        World worldy = server.getWorlds().get(world);
        if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        worldy.getBlockAt(x, y, z).setTypeId(0);
    }

    protected final void torchCheck(String player, Block block) {
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

    protected final void surroundingSignChecks(String player, Block block) {
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

    protected final void signCheck(String player, Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new DestroySignText(player, sign, world));
        }
    }

    protected final void checkGnomesLivingOnTop(String player, Block block) {
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

    protected Explosion(String player, Action dataType, int world, int x, int y, int z, int type, String data) {
        super(player, dataType, world, x, y, z, type, data);
    }
}

package me.taylorkelly.bigbrother.datablock;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeltaChest extends BBDataBlock {

    public DeltaChest(String player, Chest chest, String changes) {
        // TODO Better World support
        super(player, DELTA_CHEST, 0, chest.getX(), chest.getY(), chest.getZ(), chest.getTypeId(), changes);
    }

    private DeltaChest(String player, int world, int x, int y, int z, int type, String data) {
        super(player, DELTA_CHEST, world, x, y, z, type, data);
    }

    public void rollback(Server server) {
        World worldy = server.getWorlds().get(world);
        if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        String[] changes = data.split(";");
        Block block = worldy.getBlockAt(x, y, z);
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            Inventory inv = chest.getInventory();
            for (int i = 0; i < changes.length; i++) {
                String change = changes[i];
                if (change.equals(""))
                    continue;
                String[] pieces = change.split(",");
                if (pieces.length != 2)
                    continue;
                int id = 0;
                int data = 0;
                if (pieces[0].contains(":")) {
                    String[] subPieces = pieces[0].split(":");
                    id = Integer.parseInt(subPieces[0]);
                    data = Integer.parseInt(subPieces[1]);
                } else {
                    id = Integer.parseInt(pieces[0]);
                }
                int amount = -1 * Integer.parseInt(pieces[1]);
                ItemStack stack = inv.getItem(i);
                if (stack == null || stack.getAmount() == 0) {
                    if (amount > 0) {
                        ItemStack newStack = new ItemStack(id, amount, (byte) 0x01, (byte) data);
                        inv.setItem(i, newStack);
                    } else {
                        Logger.getLogger("Minecraft").info("[BBROTHER] Chest restore conflict. Trying to remove from a empty slot");
                    }
                } else {
                    if (stack.getTypeId() != id) {
                        Logger.getLogger("Minecraft").info("[BBROTHER] Chest restore conflict. Different types.");
                    } else {
                        amount = stack.getAmount() + amount;
                        int damage = stack.getDurability();
                        if(amount < 0) {
                            inv.clear(i);
                        } else {
                            ItemStack newStack = new ItemStack(id, amount, (byte)damage, (byte) data);
                            inv.setItem(i, newStack);
                        }
                    }
                }
            }
        } else {
            BigBrother.log.log(Level.WARNING, "[BBROTHER]: Error when restoring chest");
        }
    }

    public void redo(Server server) {
        World worldy = server.getWorlds().get(world);
        if (!((CraftWorld) worldy).getHandle().A.a(x >> 4, z >> 4)) {
            ((CraftWorld) worldy).getHandle().A.d(x >> 4, z >> 4);
        }

        String[] changes = data.split(";");
        Block block = worldy.getBlockAt(x, y, z);
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            Inventory inv = chest.getInventory();
            for (int i = 0; i < changes.length; i++) {
                String change = changes[i];
                if (change.equals(""))
                    continue;
                String[] pieces = change.split(",");
                if (pieces.length != 2)
                    continue;
                int id = 0;
                int data = 0;
                if (pieces[0].contains(":")) {
                    String[] subPieces = pieces[0].split(":");
                    id = Integer.parseInt(subPieces[0]);
                    data = Integer.parseInt(subPieces[1]);
                } else {
                    id = Integer.parseInt(pieces[0]);
                }
                int amount = Integer.parseInt(pieces[1]);
                ItemStack stack = inv.getItem(i);
                if (stack == null || stack.getAmount() == 0) {
                    if (amount > 0) {
                        ItemStack newStack = new ItemStack(id, amount, (byte) 0x01, (byte) data);
                        inv.setItem(i, newStack);
                    } else {
                        Logger.getLogger("Minecraft").info("[BBROTHER] Chest restore conflict. Trying to remove from a empty slot");
                    }
                } else {
                    if (stack.getTypeId() != id) {
                        Logger.getLogger("Minecraft").info("[BBROTHER] Chest restore conflict. Different types.");
                    } else {
                        amount = stack.getAmount() + amount;
                        int damage = stack.getDurability();
                        if(amount < 0) {
                            inv.clear(i);
                        } else {
                            ItemStack newStack = new ItemStack(id, amount, (byte)damage, (byte) data);
                            inv.setItem(i, newStack);
                        }
                    }
                }
            }
        } else {
            BigBrother.log.log(Level.WARNING, "[BBROTHER]: Error when restoring chest");
        }
    }

    public static BBDataBlock getBBDataBlock(String player, int world, int x, int y, int z, int type, String data) {
        return new DeltaChest(player, world, x, y, z, type, data);
    }

}

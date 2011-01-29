package me.taylorkelly.bigbrother.finder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datasource.DataBlockSender;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class HistoryLog extends StickMode {
    private ItemStack oldItem;
    private int slot;
    
    public void initialize(Player player) {
        slot = player.getInventory().getHeldItemSlot();
        oldItem = player.getInventory().getItem(slot);
        if(oldItem != null && oldItem.getAmount() > 0) {
            player.sendMessage(ChatColor.AQUA + "Saving your " + oldItem.getType() + ".");
        }
        player.getInventory().setItem(slot, new ItemStack(Material.LOG, 1));
    }
    
    public void disable(Player player) {
        if(oldItem != null && oldItem.getAmount() > 0) {
            player.sendMessage(ChatColor.AQUA + "Here's your " + oldItem.getType() + " back!");
            player.getInventory().setItem(slot, oldItem);
        }
    }
    
    @Override
    public ArrayList<String> getInfoOnBlock(Block block) {
        ArrayList<BBDataBlock> history = BlockHistory.hist(block);

        ArrayList<String> msgs = new ArrayList<String>();
        if (history.size() == 0) {
            msgs.add(ChatColor.RED + "No edits on this block");
        } else {
            msgs.add(ChatColor.AQUA.toString() + history.size() + " edits on this block"); 
            for (BBDataBlock dataBlock : history) {
                Calendar cal = Calendar.getInstance();
                String DATE_FORMAT = "MMM.d@'" + ChatColor.GRAY + "'kk.mm.ss";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                cal.setTimeInMillis(dataBlock.date * 1000);
                StringBuilder msg = new StringBuilder(sdf.format(cal.getTime()));
                msg.append(ChatColor.WHITE + " - " + ChatColor.YELLOW);
                msg.append(dataBlock.player);
                msg.append(ChatColor.WHITE);
                msg.append(" ");
                msg.append(DataBlockSender.getAction(dataBlock.action));
                if (dataBlock.type != 0) {
                    msg.append(" ");
                    msg.append(Material.getMaterial(dataBlock.type));
                }
                msgs.add(msg.toString());
            }
        }
        return msgs;
    }

    public String getDescription() {
        return "History Log";
    }

    public boolean usesStick() {
        return false;
    }

    public void update(Player player) {
        player.getInventory().setItem(slot, new ItemStack(Material.LOG, 1));
    }

}

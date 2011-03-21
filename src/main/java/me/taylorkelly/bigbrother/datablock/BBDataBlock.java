package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.datablock.explosions.CreeperExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.MiscExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTExplosion;
import me.taylorkelly.bigbrother.datasource.DataBlockSender;

import org.bukkit.Server;

public abstract class BBDataBlock {

    public final static String ENVIRONMENT = "Environment";
    public String player;
    public Action action;
    public int x;
    public int y;
    public int z;
    public String world;
    public int type;
    public String data;
    public long date;

    public static enum Action {

        BLOCK_BROKEN,
        BLOCK_PLACED,
        DESTROY_SIGN_TEXT,
        TELEPORT,
        DELTA_CHEST,
        COMMAND,
        CHAT,
        DISCONNECT,
        LOGIN,
        DOOR_OPEN,
        BUTTON_PRESS,
        LEVER_SWITCH,
        CREATE_SIGN_TEXT,
        LEAF_DECAY,
        FLINT_AND_STEEL,
        TNT_EXPLOSION,
        CREEPER_EXPLOSION,
        MISC_EXPLOSION,
        OPEN_CHEST,
        BLOCK_BURN,
        LAVA_FLOW,
        DROP_ITEM,
        PICKUP_ITEM
    }

    public BBDataBlock(String player, Action action, String world, int x, int y, int z, int type, String data) {
        this.date = System.currentTimeMillis() / 1000;
        this.player = player;
        this.action = action;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
    }

    public void send() {
        DataBlockSender.offer(this);
    }

    public abstract void rollback(Server server);

    public abstract void redo(Server server);

    public static BBDataBlock getBBDataBlock(String player, String world, int x, int y, int z, int type, String data) {
        return null;
    }

    public static BBDataBlock getBBDataBlock(String player, Action action, String world, int x, int y, int z, int type, String data) {
        switch (action) {
            case BLOCK_BROKEN:
                return BrokenBlock.getBBDataBlock(player, world, x, y, z, type, data);
            case BLOCK_PLACED:
                return PlacedBlock.getBBDataBlock(player, world, x, y, z, type, data);
            case DESTROY_SIGN_TEXT:
                return DestroySignText.getBBDataBlock(player, world, x, y, z, type, data);
            case TELEPORT:
                return Teleport.getBBDataBlock(player, world, x, y, z, type, data);
            case DELTA_CHEST:
                return DeltaChest.getBBDataBlock(player, world, x, y, z, type, data);
            case COMMAND:
                return Command.getBBDataBlock(player, world, x, y, z, type, data);
            case CHAT:
                return Chat.getBBDataBlock(player, world, x, y, z, type, data);
            case DISCONNECT:
                return Disconnect.getBBDataBlock(player, world, x, y, z, type, data);
            case LOGIN:
                return Login.getBBDataBlock(player, world, x, y, z, type, data);
            case DOOR_OPEN:
                return DoorOpen.getBBDataBlock(player, world, x, y, z, type, data);
            case BUTTON_PRESS:
                return ButtonPress.getBBDataBlock(player, world, x, y, z, type, data);
            case LEVER_SWITCH:
                return LeverSwitch.getBBDataBlock(player, world, x, y, z, type, data);
            case CREATE_SIGN_TEXT:
                return CreateSignText.getBBDataBlock(player, world, x, y, z, type, data);
            case LEAF_DECAY:
                return LeafDecay.getBBDataBlock(player, world, x, y, z, type, data);
            case FLINT_AND_STEEL:
                return FlintAndSteel.getBBDataBlock(player, world, x, y, z, type, data);
            case TNT_EXPLOSION:
                return TNTExplosion.getBBDataBlock(player, world, x, y, z, type, data);
            case CREEPER_EXPLOSION:
                return CreeperExplosion.getBBDataBlock(player, world, x, y, z, type, data);
            case MISC_EXPLOSION:
                return MiscExplosion.getBBDataBlock(player, world, x, y, z, type, data);
            case OPEN_CHEST:
                return ChestOpen.getBBDataBlock(player, world, x, y, z, type, data);
            case BLOCK_BURN:
                return BlockBurn.getBBDataBlock(player, world, x, y, z, type, data);
            case LAVA_FLOW:
                return LavaFlow.getBBDataBlock(player, world, x, y, z, type, data);
            case DROP_ITEM:
                return DropItem.getBBDataBlock(player, world, x, y, z, type, data);
            case PICKUP_ITEM:
                return PickupItem.getBBDataBlock(player, world, x, y, z, type, data);
            default:
                return null;
        }
    }
}

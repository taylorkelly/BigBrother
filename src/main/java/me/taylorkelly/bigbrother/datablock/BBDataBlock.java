package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datablock.explosions.CreeperExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.MiscExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTExplosion;
import me.taylorkelly.bigbrother.datasource.DataBlockSender;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Server;
import org.bukkit.World;

public abstract class BBDataBlock {

    public final static String ENVIRONMENT = "Environment";
    public BBPlayerInfo player;
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
        PICKUP_ITEM, 
        SIGN_DESTROYED
    }

    public BBDataBlock(String player, Action action, String world, int x, int y, int z, int type, String data) {
        this.date = System.currentTimeMillis() / 1000;
        this.player = BBUsersTable.getInstance().getUserByName(player);
        this.action = action;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
    }
    public BBDataBlock(BBPlayerInfo player, Action action, String world, int x, int y, int z, int type, String data) {
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

    public abstract void rollback(World world);

    public abstract void redo(Server server);

    public static BBDataBlock getBBDataBlock(String player, String world, int x, int y, int z, int type, String data) {
        return null;
    }

    public static BBDataBlock getBBDataBlock(BBPlayerInfo pi, Action action, String world, int x, int y, int z, int type, String data) {
        switch (action) {
            case BLOCK_BROKEN:
                return BrokenBlock.getBBDataBlock(pi, world, x, y, z, type, data);
            case BLOCK_PLACED:
                return PlacedBlock.getBBDataBlock(pi, world, x, y, z, type, data);
            case DESTROY_SIGN_TEXT:
                return DestroySignText.getBBDataBlock(pi, world, x, y, z, type, data);
            case TELEPORT:
                return Teleport.getBBDataBlock(pi, world, x, y, z, type, data);
            case DELTA_CHEST:
                return DeltaChest.getBBDataBlock(pi, world, x, y, z, type, data);
            case COMMAND:
                return Command.getBBDataBlock(pi, world, x, y, z, type, data);
            case CHAT:
                return Chat.getBBDataBlock(pi, world, x, y, z, type, data);
            case DISCONNECT:
                return Disconnect.getBBDataBlock(pi, world, x, y, z, type, data);
            case LOGIN:
                return Login.getBBDataBlock(pi, world, x, y, z, type, data);
            case DOOR_OPEN:
                return DoorOpen.getBBDataBlock(pi, world, x, y, z, type, data);
            case BUTTON_PRESS:
                return ButtonPress.getBBDataBlock(pi, world, x, y, z, type, data);
            case LEVER_SWITCH:
                return LeverSwitch.getBBDataBlock(pi, world, x, y, z, type, data);
            case CREATE_SIGN_TEXT:
                return CreateSignText.getBBDataBlock(pi, world, x, y, z, type, data);
            case LEAF_DECAY:
                return LeafDecay.getBBDataBlock(pi, world, x, y, z, type, data);
            case FLINT_AND_STEEL:
                return FlintAndSteel.getBBDataBlock(pi, world, x, y, z, type, data);
            case TNT_EXPLOSION:
                return TNTExplosion.getBBDataBlock(pi, world, x, y, z, type, data);
            case CREEPER_EXPLOSION:
                return CreeperExplosion.getBBDataBlock(pi, world, x, y, z, type, data);
            case MISC_EXPLOSION:
                return MiscExplosion.getBBDataBlock(pi, world, x, y, z, type, data);
            case OPEN_CHEST:
                return ChestOpen.getBBDataBlock(pi, world, x, y, z, type, data);
            case BLOCK_BURN:
                return BlockBurn.getBBDataBlock(pi, world, x, y, z, type, data);
            case LAVA_FLOW:
                return LavaFlow.getBBDataBlock(pi, world, x, y, z, type, data);
            case DROP_ITEM:
                return DropItem.getBBDataBlock(pi, world, x, y, z, type, data);
            case PICKUP_ITEM:
                return PickupItem.getBBDataBlock(pi, world, x, y, z, type, data);
            case SIGN_DESTROYED:
                return SignDestroyed.getBBDataBlock(pi, world, x, y, z, type, data);
            default:
                return null;
        }
    }
    public static BBDataBlock getBBDataBlock(int plyID, Action action, String world, int x, int y, int z, int type, String data) {
        return getBBDataBlock(BBUsersTable.getInstance().getUserByID(plyID),action,world,x,y,z,type,data);
    }
}

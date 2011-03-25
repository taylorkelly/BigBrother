package me.taylorkelly.bigbrother.rollback;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.WorldManager;

import me.taylorkelly.bigbrother.datablock.BBDataBlock.Action;

public class RollbackPreparedStatement {

    public static String create(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("SELECT bbdata.id, date, player, action, x, y, z, type, data, rbacked, bbworlds.name AS `world`");
        statement.append(" FROM ");
        statement.append(BBSettings.applyPrefix("bbdata") + " AS bbdata");
        statement.append(" INNER JOIN bbworlds ON bbworlds.id = bbdata.world");
        statement.append(" WHERE ");
        statement.append(getActionString());
        if (!rollback.rollbackAll) {
            statement.append(" AND ");
            statement.append(getPlayerString(rollback.players));
        }
        if (rollback.blockTypes.size() > 0) {
            statement.append(" AND ");
            statement.append(getBlockString(rollback.blockTypes));
        }
        if (rollback.time != 0) {
            statement.append(" AND ");
            statement.append("date > ");
            statement.append("'");
            statement.append(rollback.time);
            statement.append("'");
        }
        if (rollback.radius != 0) {
            statement.append(" AND ");
            statement.append("x < ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("x > ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y < ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y > ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z < ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z > ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() - rollback.radius);
            statement.append("'");
            statement.append(" AND bbdata.world = '");
            statement.append(manager.getWorld(rollback.center.getWorld().getName()));
            statement.append("'");
        }
        statement.append(" AND rbacked = '0'");
        statement.append(" ORDER BY bbdata.id DESC");
        statement.append(";");
        return statement.toString();
    }

    private static StringBuilder getBlockString(ArrayList<Integer> blockTypes) {
        StringBuilder ret = new StringBuilder("type IN(");
        for (int i = 0; i < blockTypes.size(); i++) {
            ret.append("'");
            ret.append(blockTypes.get(i));
            ret.append("'");
            if (i + 1 < blockTypes.size()) {
                ret.append(",");
            }
        }
        ret.append(")");
        return ret;
    }

    private static StringBuilder getPlayerString(ArrayList<String> players) {
        StringBuilder ret = new StringBuilder("player IN(");
        for (int i = 0; i < players.size(); i++) {
            ret.append("'");
            ret.append(players.get(i));
            ret.append("'");
            if (i + 1 < players.size()) {
                ret.append(",");
            }
        }
        ret.append(")");
        return ret;
    }

    private static StringBuilder getActionString() {
        // TODO maybe more customizable actions?
        StringBuilder ret = new StringBuilder("action IN(");
        ret.append("'");
        ret.append(Action.BLOCK_BROKEN.ordinal());
        ret.append("','");
        ret.append(Action.BLOCK_PLACED.ordinal());
        ret.append("','");
        ret.append(Action.DELTA_CHEST.ordinal());
        ret.append("','");
        ret.append(Action.CREATE_SIGN_TEXT.ordinal());
        ret.append("','");
        ret.append(Action.DESTROY_SIGN_TEXT.ordinal());
        ret.append("','");
        ret.append(Action.LEAF_DECAY.ordinal());
        ret.append("','");
        ret.append(Action.TNT_EXPLOSION.ordinal());
        ret.append("','");
        ret.append(Action.CREEPER_EXPLOSION.ordinal());
        ret.append("','");
        ret.append(Action.MISC_EXPLOSION.ordinal());
        ret.append("','");
        ret.append(Action.BLOCK_BURN.ordinal());
        ret.append("','");
        ret.append(Action.LAVA_FLOW.ordinal());
        ret.append("')");
        return ret;
    }

    public static String update(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("UPDATE ");
        statement.append(BBSettings.applyPrefix("bbdata"));
        statement.append(" SET rbacked = '1'");
        statement.append(" WHERE ");
        statement.append(getActionString());
        if (!rollback.rollbackAll) {
            statement.append(" AND ");
            statement.append(getPlayerString(rollback.players));
        }
        if (rollback.blockTypes.size() > 0) {
            statement.append(" AND ");
            statement.append(getBlockString(rollback.blockTypes));
        }
        if (rollback.time != 0) {
            statement.append(" AND ");
            statement.append("date > ");
            statement.append("'");
            statement.append(rollback.time);
            statement.append("'");
        }
        if (rollback.radius != 0) {
            statement.append(" AND ");
            statement.append("x < ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("x > ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y < ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y > ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z < ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z > ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() - rollback.radius);
            statement.append("'");
            statement.append(" AND world = ");
            statement.append("'");
            statement.append(rollback.server.getWorlds().indexOf(rollback.center.getWorld()));
            statement.append("'");
            statement.append(" AND world = '");
            statement.append(manager.getWorld(rollback.center.getWorld().getName()));
            statement.append("'");
        }

        statement.append(" AND rbacked = '0'");
        statement.append(";");
        return statement.toString();
    }

    public static String undoStatement(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("UPDATE ");
        statement.append(BBSettings.applyPrefix("bbdata"));
        statement.append(" SET rbacked = '0'");
        statement.append(" WHERE ");
        statement.append(getActionString());
        if (!rollback.rollbackAll) {
            statement.append(" AND ");
            statement.append(getPlayerString(rollback.players));
        }
        if (rollback.blockTypes.size() > 0) {
            statement.append(" AND ");
            statement.append(getBlockString(rollback.blockTypes));
        }
        if (rollback.time != 0) {
            statement.append(" AND ");
            statement.append("date > ");
            statement.append("'");
            statement.append(rollback.time);
            statement.append("'");
        }
        if (rollback.radius != 0) {
            statement.append(" AND ");
            statement.append("x < ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("x > ");
            statement.append("'");
            statement.append(rollback.center.getBlockX() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y < ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("y > ");
            statement.append("'");
            statement.append(rollback.center.getBlockY() - rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z < ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() + rollback.radius);
            statement.append("'");
            statement.append(" AND ");
            statement.append("z > ");
            statement.append("'");
            statement.append(rollback.center.getBlockZ() - rollback.radius);
            statement.append("'");
            statement.append(" AND world = ");
            statement.append("'");
            statement.append(rollback.server.getWorlds().indexOf(rollback.center.getWorld()));
            statement.append("'");
            statement.append(" AND world = '");
            statement.append(manager.getWorld(rollback.center.getWorld().getName()));
            statement.append("'");
        }

        statement.append(" AND rbacked = '1'");
        statement.append(";");
        return statement.toString();
    }
}

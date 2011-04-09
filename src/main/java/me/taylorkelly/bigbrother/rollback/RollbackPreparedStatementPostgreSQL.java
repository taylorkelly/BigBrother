package me.taylorkelly.bigbrother.rollback;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.BBDataBlock.Action;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;
import me.taylorkelly.bigbrother.tablemgrs.BBWorldsTable;

public class RollbackPreparedStatementPostgreSQL extends
		RollbackPreparedStatement {

    public String create(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("SELECT bbdata.id, date, player, action, x, y, z, type, data, rbacked, bbworlds.name AS \"world\"");
        statement.append(" FROM");
        statement.append(" "+BBDataTable.getInstance().getTableName()+ " AS bbdata,");
        statement.append(" "+BBWorldsTable.getInstance().getTableName()+" AS bbworlds, ");
        statement.append(" "+BBUsersTable.getInstance().getTableName()+" AS usr ");
        statement.append(" WHERE ");
        statement.append(" bbworlds.id = bbdata.world AND bbdata.player = usr.id AND ");
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

    private StringBuilder getBlockString(ArrayList<Integer> blockTypes) {
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

    private StringBuilder getPlayerString(ArrayList<String> players) {
        StringBuilder ret = new StringBuilder("usr.name IN (");
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

    private StringBuilder getActionString() {
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

    public String update(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("UPDATE");
        statement.append(" " + BBDataTable.getInstance().getTableName());
        statement.append(" SET rbacked = '1'");
        statement.append(" WHERE ");
        statement.append(getActionString());
        if (!rollback.rollbackAll) {
            statement.append(" AND player IN (SELECT id FROM " + BBUsersTable.getInstance().getTableName()+" AS usr WHERE ");
            statement.append(getPlayerString(rollback.players));
            statement.append(")");
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

    public String undoStatement(Rollback rollback, WorldManager manager) {
        StringBuilder statement = new StringBuilder("UPDATE ");
        statement.append(" "+BBDataTable.getInstance().getTableName() + " AS bbdata,");
        statement.append(" "+BBUsersTable.getInstance().getTableName()+" AS usr ");
        statement.append(" SET rbacked = '0'");
        statement.append(" WHERE ");
        statement.append(" bbdata.player = usr.id AND ");
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

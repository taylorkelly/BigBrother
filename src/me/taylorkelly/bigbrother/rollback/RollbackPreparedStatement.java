package me.taylorkelly.bigbrother.rollback;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.datablock.BBDataBlock;

public class RollbackPreparedStatement {

    public static String create(Rollback rollback) {
        // TODO More variable prepared statements
        StringBuilder statement = new StringBuilder("SELECT * FROM ");
        statement.append(BBDataBlock.BBDATA_NAME);
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
        
        statement.append(" AND rbacked = '0'");
        statement.append(" ORDER BY id DESC");
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
        ret.append(BBDataBlock.BLOCK_BROKEN);
        ret.append("','");
        ret.append(BBDataBlock.BLOCK_PLACED);
        ret.append("','");
        ret.append(BBDataBlock.DELTA_CHEST);
        ret.append("','");
        ret.append(BBDataBlock.CREATE_SIGN_TEXT);
        ret.append("','");
        ret.append(BBDataBlock.DESTROY_SIGN_TEXT);
        ret.append("')");
        return ret;
    }

    public static String update(Rollback rollback) {
        StringBuilder statement = new StringBuilder("UPDATE ");
        statement.append(BBDataBlock.BBDATA_NAME);
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
        
        statement.append(" AND rbacked = '0'");
        statement.append(";");
        return statement.toString();
    }


}

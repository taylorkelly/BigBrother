package me.taylorkelly.bigbrother;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.util.config.ConfigurationNode;

public class BetterNode extends ConfigurationNode {

    protected BetterNode(Map<String, Object> root) {
        super(root);
    }

    public BetterNode() {
        this(new HashMap<String, Object>());
    }

    /**
     * Casts a value to a long. May return null.
     *
     * @param o
     * @return
     */
    private static Long castLong(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof Byte) {
            return (long) (Byte) o;
        } else if (o instanceof Integer) {
            return Long.valueOf((Integer)o);
        } else if (o instanceof Double) {
            return (long) (double) (Double) o;
        } else if (o instanceof Float) {
            return (long) (float) (Float) o;
        } else if (o instanceof Long) {
            return (Long) o;
        } else {
            return null;
        }
    }

    public long getLong(String path, long defaultValue) {
        if (getProperty(path) == null) {
            setProperty(path, defaultValue);
        }
        return castLong(getProperty(path));
    }

    @Override
    public double getDouble(String path, double defaultValue) {
        if (getProperty(path) == null) {
            setProperty(path, defaultValue);
        }
        return super.getDouble(path, defaultValue);
    }

    @Override
    public int getInt(String path, int defaultValue) {
        if (getProperty(path) == null) {
            setProperty(path, defaultValue);
        }
        return super.getInt(path, defaultValue);
    }

    @Override
    public String getString(String path, String defaultValue) {
        if (getProperty(path) == null) {
            setProperty(path, defaultValue);
        }
        return super.getString(path, defaultValue);
    }

    @Override
    public boolean getBoolean(String path, boolean defaultValue) {
        if (getProperty(path) == null) {
            setProperty(path, defaultValue);
        }
        return super.getBoolean(path, defaultValue);
    }
}

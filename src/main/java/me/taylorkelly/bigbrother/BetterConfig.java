package me.taylorkelly.bigbrother;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.util.config.Configuration;

public class BetterConfig extends Configuration {

    public BetterConfig(File file) {
        super(file);
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

    @SuppressWarnings("unchecked")
    @Override
    public BetterNode getNode(String path) {
        if (getProperty(path) == null || !(getProperty(path) instanceof Map)) {
            BetterNode node = new BetterNode();
            setProperty(path, new HashMap<String, Object>());
            return node;
        } else {
            Object raw = getProperty(path);
            return new BetterNode((Map<String, Object>) raw);
        }

    }
}


package me.taylorkelly.bigbrother;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.util.config.ConfigurationNode;

public class BetterNode extends ConfigurationNode{

    protected BetterNode(Map<String, Object> root) {
        super(root);
    }

    public BetterNode() {
        this(new HashMap<String, Object>());
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

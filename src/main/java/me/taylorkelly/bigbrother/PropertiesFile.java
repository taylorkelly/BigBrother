package me.taylorkelly.bigbrother;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class PropertiesFile {
    private HashMap<String, PropertiesEntry> map;
    private File file;
    private boolean modified;

    public PropertiesFile(File file) {
        this.file = file;
        map = new HashMap<String, PropertiesEntry>();
        Scanner scan;
        try {
            if (!file.exists())
                file.createNewFile();
            scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (!line.contains("="))
                    continue;
                int equals = line.indexOf('=');
                int commentIndex = line.length();
                if (line.contains("#")) {
                    commentIndex = line.indexOf('#');
                }

                String key = line.substring(0, equals).trim();
                if (key.equals(""))
                    continue;
                String value = line.substring(equals + 1, commentIndex).trim();
                String comment = "";
                if (commentIndex < line.length() - 1) {
                    comment = line.substring(commentIndex + 1, line.length()).trim();
                }
                map.put(key, new PropertiesEntry(value, comment));
            }
        } catch (FileNotFoundException e) {
            BBLogging.severe("Cannot read file " + file.getName());
        } catch (IOException e) {
            BBLogging.severe("Cannot create file " + file.getName());
        }
    }

    public boolean getBoolean(String key, Boolean defaultValue, String defaultComment) {
        if (map.containsKey(key)) {
            return Boolean.parseBoolean(map.get(key).value);
        } else {
            map.put(key, new PropertiesEntry(defaultValue.toString(), defaultComment));
            modified = true;
            return defaultValue;
        }
    }

    public String getString(String key, String defaultValue, String defaultComment) {
        if (map.containsKey(key)) {
            return map.get(key).value;
        } else {
            map.put(key, new PropertiesEntry(defaultValue, defaultComment));
            modified = true;
            return defaultValue;
        }
    }

    public int getInt(String key, Integer defaultValue, String defaultComment) {
        if (map.containsKey(key)) {
            try {
                return Integer.parseInt(map.get(key).value);
            } catch (Exception e) {
                BBLogging.warning("Trying to get Integer from " + key + ": " + map.get(key).value);
                return defaultValue;
            }
        } else {
            map.put(key, new PropertiesEntry(defaultValue.toString(), defaultComment));
            modified = true;
            return defaultValue;
        }
    }

    public long getLong(String key, Long defaultValue, String defaultComment) {
        if (map.containsKey(key)) {
            try {
                return Long.parseLong(map.get(key).value);
            } catch (Exception e) {
                BBLogging.warning("Trying to get Long from " + key + ": " + map.get(key).value);
                return defaultValue;
            }
        } else {
            map.put(key, new PropertiesEntry(defaultValue.toString(), defaultComment));
            modified = true;
            return defaultValue;
        }
    }

    public double getDouble(String key, Double defaultValue, String defaultComment) {
        if (map.containsKey(key)) {
            try {
                return Double.parseDouble(map.get(key).value);
            } catch (Exception e) {
                BBLogging.warning("Trying to get Double from " + key + ": " + map.get(key).value);
                return defaultValue;
            }
        } else {
            map.put(key, new PropertiesEntry(defaultValue.toString(), defaultComment));
            modified = true;
            return defaultValue;
        }
    }

    public void setDouble(String key, Double globalMemory, String defaultComment) {
        if (map.containsKey(key)) {
            PropertiesEntry entry = map.get(key);
            entry.value = globalMemory.toString();
        } else {
            map.put(key, new PropertiesEntry(globalMemory.toString(), defaultComment));
        }
        modified = true;
    }

    public void save() {
        if (!modified)
            return;
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            if (!file.exists())
                file.createNewFile();
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            for (Entry<String, PropertiesEntry> entry : map.entrySet()) {
                StringBuilder builder = new StringBuilder();
                builder.append(entry.getKey());
                builder.append(" = ");
                builder.append(entry.getValue().value);
                if (!entry.getValue().comment.equals("")) {
                    builder.append("   #");
                    builder.append(entry.getValue().comment);
                }
                bwriter.write(builder.toString());
                bwriter.newLine();
            }
            bwriter.flush();
        } catch (IOException e) {
            BBLogging.severe("IO Exception with file " + file.getName());
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null) {
                    fwriter.close();
                }
            } catch (IOException e) {
                BBLogging.severe("IO Exception with file " + file.getName() + " (on close)");
            }
        }

    }

    private static class PropertiesEntry {
        public String value;
        public String comment;

        public PropertiesEntry(String value, String comment) {
            this.value = value;
            this.comment = comment;
        }
    }
}

package me.taylorkelly.bigbrother;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

class URLReader {

    private static final String url = "http://taylorkelly.me/plugins/BigBrother/BigBrother.updatr";
    private ArrayList<String> versions;
    private String currVersion;

    public URLReader() {
        versions = new ArrayList<String>();
        try {
            URL updateUrl = new URL(url);
            InputStream uin = updateUrl.openStream();
            Scanner sc = new Scanner(uin);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                versions.add(line);
                if (!line.contains("SNAPSHOT")) {
                    currVersion = line;
                }
            }
        } catch (Exception e) {
            BBLogging.severe("Error getting version information.");
        }
    }

    /**
     * Gets the data from a given string
     * @param string The line to get the data from
     * @return The data.
     */
    public static String getField(String string) {
        int start = string.indexOf("=");
        return string.substring(start + 1).trim();
    }

    public boolean versionIsUpToDate(String version) {
        return versions.contains(version);
    }

    public String getUrl() {
        return url;
    }

    public String getCurrVersion() {
        return currVersion;
    }
}

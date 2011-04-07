package me.taylorkelly.bigbrother;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BBLogging {

    public static final Logger log = Logger.getLogger("Minecraft");

    /**
     * Log INFO-level messages with a minimum of cruft.
     * @author N3X15
     */
    public static void info(String message, Throwable e) {
        log.log(Level.INFO, "[BBROTHER] " + message, e);
    }

    /**
     * Log WARNING-level messages with a minimum of cruft.
     * @author N3X15
     */
    public static void warning(String message, Throwable e) {
        log.log(Level.WARNING, "[BBROTHER] " + message, e);
    }

    /**
     * Log SEVERE-level messages with a minimum of cruft.
     * @author N3X15
     */
    public static void severe(String message, Throwable e) {
        log.log(Level.SEVERE, "[BBROTHER] " + message, e);
    }

    /**
     * Log FINE-level messages with a minimum of cruft.
     * @author N3X15
     */
    public static void fine(String message, Throwable e) {
        log.log(Level.FINE, "[BBROTHER] " + message, e);
    }

    /**
     * Log INFO-level messages with a minimum of cruft.
     * @author N3X15
     */
    public static void info(String message) {
        log.log(Level.INFO, "[BBROTHER] " + message);
    }

    /**
     * Log WARNING-level messages with a minimum of cruft.
     * @author N3X15
     */
    public static void warning(String message) {
        log.log(Level.WARNING, "[BBROTHER] " + message);
    }

    /**
     * Log SEVERE-level messages with a minimum of cruft.
     * @author N3X15
     */
    public static void severe(String message) {
        log.log(Level.SEVERE, "[BBROTHER] " + message);
    }

    /**
     * Log FINE-level messages with a minimum of cruft.
     * @author N3X15
     */
    public static void fine(String message) {
        log.log(Level.FINE, "[BBROTHER] " + message);
    }

    public static void debug(String string) {
        if (BBSettings.debugMode) {
            log.log(Level.INFO, "[BBDEBUG] " + string);
        }
    }

    public static void debug(String string, Throwable e) {
        if (BBSettings.debugMode) {
            log.log(Level.INFO, "[BBDEBUG] " + string,e);
        }
    }
}

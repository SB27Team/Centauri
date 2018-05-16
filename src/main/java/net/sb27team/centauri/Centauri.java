package net.sb27team.centauri;

import java.io.File;
import java.util.logging.*;

public class Centauri {
    public static Centauri INSTANCE = new Centauri();
    public static final Logger LOGGER = Logger.getAnonymousLogger();
    public static boolean DEBUG = true;

    static {
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        LOGGER.setLevel(DEBUG ? Level.FINEST : Level.INFO);
        handler.setLevel(DEBUG ? Level.FINEST : Level.INFO);
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
//        LOGGER.setFilter(record -> DEBUG || record.getLevel().intValue() >= Level.INFO.intValue());
    }

    public void shutDown() {
        LOGGER.info("Shutting down...");
        System.exit(0);
    }

    public void closeFile() {
        LOGGER.fine("Closing file...");
    }

    public void openFile(File file) {
        LOGGER.info("Opening " + file.getName());
    }
}

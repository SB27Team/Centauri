package net.sb27team.centauri;

import net.sb27team.centauri.controller.MainMenuController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.logging.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Centauri {
    public static Centauri INSTANCE = new Centauri();
    public static final Logger LOGGER = Logger.getAnonymousLogger();
    public static boolean DEBUG = true;
    private ZipFile openedZipFile = null;
    private File openedFile = null;
    private List<ZipEntry> loadedZipEntries = new ArrayList<>();

    static {
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        LOGGER.setLevel(DEBUG ? Level.FINEST : Level.INFO);
        handler.setLevel(DEBUG ? Level.FINEST : Level.INFO);
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> INSTANCE.shutDown()));
//        LOGGER.setFilter(record -> DEBUG || record.getLevel().intValue() >= Level.INFO.intValue());
    }

    public void shutDown() {
        LOGGER.info("Shutting down...");
        closeFile();
//        System.exit(0);
    }

    public void closeFile() {
        LOGGER.fine("Closing file...");

        if (openedZipFile != null) {
            try {
                openedZipFile.close();
            } catch (IOException e) {
                report(e);
            }
        }

        openedZipFile = null;
        openedFile = null;

        if (loadedZipEntries != null) {
            loadedZipEntries.clear();
        }
        MainMenuController.INSTANCE.updateTree();
    }

    public void openFile(File file) {
        Objects.requireNonNull(file, "File is null");

        LOGGER.info("Opening " + file.getName());

        if (openedFile != null) {
            closeFile();
        }

        MainMenuController.INSTANCE.setStatus("Opening " + file.getAbsolutePath());

        try {
            openedZipFile = new ZipFile(file);
            openedFile = file;
            Enumeration<? extends ZipEntry> entries = openedZipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                this.loadedZipEntries.add(entry);
            }

            MainMenuController.INSTANCE.updateTree();
            MainMenuController.INSTANCE.setStatus("Ready");
        } catch (Exception e) {
            closeFile();
            MainMenuController.INSTANCE.updateTree();
            MainMenuController.INSTANCE.setStatus("Error " + e);
            report(e);
        }
    }

    private void report(Exception e) {
        LOGGER.severe("ERROR: " + e);

        if (DEBUG) {
            e.printStackTrace();
        }
    }

    public List<ZipEntry> getLoadedZipEntries() {
        return loadedZipEntries;
    }

    public File getOpenedFile() {
        return openedFile;
    }
}
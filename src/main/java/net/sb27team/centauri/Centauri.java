package net.sb27team.centauri;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import net.sb27team.centauri.controller.MainMenuController;
import net.sb27team.centauri.controller.utils.Utils;
import net.sb27team.centauri.resource.ResourceManager;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Centauri {
    public static Centauri INSTANCE = new Centauri();
    public static final Logger LOGGER = Logger.getAnonymousLogger();
    public static boolean DEBUG = true;
    private ZipFile openedZipFile = null;
    private File openedFile = null;
    private List<ZipEntry> loadedZipEntries = new ArrayList<>();
    public HashMap<ResourceItem, Tab> resourceTabMap = new HashMap<>();

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

    public Tab openTab(ResourceItem res) {
        Tab tab = new Tab(res.getName().length() > 16 ? res.getName().substring(0, 16) + "..." : res.getName());

//        Label label = new Label()

        addContent(res, tab);

        resourceTabMap.put(res, tab);

        return tab;
    }

    private void addContent(ResourceItem res, Tab tab) {
        {
            Label label = new Label("LOADING...", new ImageView(ResourceManager.ANIMATED_LOADING_ICON));
            label.setTextAlignment(TextAlignment.CENTER);
            label.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
            tab.setContent(label);
        }
        File f = new File(res.getEntry().getName());
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];

        System.out.println(type);

        if (type.equals("image") || Utils.isImage(res.getEntry().getName())) {
            try {
                ScrollPane pane = new ScrollPane();
                Image image = new Image(getInputStream(res));
                pane.setContent(new ImageView(image));
                tab.setContent(pane);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Label label = new Label("No preview available :o");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font("Roboto", FontWeight.BOLD, 20));

        tab.setContent(label);
    }

    public InputStream getInputStream(ResourceItem res) throws IOException {
        return getInputStream(res.getEntry());
    }

    public InputStream getInputStream(ZipEntry entry) throws IOException {
        return openedZipFile.getInputStream(entry);
    }

    public void shutDown() {
        LOGGER.info("Shutting down...");
        closeFile();
//        System.exit(0);
    }

    public void closeFile() {
        LOGGER.fine("Closing file...");

        resourceTabMap.clear();

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
        resourceTabMap.clear();

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
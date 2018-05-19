package net.sb27team.centauri;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import net.sb27team.centauri.controller.MainMenuController;
import net.sb27team.centauri.controller.utils.Utils;
import net.sb27team.centauri.editors.IEditor;
import net.sb27team.centauri.resource.ResourceManager;
import net.sb27team.centauri.utils.Configuration;

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
    private Configuration config = new Configuration();

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

        tab.setOnClosed(e -> resourceTabMap.remove(res));

        resourceTabMap.put(res, tab);

        return tab;
    }

    private void addContent(ResourceItem res, Tab tab) {
        Label label = new Label("LOADING...", new ImageView(ResourceManager.ANIMATED_LOADING_ICON));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        tab.setContent(label);

        File f = new File(res.getEntry().getName());
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];

        String ext = "";
        if (f.getName().lastIndexOf(".") != -1)
            ext = f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length()).toLowerCase();

        System.out.println(type);

        List<IEditor> compatEditors = Utils.getSupportedEditors(type, f.getName());
        String editor = config.get("exts." + ext + ".default", compatEditors.get(0).name());
        try {
            compatEditors.stream().filter(e -> editor.equals(e.name())).findFirst()
                    .orElseThrow(() -> {
                        config.set("exts." + type + ".default", compatEditors.get(0).name());
                        label.setText("Editor for this file was not found.");
                        return new IllegalStateException("Default editor not found");
                    })
                    .open(res, getInputStream(res.getEntry()), tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        config.save();
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
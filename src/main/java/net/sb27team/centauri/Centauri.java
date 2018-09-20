/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri;

import com.google.common.io.ByteStreams;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Pair;
import net.sb27team.centauri.actions.ActionManager;
import net.sb27team.centauri.actions.impl.CloseAction;
import net.sb27team.centauri.controller.MainMenuController;
import net.sb27team.centauri.discord.DiscordIntegration;
import net.sb27team.centauri.editors.IEditor;
import net.sb27team.centauri.explorer.FileComponent;
import net.sb27team.centauri.resource.ResourceManager;
import net.sb27team.centauri.utils.Alerts;
import net.sb27team.centauri.utils.Configuration;
import net.sb27team.centauri.utils.IProgressCallback;
import net.sb27team.centauri.utils.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Centauri {
    public static final Centauri INSTANCE = new Centauri();
    public static final Logger LOGGER = Logger.getAnonymousLogger();
    public static final boolean DEBUG = true;
    private ZipFile openedZipFile = null;
    private File openedFile = null;
    private List<ZipEntry> loadedZipEntries = new ArrayList<>();
    private HashMap<ZipEntry, byte[]> updatedData = new HashMap<>();
    //(file, editor), javafx tab
    public HashMap<Pair<FileComponent, String>, Tab> resourceTabMap = new HashMap<>();
    private Configuration config = new Configuration();

    static {
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        LOGGER.setLevel(DEBUG ? Level.FINEST : Level.INFO);
        handler.setLevel(DEBUG ? Level.FINEST : Level.INFO);
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
        Runtime.getRuntime().addShutdownHook(new Thread(INSTANCE::shutDown));
//        LOGGER.setFilter(record -> DEBUG || record.getLevel().intValue() >= Level.INFO.intValue());
    }

    private List<Thread> threads = new ArrayList<>();
    private Charset encoding = StandardCharsets.UTF_8;

    public static void applyStyle(Scene scene, Stage stage) {
        stage.getIcons().add(ResourceManager.CENTAURI_ICON);
        scene.getStylesheets().add("/gui/style.css");
    }

    public static void applyStyle(Dialog dialog) {
        applyStyle(dialog.getDialogPane().getScene(), (Stage) dialog.getDialogPane().getScene().getWindow());
    }

    public Tab openTab(FileComponent res) {
        return openTab(res, getOptimalEditor(res));
    }

    public Tab openTab(FileComponent res, Optional<IEditor> editor) {
        Tab tab = new Tab(res.getName().length() > 16 ? res.getName().substring(0, 16) + "..." : res.getName());

        addContent(res, tab, editor);

        String name = editor.isPresent() ? editor.get().name() : "Error";
        tab.setOnClosed(e -> resourceTabMap.remove(new Pair<>(res, name)));

        resourceTabMap.put(new Pair<>(res, name), tab);

        return tab;
    }

    public Optional<IEditor> getOptimalEditor(final FileComponent component) {
        File f = new File(component.getZipEntry().getName());
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];

        String ext = "";
        if (f.getName().lastIndexOf(".") != -1)
            ext = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();


        List<IEditor> compatEditors = Utils.getSupportedEditors(type, f.getName());
        String editor = config.get("exts." + ext + ".default", compatEditors.get(0).name());
        Optional<IEditor> optional = compatEditors.stream().filter(e -> editor.equals(e.name())).findFirst();
        if (!optional.isPresent()) {
            config.set("exts." + type + ".default", compatEditors.get(0).name());
        }
        return optional;
    }

    private void addContent(FileComponent res, Tab tab, Optional<IEditor> editor) {
        StackPane pane = new StackPane();
        Label label = new Label("LOADING...", new ImageView(ResourceManager.ANIMATED_LOADING_ICON));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        pane.getChildren().add(label);
        tab.setContent(pane);
        try {
            editor.orElseThrow(() -> {
                label.setText("Editor for this file was not found.");
                return new IllegalStateException("Default editor not found");
            }).open(res, getInputStream(res.getZipEntry()), tab);
        } catch (Exception e) {
            Centauri.INSTANCE.report(e);
        }
    }

    public InputStream getInputStream(FileComponent res) throws IOException {
        return getInputStream(res.getZipEntry());
    }

    public InputStream getInputStream(ZipEntry entry) throws IOException {
        return updatedData.containsKey(entry) ? new ByteArrayInputStream(updatedData.get(entry)) : openedZipFile.getInputStream(entry);
    }

    public void updateData(FileComponent component, byte[] newData) {
        updateData(component.getZipEntry(), newData);
    }

    public void updateData(ZipEntry entry, byte[] newData) {
        if (newData == null) {
            newData = new byte[0];
        }

        updatedData.put(entry, newData);
        LOGGER.fine(MessageFormat.format("Updated {0} ({1} bytes)", entry.getName(), newData.length));
    }

    public void shutDown() {
        LOGGER.info("Shutting down...");
        for (Thread thread : threads) {
            thread.interrupt();
        }
        threads.clear();
        config.save();
        DiscordIntegration.stopRPC();
    }

    public void report(Exception e) {
        LOGGER.severe("ERROR: " + e);

        if (DEBUG) {
            e.printStackTrace();
        }

        Alerts.exceptionDialog(e);
    }

    public List<ZipEntry> getLoadedZipEntries() {
        return loadedZipEntries;
    }

    public File getOpenedFile() {
        return openedFile;
    }

    public ZipFile getOpenedZipFile() {
        return openedZipFile;
    }

    public HashMap<ZipEntry, byte[]> getUpdatedData() {
        return updatedData;
    }

    public HashMap<Pair<FileComponent, String>, Tab> getResourceTabMap() {
        return resourceTabMap;
    }

    public void setOpenedZipFile(ZipFile openedZipFile) {
        this.openedZipFile = openedZipFile;
    }

    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }

    public void addThread(Thread thread) {
        threads.add(thread);
    }

    public void openFile(File file) {
        if (file == null) return;
        if (!file.exists()) {
            Alerts.errorDialog("The entered File does not Exists");
            return;
        }

        Centauri.INSTANCE.getResourceTabMap().clear();

        Centauri.LOGGER.info("Opening " + file.getName());

        if (Centauri.INSTANCE.getOpenedFile() != null) {
            ActionManager.INSTANCE.call(CloseAction.class);
        }

        MainMenuController.INSTANCE.setStatus("Opening " + file.getAbsolutePath());

        try {
            Centauri.INSTANCE.setOpenedZipFile(new ZipFile(file));
            Centauri.INSTANCE.setOpenedFile(file);
            Enumeration<? extends ZipEntry> entries = Centauri.INSTANCE.getOpenedZipFile().entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Centauri.INSTANCE.getLoadedZipEntries().add(entry);
            }

            MainMenuController.INSTANCE.updateTree();
            MainMenuController.INSTANCE.setStatus("Ready");

            /* Only set if no exception catched */
            config.set("openedfile", file.getAbsolutePath());
        } catch (Exception e) {
            ActionManager.INSTANCE.call(CloseAction.class);
            MainMenuController.INSTANCE.updateTree();
            MainMenuController.INSTANCE.setStatus("Error " + e);
            Centauri.INSTANCE.report(e);
        }

        MainMenuController.INSTANCE.updateRPC();
    }

    public void export(File file, IProgressCallback callback) {
        try {
            int entryCount = Centauri.INSTANCE.getOpenedZipFile().size();

            Enumeration<? extends ZipEntry> entries = Centauri.INSTANCE.getOpenedZipFile().entries();
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));

            int currentEntry = 0;


            List<String> exported = new ArrayList<>();

            for (Map.Entry<ZipEntry, byte[]> zipEntryEntry : Centauri.INSTANCE.getUpdatedData().entrySet()) {
                zos.putNextEntry(new ZipEntry(zipEntryEntry.getKey().getName()));
                zos.write(zipEntryEntry.getValue());

                exported.add(zipEntryEntry.getKey().getName());

                zos.closeEntry();

                if (Thread.interrupted()) throw new InterruptedException();
            }

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (!exported.contains(entry.getName())) {
                    zos.putNextEntry(new ZipEntry(entry.getName()));
                    ByteStreams.copy(Centauri.INSTANCE.getInputStream(entry), zos);
                    zos.closeEntry();

                    if (Thread.interrupted()) throw new InterruptedException();
                }

                currentEntry++;
                callback.progressUpdate(currentEntry, entryCount);
            }
            zos.close();
            callback.end(true);
        } catch (InterruptedException e) {
            LOGGER.fine("Exporting was cancelled");
        } catch (Exception e) {
            Centauri.INSTANCE.report(e);
            callback.end(false);
        }
    }

    public ClassNode getClassNode(FileComponent fileComponent) throws IOException {
        return getClassNode(fileComponent.getZipEntry());
    }

    private ClassNode getClassNode(ZipEntry zipEntry) throws IOException {
        byte[] data = ByteStreams.toByteArray(getInputStream(zipEntry));

        ClassReader reader = new ClassReader(data);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        return node;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public Configuration getConfig() {
        return config;
    }

    public byte[] classNodeToBytes(ClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }
}
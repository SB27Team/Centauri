/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.controller;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Pair;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.Main;
import net.sb27team.centauri.actions.*;
import net.sb27team.centauri.actions.impl.ExportAction;
import net.sb27team.centauri.actions.impl.OpenAction;
import net.sb27team.centauri.discord.DiscordIntegration;
import net.sb27team.centauri.editors.IEditor;
import net.sb27team.centauri.explorer.*;
import net.sb27team.centauri.resource.ResourceManager;
import net.sb27team.centauri.scanner.Scanner;
import net.sb27team.centauri.utils.Alerts;
import net.sb27team.centauri.utils.Mapper;
import net.sb27team.centauri.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class MainMenuController {

    public static MainMenuController INSTANCE = new MainMenuController();

    @FXML
    public MenuItem open;

    @FXML
    private VBox root;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private TabPane tabPane;
    @FXML
    private WebView homeWV;
    @FXML
    private TreeView<ExplorerItem> resourceTree = new TreeView<>();
    @FXML
    private Label rightStatus;
    @FXML
    private Label leftStatus;

    @FXML
    private Menu mapperFiles;

    @FXML
    private MenuItem export = new MenuItem();

    @FXML
    private Menu openWith = new Menu();

    private FileExplorer fileExplorer;


    public void initialize() {
        INSTANCE = this;
        root.setOnDragOver(e -> {
            e.acceptTransferModes(TransferMode.COPY);
        });
        contextMenu.setOnShowing(event -> {
            openWith.getItems().clear();
            Component component = resourceTree.getSelectionModel().getSelectedItem().getValue().getComponent();
            if (component instanceof FileComponent) {
                FileComponent file = (FileComponent) component;
                File f = new File(file.getZipEntry().getName());
                String mimetype = new MimetypesFileTypeMap().getContentType(f);
                String type = mimetype.split("/")[0];

                Utils.getSupportedEditors(type, f.getName()).forEach(editor -> {
                    MenuItem item = new MenuItem(editor.name());
                    item.setOnAction(event1 -> {
                        Centauri.LOGGER.fine("Open With: " + file.getName() + " " + editor.name());
                        openOrSwitchToTab(file, Optional.of(editor));
                    });
                    openWith.getItems().add(item);
                });
            }
            if (openWith.getItems().isEmpty()) openWith.getItems().add(new MenuItem("Not Supported"));
        });
        ActionManager.INSTANCE.applyMenuItem(export, ExportAction.class);
        ActionManager.INSTANCE.applyMenuItem(open, OpenAction.class);
        resourceTree.setCellFactory(treeView -> {
            TreeCell<ExplorerItem> cell = new TreeCell<ExplorerItem>() {
                @Override
                protected void updateItem(ExplorerItem obj, boolean empty) {
                    super.updateItem(obj, empty);
                    setText(empty ? null : obj.getName());
                    if (!empty && obj.getName() != null && getTreeItem() != null) {
                        if (obj.isHome()) {
                            setGraphic(new ImageView(ResourceManager.HOME_ICON));
                        } else if (obj.getComponent() instanceof Directory) {
                            setGraphic(new ImageView(ResourceManager.FOLDER_ICON));
                        } else setGraphic(new ImageView(ResourceManager.getIconForName(obj.getName())));
                    } else setGraphic(null);
                }
            };
            return cell;
        });
        homeWV.getEngine().loadContent("<html><body style=\"background: rgb(34, 34, 34);\"><h1 style=\"color: white; font-family: Arial, Helvetica, sans-serif;\">Loading...</h1></body></html>", "text/html");
        new Thread(() -> {
            try {
                HttpClient client = HttpClients.createDefault();

                HttpGet get = new HttpGet("https://raw.githubusercontent.com/SB27Team/Centauri/master/README.md");
                String md = IOUtils.toString(client.execute(get).getEntity().getContent());

                HttpPost post = new HttpPost("https://api.github.com/markdown");
                JsonObject json = new JsonObject();
                json.addProperty("text", md);
                json.addProperty("mode", "gfm");
                json.addProperty("context", "github/gollum");
                post.setEntity(new StringEntity(json.toString()));
                String html = IOUtils.toString(client.execute(post).getEntity().getContent());
                Platform.runLater(() ->
                        homeWV.getEngine().loadContent("<html><body style=\"background: rgb(34, 34, 34); font-family: Arial, Helvetica, sans-serif; color: white;\">" + html + "</body></html>", "text/html"));
            } catch (Exception ex) {
                Centauri.INSTANCE.report(ex);
                Platform.runLater(() ->
                        homeWV.getEngine().loadContent("<html><body style=\"background: rgb(34, 34, 34);\"><h1 style=\"color: white; font-family: Arial, Helvetica, sans-serif;\">Failed to load the home page!</h1></body></html>", "text/html"));
            }
        }, "Web Loader").start();
    }

    @FXML
    public void onDrag(DragEvent e) {
        if (e.getDragboard().hasFiles()) {
            List<File> files = e.getDragboard().getFiles();

            if (files.size() > 0) {
                DataFactory factory = new DataFactory();
                factory.putData(DataKeys.OPEN_FILE, files.get(0));
                e.setDropCompleted(true);
            }
        }
    }

    @FXML
    public void scannerMenuItemClicked(ActionEvent e) {
        if (Centauri.INSTANCE.getOpenedFile() != null) {
            try {
                Parent root = FXMLLoader.load(Main.class.getResource("/gui/scanner.fxml"));
                Scene scene = new Scene(root, 500, 300);
                Stage stage = new Stage();
                stage.setTitle("Scanner");
                stage.setScene(scene);
                stage.getIcons().add(ResourceManager.CENTAURI_ICON);
                stage.show();
                new Thread(() ->
                        ScannerController.INSTANCE.setData(new Scanner(Centauri.INSTANCE.getOpenedFile()).runScan())).start();
            } catch (IOException e1) {
                Centauri.INSTANCE.report(e1);
            }
        }
    }

    @FXML
    public void quitMenuItemClicked(ActionEvent e) {
//        Centauri.INSTANCE.shutDown();
        System.exit(0);
    }

    @FXML
    public void closeMenuItemClicked(ActionEvent e) {
        Centauri.INSTANCE.closeFile();
    }

    public void setStatus(String text) {
        leftStatus.setText(text);
    }

    public void updateTree() {
        tabPane.getTabs().clear();

        if (Centauri.INSTANCE.getOpenedFile() == null) {
            resourceTree.setRoot(null);
            return;
        }

        fileExplorer = new FileExplorer(new HashSet<>(Centauri.INSTANCE.getLoadedZipEntries()), Centauri.INSTANCE.getOpenedFile().getName(), "/");
        resourceTree.setRoot(fileExplorer.getMain());

        EventHandler<MouseEvent> mouseEventHandle = this::handleMouseClicked;
        resourceTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
    }

    private void handleMouseClicked(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;

        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            Component component = resourceTree.getSelectionModel().getSelectedItem().getValue().getComponent();
            if (component instanceof FileComponent) {
                Centauri.LOGGER.fine("Node click: " + component.getName());
                openOrSwitchToTab((FileComponent) component);
            }
        }
    }

    @FXML
    public void clearMapper() {
        Mapper.getInstance().clear();
        mapperFiles.getItems().clear();
    }

    @FXML
    public void addFileToMapper() {
        File file = Utils.openFileDialog(null);
        Mapper.getInstance().addFile(file);
        mapperFiles.getItems().add(new MenuItem(file.getName()));
    }

    private void openOrSwitchToTab(FileComponent res) {
        openOrSwitchToTab(res, Centauri.INSTANCE.getOptimalEditor(res));
    }

    private void openOrSwitchToTab(FileComponent res, Optional<IEditor> editor) {
        String name = editor.isPresent() ? editor.get().name() : "Error";

        if (Centauri.INSTANCE.resourceTabMap.containsKey(new Pair<>(res, name))) {
            Centauri.LOGGER.fine("Moved to tab " + res.getName() + " - " + name + " " + Centauri.INSTANCE.resourceTabMap.size());
            tabPane.getSelectionModel().select(Centauri.INSTANCE.resourceTabMap.get(new Pair<>(res, name)));
        } else {
            Centauri.LOGGER.fine("Creating " + res.getName() + " via " + name);
            Tab tab = Centauri.INSTANCE.openTab(res, editor);

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        }
        updateRPC();
    }

    public void updateRPC() {
        DiscordIntegration.updateRPC(Centauri.INSTANCE.getOpenedFile() == null ? null : Centauri.INSTANCE.getOpenedFile().getName(), tabPane.getSelectionModel().getSelectedItem() == null ? null : tabPane.getSelectionModel().getSelectedItem().getText());
    }

    public FileExplorer getFileExplorer() {
        return fileExplorer;
    }

    public void setFileExplorer(FileExplorer fileExplorer) {
        this.fileExplorer = fileExplorer;
    }
}

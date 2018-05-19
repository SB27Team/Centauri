/*
 * Copyright (c) 2018 SinC (superblaubeere27, Cubixy, Xc3pt1on, Cython)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWAR
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
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.Main;
import net.sb27team.centauri.controller.utils.Utils;
import net.sb27team.centauri.explorer.*;
import net.sb27team.centauri.resource.ResourceManager;
import net.sb27team.centauri.scanner.Scanner;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;

public class MainMenuController {
    public static MainMenuController INSTANCE = new MainMenuController();
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

    private FileExplorer fileExplorer;


    public void initialize() {
        INSTANCE = this;
        root.setOnDragOver(e -> {
            e.acceptTransferModes(TransferMode.COPY);
        });
        resourceTree.setCellFactory(treeView -> {
            TreeCell<ExplorerItem> cell = new TreeCell<ExplorerItem>(){
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
                ex.printStackTrace();
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
                Centauri.INSTANCE.openFile(files.get(0));
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
                e1.printStackTrace();
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

    @FXML
    public void openMenuItemClicked(ActionEvent e) {
        try {
            Centauri.INSTANCE.openFile(Utils.openFileDialog(null));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
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

    private void openOrSwitchToTab(FileComponent res) {
        if (Centauri.INSTANCE.resourceTabMap.containsKey(res)) {
            tabPane.getSelectionModel().select(Centauri.INSTANCE.resourceTabMap.get(res));
        } else {
            Tab tab = Centauri.INSTANCE.openTab(res);

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        }
    }

    public FileExplorer getFileExplorer() {
        return fileExplorer;
    }

    public void setFileExplorer(FileExplorer fileExplorer) {
        this.fileExplorer = fileExplorer;
    }
}

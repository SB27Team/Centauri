package net.sb27team.centauri.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.Main;
import net.sb27team.centauri.ResourceItem;
import net.sb27team.centauri.controller.utils.Utils;
import net.sb27team.centauri.resource.ResourceManager;
import net.sb27team.centauri.scanner.Scanner;
import net.sb27team.centauri.scanner.resource.ResourceManager;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;

public class MainMenuController {
    public static MainMenuController INSTANCE = new MainMenuController();

    @FXML
    public WebView homeWV;
    @FXML
    private TreeView<ResourceItem> resourceTree;
    @FXML
    private Label rightStatus;
    @FXML
    private Label leftStatus;


    public void initialize() {
        INSTANCE = this;
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
        if (Centauri.INSTANCE.getOpenedFile() == null) {
            resourceTree.setRoot(null);
            return;
        }

        HashMap<String, List<ZipEntry>> packageEntryMap = new HashMap<>();

        for (ZipEntry zipEntry : Centauri.INSTANCE.getLoadedZipEntries()) {
            if (zipEntry.isDirectory()) {
                continue;
            }
            String parent = new File(zipEntry.getName()).getParent();

            if (!packageEntryMap.containsKey(parent)) {
                packageEntryMap.put(parent, new ArrayList<>());
            }
            packageEntryMap.get(parent).add(zipEntry);
        }

        TreeItem<ResourceItem> rootItem = new TreeItem<>(new ResourceItem(Centauri.INSTANCE.getOpenedFile().getName()), new ImageView(ResourceManager.FOLDER_ICON));

//        for (Map.Entry<String, List<ZipEntry>> stringListEntry : packageEntryMap.entrySet()) {
//
//
//        }
        packageEntryMap.entrySet().stream().sorted((o1, o2) -> {
            String obj1 = o1.getKey();
            String obj2 = o2.getKey();

            if (obj1 == null) {
                return 1;
            }
            if (obj2 == null) {
                return -1;
            }
            if (obj1.equals( obj2 )) {
                return 0;
            }
            return obj1.compareTo(obj2);
        }).forEach(entry -> {
            if (entry.getKey() == null) {
                for (ZipEntry zipEntry : entry.getValue()) {
                    TreeItem<ResourceItem> item = new TreeItem<>(new ResourceItem(new File(zipEntry.getName()).getName(), zipEntry), new ImageView(ResourceManager.getIconForName(zipEntry.getName())));
                    rootItem.getChildren().add(item);
                }
            } else {
                TreeItem<ResourceItem> pack = new TreeItem<>(new ResourceItem(entry.getKey().replace('\\', '.').replace('/', '.')), new ImageView(ResourceManager.FOLDER_ICON));

                for (ZipEntry zipEntry : entry.getValue()) {
                    TreeItem<ResourceItem> item = new TreeItem<>(new ResourceItem(new File(zipEntry.getName()).getName(), zipEntry), new ImageView(ResourceManager.getIconForName(zipEntry.getName())));
                    pack.getChildren().add(item);
                }

                rootItem.getChildren().add(pack);
            }
        });
        EventHandler<MouseEvent> mouseEventHandle = this::handleMouseClicked;
        resourceTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);

        resourceTree.setRoot(rootItem);
    }
    private void handleMouseClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            TreeItem<ResourceItem> name = resourceTree.getSelectionModel().getSelectedItem();
            System.out.println("Node click: " + name.getValue().getEntry().getName());
        }
    }
}

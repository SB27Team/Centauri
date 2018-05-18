package net.sb27team.centauri.controller;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.controller.utils.Utils;
import net.sb27team.centauri.resource.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;

public class MainMenuController {
    public static MainMenuController INSTANCE = new MainMenuController();

    @FXML
    public WebView homeWV;
    @FXML
    private TreeView<String> resourceTree;
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

        TreeItem<String> rootItem = new TreeItem<>(Centauri.INSTANCE.getOpenedFile().getName(), new ImageView(ResourceManager.FOLDER_ICON));

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
            if (obj1.equals(obj2)) {
                return 0;
            }
            return obj1.compareTo(obj2);
        }).forEach(entry -> {
            if (entry.getKey() == null) {
                for (ZipEntry zipEntry : entry.getValue()) {
                    rootItem.getChildren().add(new TreeItem<>(new File(zipEntry.getName()).getName(), new ImageView(ResourceManager.getIconForName(zipEntry.getName()))));
                }
            } else {
                TreeItem<String> pack = new TreeItem<>(entry.getKey().replace('\\', '.').replace('/', '.'), new ImageView(ResourceManager.FOLDER_ICON));

                for (ZipEntry zipEntry : entry.getValue()) {
                    pack.getChildren().add(new TreeItem<>(new File(zipEntry.getName()).getName(), new ImageView(ResourceManager.getIconForName(zipEntry.getName()))));
                }

                rootItem.getChildren().add(pack);
            }
        });

        resourceTree.setRoot(rootItem);
    }

}

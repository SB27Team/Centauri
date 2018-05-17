package net.sb27team.centauri.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.controller.utils.Utils;
import net.sb27team.centauri.scanner.resource.ResourceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;

public class MainMenuController {
    public static MainMenuController INSTANCE = new MainMenuController();

    @FXML
    private TreeView<String> resourceTree;
    @FXML
    private Label rightStatus;
    @FXML
    private Label leftStatus;


    public void initialize() {
        INSTANCE = this;
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
            if (obj1.equals( obj2 )) {
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

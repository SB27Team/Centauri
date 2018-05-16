package net.sb27team.centauri.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.controller.utils.Utils;

public class MainMenuController {

    @FXML
    private TreeView treeView;

    public void initialize() {

    }

    @FXML
    public void quitMenuItemClicked(ActionEvent e) {
        Centauri.INSTANCE.shutDown();
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

}

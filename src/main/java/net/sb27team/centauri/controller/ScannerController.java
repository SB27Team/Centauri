package net.sb27team.centauri.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.sb27team.centauri.scanner.Scanner;

import java.util.List;

/*
 * Created by Cubxity on 18/05/2018
 */
public class ScannerController {
    public static ScannerController INSTANCE = new ScannerController();

    @FXML
    public TableView<Scanner.Threat> table;
    @FXML
    public TableColumn<Object, Object> level;
    @FXML
    public TableColumn name;
    @FXML
    public TableColumn desc;
    @FXML
    public TableColumn loc;
    @FXML
    public Label resultTxt;

    public void initialize() {
        INSTANCE = this;
        level.setCellValueFactory(new PropertyValueFactory<>("level"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        desc.setCellValueFactory(new PropertyValueFactory<>("description"));
        loc.setCellValueFactory(new PropertyValueFactory<>("location"));
    }

    public void setData(List<Scanner.Threat> threats) {
        Platform.runLater(() -> {
            table.setItems(FXCollections.observableArrayList(threats));
            resultTxt.setText(threats.size() + " Potential threats, " + threats.stream().filter(t -> t.getLevel() == Scanner.Level.CRITICAL).count() + " critical threats");
        });
    }
}

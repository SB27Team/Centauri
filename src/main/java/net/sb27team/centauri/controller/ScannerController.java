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

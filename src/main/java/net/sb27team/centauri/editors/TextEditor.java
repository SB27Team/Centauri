/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.editors;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.explorer.FileComponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TextEditor implements IEditor {

    @Override
    public void open(FileComponent file, InputStream stream, Tab tab) {
        CheckBox editable = new CheckBox("Editable");
        Button update = new Button("Update");
        update.setDisable(true);

        FlowPane header = new FlowPane(Orientation.HORIZONTAL);

        header.getChildren().add(editable);
        header.getChildren().add(update);

        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(false);

        BorderPane pane = new BorderPane();
        pane.setCenter(area);
        pane.setTop(header);

        editable.setOnAction(event -> {
            update.setDisable(!editable.isSelected());
            area.setEditable(editable.isSelected());
        });

        update.setOnAction(event -> file.update(area.getText().getBytes(StandardCharsets.UTF_8)));

        Platform.runLater(() -> {
            tab.setContent(pane);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) area.appendText(line + System.lineSeparator());
            } catch (IOException e) {
                Centauri.INSTANCE.report(e);
            }
        });
    }

    @Override
    public boolean supports(String type, String name) {
        return !type.equals(".dll") && !type.equals(".exe");
    }

    @Override
    public String name() {
        return "Text Editor";
    }


    @Override
    public int priority() {
        return PRIORITY_TEXT_EDITOR;
    }
}

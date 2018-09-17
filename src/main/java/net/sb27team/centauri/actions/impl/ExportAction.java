/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.actions.impl;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.actions.Action;
import net.sb27team.centauri.actions.DataFactory;
import net.sb27team.centauri.utils.Alerts;
import net.sb27team.centauri.utils.IProgressCallback;
import net.sb27team.centauri.utils.Utils;

import java.io.File;
import java.text.MessageFormat;

public class ExportAction extends Action {

    @Override
    public void call(DataFactory factory) {
        if (Centauri.INSTANCE.getOpenedFile() == null) {
            Alerts.noFileOpened();
            return;
        }


        File file = Utils.saveFileDialog(null);

        FlowPane pane = new FlowPane(Orientation.HORIZONTAL);
        ProgressBar progressBar = new ProgressBar();
        Label state = new Label("Exporting... (Preparing)");

        pane.getChildren().add(state);
        pane.getChildren().add(progressBar);

        DialogPane dialogPane = new DialogPane();

        dialogPane.getChildren().add(pane);

        Alert alert = new Alert(Alert.AlertType.NONE, "Exporting...", ButtonType.CANCEL);
        alert.getDialogPane().setContent(pane);


        Thread t = new Thread(() -> Centauri.INSTANCE.export(file, new IProgressCallback() {

            @Override
            public void progressUpdate(int curr, int max) {
                Platform.runLater(() -> {
                    state.setText(MessageFormat.format("Exporting {0}% ({1}/{2})", curr * 100 / max, curr, max));
                    progressBar.setProgress(curr / (double) max);
                });
            }

            @Override
            public void end(boolean success) {
                Platform.runLater(() -> {
                    alert.close();

                    if (success) new Alert(Alert.AlertType.INFORMATION, "Exported", ButtonType.OK).showAndWait();
                });
            }
        }));

        alert.resultProperty().addListener(event -> t.interrupt());
        alert.show();

        t.start();

    }

    @Override
    public String getDisplayName() {
        return "Export";
    }
}

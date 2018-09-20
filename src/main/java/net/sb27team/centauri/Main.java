/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.sb27team.centauri.actions.ActionManager;
import net.sb27team.centauri.actions.impl.ExitAction;
import net.sb27team.centauri.discord.DiscordIntegration;
import net.sb27team.centauri.resource.ResourceManager;

import java.io.File;

public class Main extends Application {

    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Platform.setImplicitExit(false);
        ResourceManager.loadResources();

        DiscordIntegration.init();

        Parent root = FXMLLoader.load(Main.class.getResource("/gui/mainGui.fxml"));

        Scene scene = new Scene(root, 1000, 500);

        stage.setTitle("Centauri");
        stage.setScene(scene);
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            ActionManager.INSTANCE.call(ExitAction.class);
            windowEvent.consume();
        });
        stage.getIcons().add(ResourceManager.CENTAURI_ICON);
        stage.show();

        String file = Centauri.INSTANCE.getConfig().get("openedfile", "");
        if (file != null && !file.isEmpty()) {
            Centauri.INSTANCE.openFile(new File(file));
        }
    }

    public static void main(String args[]) {
        launch(args);
    }
}

package net.sb27team.centauri.controller.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class Utils {

    public static File openFileDialog(Window window) {
        FileChooser chooser = new FileChooser();

        File f = chooser.showOpenDialog(window);

        if (f == null) {
            throw new IllegalStateException();
        }

        return f;
    }

}

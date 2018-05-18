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

    public static boolean isImage(String name) {
        if (name.lastIndexOf(".") == -1)
            return false;
        String str = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        System.out.println(str);

        return str.equals("png") || str.equals("bmp") || str.equals("jpg") || str.equals("jpeg") || str.equals("gif");
    }
}

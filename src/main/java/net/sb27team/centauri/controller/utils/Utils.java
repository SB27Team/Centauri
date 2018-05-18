package net.sb27team.centauri.controller.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.sb27team.centauri.editors.FFEditor;
import net.sb27team.centauri.editors.HexEditor;
import net.sb27team.centauri.editors.IEditor;
import net.sb27team.centauri.editors.ImageEditor;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private static List<IEditor> editors = Arrays.asList(
            new FFEditor(),
            new ImageEditor(),
            new HexEditor() // should be the last one
    );

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

    public static List<IEditor> getSupportedEditors(String type, String name) {
        return editors.stream().filter(e -> e.supports(type, name)).collect(Collectors.toList());
    }
}

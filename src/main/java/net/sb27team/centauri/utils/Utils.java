/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.sb27team.centauri.editors.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    private static List<IEditor> editors = Arrays.asList(
            new FFEditor(),
            new CFREditor(),
            new ProcyonEditor(),
//            new JDEditor(),
            new SyntaxHighligtEditor(),
            new TextEditor(),
            new ImageEditor(),
            new HexEditor(),
            new JASMEditor() // should be the last one
    );

    public static File openFileDialog(Window window) {
        FileChooser chooser = new FileChooser();
        File f = chooser.showOpenDialog(window);

        if (f == null) {
            throw new IllegalStateException();
        }

        return f;
    }

    public static File saveFileDialog(Window window) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR File", "*.jar"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Archive", "*.zip"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*"));
        File f = chooser.showSaveDialog(window);

        if (f == null) {
            throw new IllegalStateException();
        }

        return f;
    }

    public static boolean isImage(String name) {
        if (name.lastIndexOf(".") == -1)
            return false;
        String str = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();

        return str.equals("png") || str.equals("bmp") || str.equals("jpg") || str.equals("jpeg") || str.equals("gif");
    }

    public static List<IEditor> getSupportedEditors(String type, String name) {
        return editors.stream().filter(e -> e.supports(type, name)).collect(Collectors.toList());
    }

    public static String unixPathReplacer(String absolutePath) {
        return absolutePath.replace("(", "\\)").replace(" ", "\\ ");
    }

    public static OSType getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OSType.WINDOWS;
        } else if (osName.contains("mac")) {
            return OSType.MACOSX;
        } else if (osName.contains("unix")) {
            return OSType.UNIX;
        } else if (osName.contains("linux")) {
            return OSType.UNIX;
        } else {
            return osName.contains("sonos") ? OSType.SOLARIS : OSType.UNKNOWN;
        }
    }

    public enum OSType {
        WINDOWS,
        MACOSX,
        UNIX,
        SOLARIS,
        UNKNOWN;
    }



}

/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.resource;

import javafx.scene.image.Image;

import java.io.File;
import java.util.HashMap;

public class ResourceManager {
    public static Image JAVA_ICON;
    public static Image FOLDER_ICON;
    public static Image FILE_ICON;
    public static Image HOME_ICON;
    public static Image CENTAURI_ICON;
    public static Image IMAGE_ICON;
    public static Image XML_ICON;
    private static Image JSON_ICON;
    private static Image LIBRARY_ICON;
    public static Image ANIMATED_LOADING_ICON;
    public static Image SAD_FACE;

    private static HashMap<String, Image> resourceFileSuffixMap = new HashMap<>();

    public static void loadResources() {
        CENTAURI_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/centauri.png"));
        JAVA_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/java.png"));
        FOLDER_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/folder.png"));
        FILE_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/file.png"));
        HOME_ICON = new Image("/icons/home.png");
        IMAGE_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/picture.png"));
        XML_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/xml.png"));
        JSON_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/json.png"));
        LIBRARY_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/books.png"));
        SAD_FACE = new Image(ResourceManager.class.getResourceAsStream("/icons/sad.png"));
        ANIMATED_LOADING_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/loading.gif"));

        resourceFileSuffixMap.put("java", JAVA_ICON);
        resourceFileSuffixMap.put("class", JAVA_ICON);
        resourceFileSuffixMap.put("png", IMAGE_ICON);
        resourceFileSuffixMap.put("bmp", IMAGE_ICON);
        resourceFileSuffixMap.put("jpg", IMAGE_ICON);
        resourceFileSuffixMap.put("jpeg", IMAGE_ICON);
        resourceFileSuffixMap.put("gif", IMAGE_ICON);
        resourceFileSuffixMap.put("xml", XML_ICON);
        resourceFileSuffixMap.put("json", JSON_ICON);
        resourceFileSuffixMap.put("dll", LIBRARY_ICON);
        resourceFileSuffixMap.put("lib", LIBRARY_ICON);
        resourceFileSuffixMap.put("so", LIBRARY_ICON);
    }

    public static Image getIconForName(String name) {
        String fileName = new File(name).getName();

        if (fileName.lastIndexOf(".") != -1) {
            return resourceFileSuffixMap.getOrDefault(fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase(), FILE_ICON);
        }

        return FILE_ICON;
    }
}

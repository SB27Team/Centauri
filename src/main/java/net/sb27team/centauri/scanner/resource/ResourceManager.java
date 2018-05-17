package net.sb27team.centauri.scanner.resource;

import javafx.scene.image.Image;

import java.io.File;
import java.util.HashMap;

public class ResourceManager {
    public static Image JAVA_ICON;
    public static Image FOLDER_ICON;
    public static Image FILE_ICON;
    public static Image CENTAURI_ICON;
    public static Image IMAGE_ICON;
    public static Image XML_ICON;
    private static Image JSON_ICON;
    private static Image LIBRARY_ICON;

    private static HashMap<String, Image> resourceFileSuffixMap = new HashMap<>();

    public static void loadResources() {
        CENTAURI_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/centauri.png"));
        JAVA_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/java.png"));
        FOLDER_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/folder.png"));
        FILE_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/file.png"));
        IMAGE_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/picture.png"));
        XML_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/xml.png"));
        JSON_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/json.png"));
        LIBRARY_ICON = new Image(ResourceManager.class.getResourceAsStream("/icons/books.png"));

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

package net.sb27team.centauri.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Mapper {

    private static Mapper instance = new Mapper();

    private Mapper() {}

    private HashMap<String, String> map = new HashMap<>();

    public void clear() {
        map.clear();
    }

    private static final String[] SEPERATORS = {", ", ",", ":"};

    public void addFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            int skip = 0;
            while ((line = reader.readLine()) != null) {
                String[] split = new String[0];
                for (String seperator : SEPERATORS) {
                    if (split.length == 2) break;
                    split = line.split(seperator);
                }
                if(split.length == 2) map.put(split[0], split[1]);
                else skip++;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Mapped " + map.size() + " values!\nSkipped: " + skip + " lines!", ButtonType.OK);
            alert.setTitle("Mapping Complete");
            alert.setHeaderText("Mapping Complete");
            alert.show();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Failed reading File!", ButtonType.CLOSE).show();
        }
    }

    public String replace(String text) {
        for (Map.Entry<String, String> entry : map.entrySet())
            text = text.replaceAll(entry.getKey(), entry.getValue());
        return text;
    }


    public static Mapper getInstance() {
        return instance;
    }
}

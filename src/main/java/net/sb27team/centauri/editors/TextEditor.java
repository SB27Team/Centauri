package net.sb27team.centauri.editors;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import net.sb27team.centauri.explorer.FileComponent;

import javax.activation.MimetypesFileTypeMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class TextEditor implements IEditor {

    private static final String[] EXTENSIONS = {"md", "sql", "xml", "html"};

    @Override
    public void open(FileComponent file, InputStream stream, Tab tab) {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(false);
        area.setFont(Font.font("Consolas", 13));
        Platform.runLater(() -> {
            tab.setContent(area);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) area.appendText(line + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
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
}

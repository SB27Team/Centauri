package net.sb27team.centauri.editors;

import com.google.common.io.ByteStreams;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import javafx.application.Platform;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.controller.MainMenuController;
import net.sb27team.centauri.explorer.FileComponent;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.*;
import java.nio.file.Files;

public class ProcyonEditor extends AbstractCodeEditor {

    @Override
    String getContext(FileComponent classFile, File jar) throws IOException {
        File temp = File.createTempFile("centauri", "_tmp.class");
        Files.write(temp.toPath(), ByteStreams.toByteArray(Centauri.INSTANCE.getInputStream(classFile)));

        Platform.runLater(() -> MainMenuController.INSTANCE.setStatus("Decompiling: " + classFile + "..."));
        try (StringWriter writer = new StringWriter()){
            Decompiler.decompile(
                "java/lang/String",
                new PlainTextOutput(writer),
                DecompilerSettings.javaDefaults()
            );
            Platform.runLater(() -> MainMenuController.INSTANCE.setStatus("Ready"));
            return writer.toString();
        }
    }

    @Override
    String getSyntax() {
        return SyntaxConstants.SYNTAX_STYLE_JAVA;
    }

    @Override
    public boolean supports(String type, String name) {
        return name.endsWith(".class");
    }

    @Override
    public String name() {
        return "Procyon Decompiler";
    }
}

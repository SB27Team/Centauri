package net.sb27team.centauri.actions.impl;

import com.google.common.io.ByteStreams;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.actions.Action;
import net.sb27team.centauri.actions.DataFactory;
import net.sb27team.centauri.utils.Alerts;
import net.sb27team.centauri.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportAction extends Action {

    @Override
    public void call(DataFactory factory) {
        if (Centauri.INSTANCE.getOpenedFile() == null) {
            Alerts.noFileOpened();
            return;
        }


        File file = Utils.saveFileDialog(null);

        if (file == null) return;

        FlowPane pane = new FlowPane(Orientation.HORIZONTAL);
        ProgressBar progressBar = new ProgressBar();
        Label state = new Label("Exporting... (Preparing)");

        pane.getChildren().add(state);
        pane.getChildren().add(progressBar);

        DialogPane dialogPane = new DialogPane();

        dialogPane.getChildren().add(pane);

        Alert alert = new Alert(Alert.AlertType.NONE, "Exporting...", ButtonType.CANCEL);
        alert.getDialogPane().setContent(pane);
        alert.show();

        try {
            int entryCount = Centauri.INSTANCE.getOpenedZipFile().size();

            Enumeration<? extends ZipEntry> entries = Centauri.INSTANCE.getOpenedZipFile().entries();
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));

            int currentEntry = 0;



            List<String> exported = new ArrayList<>();

            for (Map.Entry<ZipEntry, byte[]> zipEntryEntry : Centauri.INSTANCE.getUpdatedData().entrySet()) {
                zos.putNextEntry(new ZipEntry(zipEntryEntry.getKey().getName()));
                zos.write(zipEntryEntry.getValue());

                exported.add(zipEntryEntry.getKey().getName());

                zos.closeEntry();
            }

            while (entries.hasMoreElements()) {
                state.setText("Exporting... " + (currentEntry + 1) + "/" + entryCount);
                ZipEntry entry = entries.nextElement();

                if (!exported.contains(entry.getName())) {
                    zos.putNextEntry(new ZipEntry(entry.getName()));
                    ByteStreams.copy(Centauri.INSTANCE.getInputStream(entry), zos);
                    zos.closeEntry();
                }

                currentEntry++;
                progressBar.setProgress(entryCount / (currentEntry + 1) * 100);
            }
            zos.close();
        } catch (Exception e) {
            Centauri.INSTANCE.report(e);
            alert.close();
            return;
        }

        alert.close();
        new Alert(Alert.AlertType.INFORMATION, "Exported", ButtonType.OK).showAndWait();
    }

    @Override
    public String getDisplayName() {
        return "Export";
    }
}

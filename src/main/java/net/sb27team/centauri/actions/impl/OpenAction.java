package net.sb27team.centauri.actions.impl;

import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.actions.Action;
import net.sb27team.centauri.actions.DataFactory;
import net.sb27team.centauri.actions.DataKey;
import net.sb27team.centauri.actions.DataKeys;
import net.sb27team.centauri.controller.MainMenuController;
import net.sb27team.centauri.utils.Utils;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class OpenAction extends Action {
    @Override
    public void call(DataFactory factory) {
        try {
            File file = factory.getData(DataKeys.OPEN_FILE);
            if (file == null) file = Utils.openFileDialog(null);
            if (file == null) return;

            Centauri.INSTANCE.getResourceTabMap().clear();

            Centauri.LOGGER.info("Opening " + file.getName());

            if (Centauri.INSTANCE.getOpenedFile() != null) {
                Centauri.INSTANCE.closeFile();
            }

            MainMenuController.INSTANCE.setStatus("Opening " + file.getAbsolutePath());

            try {
                Centauri.INSTANCE.setOpenedZipFile(new ZipFile(file));
                Centauri.INSTANCE.setOpenedFile(file);
                Enumeration<? extends ZipEntry> entries = Centauri.INSTANCE.getOpenedZipFile().entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    Centauri.INSTANCE.getLoadedZipEntries().add(entry);
                }

                MainMenuController.INSTANCE.updateTree();
                MainMenuController.INSTANCE.setStatus("Ready");
            } catch (Exception e) {
                Centauri.INSTANCE.closeFile();
                MainMenuController.INSTANCE.updateTree();
                MainMenuController.INSTANCE.setStatus("Error " + e);
                Centauri.INSTANCE.report(e);
            }

            MainMenuController.INSTANCE.updateRPC();
        } catch (Exception e1) {
            Centauri.INSTANCE.report(e1);
        }
    }

    @Override
    public String getDisplayName() {
        return "Open...";
    }

}

package net.sb27team.centauri.actions.impl;

import javafx.application.Platform;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.actions.Action;
import net.sb27team.centauri.actions.DataFactory;
import net.sb27team.centauri.controller.MainMenuController;

import java.io.IOException;

public class CloseAction extends Action {

    @Override
    public void call(DataFactory factory) {
        Centauri.LOGGER.fine("Closing file...");

        Centauri.INSTANCE.getResourceTabMap().clear();

        if (Centauri.INSTANCE.getOpenedZipFile() != null) {
            try {
                Centauri.INSTANCE.getOpenedZipFile().close();
            } catch (IOException e) {
                Centauri.INSTANCE.report(e);
            }
        }

        Centauri.INSTANCE.setOpenedFile(null);
        Centauri.INSTANCE.setOpenedZipFile(null);

        if (Centauri.INSTANCE.getLoadedZipEntries() != null) {
            Centauri.INSTANCE.getLoadedZipEntries().clear();
        }
        Platform.runLater(() -> MainMenuController.INSTANCE.updateTree());
        MainMenuController.INSTANCE.updateRPC();
    }

    @Override
    public String getDisplayName() {
        return "Close";
    }
}

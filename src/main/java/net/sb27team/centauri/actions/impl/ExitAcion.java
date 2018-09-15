package net.sb27team.centauri.actions.impl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import net.sb27team.centauri.actions.Action;
import net.sb27team.centauri.actions.DataFactory;

import java.util.Optional;

public class ExitAcion extends Action {

    @Override
    public void call(DataFactory factory) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setContentText("Are you sure that you want to Exit Centauri?");

        ButtonType exit = new ButtonType("Exit");

        alert.getButtonTypes().setAll(exit, ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == exit) {
            System.exit(0);
        }
    }

    @Override
    public String getDisplayName() {
        return "Exit";
    }
}

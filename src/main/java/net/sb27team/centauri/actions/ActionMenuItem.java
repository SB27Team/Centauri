package net.sb27team.centauri.actions;

import javafx.scene.control.MenuItem;

public class ActionMenuItem extends MenuItem {

    private Class<? extends Action> action;

    public ActionMenuItem(Class<? extends Action> action) {
        this.action = action;
        setOnAction(event -> {
            ActionManager.INSTANCE.call(action);
        });
    }
}

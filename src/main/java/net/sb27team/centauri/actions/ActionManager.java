package net.sb27team.centauri.actions;

import com.google.common.reflect.ClassPath;
import javafx.scene.control.MenuItem;
import net.sb27team.centauri.Centauri;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum ActionManager {

    INSTANCE;

    HashMap<Class<? extends Action>, Action> actions = new HashMap<>();

    ActionManager() {
        try {
            for (ClassPath.ClassInfo info : ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive("net.sb27team.centauri.actions.impl")) {
                Class<?> clazz = info.load();
                if (Action.class.isAssignableFrom(clazz)) {
                    actions.putIfAbsent((Class<? extends Action>) clazz, (Action) clazz.newInstance());
                }
            }
        } catch (IOException | ReflectiveOperationException ex) {
            Centauri.INSTANCE.report(ex);
        }
    }

    public void call(Class<? extends Action> action, DataFactory factory) {
        actions.get(action).call(factory);
    }

    public void call(Class<? extends Action> action) {
        call(action, new DataFactory());
    }

    public void applyMenuItem(MenuItem menuItem, Class<? extends Action> actionClazz) {
        Action action = actions.get(actionClazz);
        menuItem.setText(action.getDisplayName());
        menuItem.setOnAction(event -> action.call(new DataFactory()));
    }

}

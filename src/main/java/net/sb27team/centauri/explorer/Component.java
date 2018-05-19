package net.sb27team.centauri.explorer;

import com.google.common.collect.Lists;
import net.sb27team.centauri.controller.MainMenuController;

import java.util.ArrayList;
import java.util.List;

public class Component {

    private String name;
    private Directory parent;

    public Component(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public List<Directory> getParents() {
        List<Directory> parents = new ArrayList<>();
        Directory currentParent = parent;
        while (currentParent.getParent() != MainMenuController.INSTANCE.getFileExplorer().getMainPackage()) {
            parents.add(currentParent.getParent());
            currentParent = currentParent.getParent();
        }
        return parents;
    }

    public String getFullPath() {
        StringBuilder builder = new StringBuilder();
        Lists.reverse(getParents()).forEach(directory -> builder.append(directory.getName()));
        return builder.append(name).toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }
}

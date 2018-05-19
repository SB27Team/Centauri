package net.sb27team.centauri.explorer;

public class ExplorerItem {

    private String name;
    private Component component;

    private boolean home = false;

    public ExplorerItem(String name, Component component) {
        this.name = name;
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }
}

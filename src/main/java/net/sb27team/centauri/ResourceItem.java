package net.sb27team.centauri;

import java.util.zip.ZipEntry;

public class ResourceItem {
    private String name;
    private ZipEntry entry;

    public ResourceItem(String name, ZipEntry entry) {
        this.name = name;
        this.entry = entry;
    }

    public ResourceItem(String name) {
        this.name = name;
        this.entry = null;
    }

    public String getName() {
        return name;
    }

    public ZipEntry getEntry() {
        return entry;

    }

    public boolean isDirectory() {
        return entry == null;
    }

    @Override
    public String toString() {
        return name;
    }
}

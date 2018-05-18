package net.sb27team.centauri;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ResourceItem that = (ResourceItem) o;

        return Objects.equals(name, that.name) &&
                Objects.equals(entry, that.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, entry);
    }
}

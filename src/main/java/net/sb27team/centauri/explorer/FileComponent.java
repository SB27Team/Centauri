package net.sb27team.centauri.explorer;

import java.util.zip.ZipEntry;

public class FileComponent extends Component {

    private ZipEntry zipEntry;

    public FileComponent(String name, Directory parent, ZipEntry zipEntry) {
        super(name, parent);
        this.zipEntry = zipEntry;
    }

    public ZipEntry getZipEntry() {
        return zipEntry;
    }

    public void setZipEntry(ZipEntry zipEntry) {
        this.zipEntry = zipEntry;
    }
}

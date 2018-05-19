package net.sb27team.centauri.explorer;

import java.util.List;

public class Directory extends Component {

    private List<Component> files;

    public Directory(String name, Directory parent, List<Component> files) {
        super(name, parent);
        this.files = files;
    }

    public List<Component> getFiles() {
        return files;
    }

    public void setFiles(List<Component> files) {
        this.files = files;
    }
}

package net.sb27team.centauri.explorer;

import com.google.common.collect.Lists;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import net.sb27team.centauri.resource.ResourceManager;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;

public class FileExplorer {

    private Directory mainPackage;
    private boolean compremise = true, packageFirst = true, sortType = false, flattern = false, showFileType = true;
    private String separator;

    public FileExplorer(Set<ZipEntry> files, String projekt, String separator) {
        this.separator = separator;
        mainPackage = new Directory(projekt, null, new ArrayList<>());

        for (ZipEntry zipEntry : files) {
            if (zipEntry.isDirectory()) continue;
            String file = zipEntry.getName();

            String name = file.substring(file.lastIndexOf(separator)+1);
            String[] path = file.substring(0, file.lastIndexOf(separator)).split(separator);

            //System.out.println("name=" + name + " path=" + file.substring(0, file.lastIndexOf(separator)));

            Directory dir = mainPackage;
            for (String pa : path) {
                boolean found = false;
                for (Component comp : dir.getFiles()){
                    if (comp instanceof Directory && comp.getName().equals(pa)) {
                        dir = (Directory) comp;
                        found = true;
                    }
                }
                if (!found) {
                    Directory directory = new Directory(pa, dir, new ArrayList<>());
                    dir.getFiles().add(directory);
                    dir = directory;
                }
            }

            dir.getFiles().add(new FileComponent(name, dir, zipEntry));
        }

        update();
    }

    private TreeItem<ExplorerItem> main;

    public void update() {
        main = getNodesForDirectory(mainPackage);
        main.setExpanded(main.getChildren().size() != 0);
        main.getValue().setHome(true);
    }

    private TreeItem<ExplorerItem> getNodesForDirectory(Directory directory) {
        TreeItem<ExplorerItem> root = new TreeItem<>(new ExplorerItem(directory.getName(), directory));
        //root.setExpanded(directory.getFiles().size() != 0);

        List<Component> list = directory.getFiles();
        list.sort((t1, t2) -> {
            int comp = 0;
            if (packageFirst) {
                comp = Boolean.compare(t2 instanceof Directory, t1 instanceof Directory);
            }
            if (comp == 0) {
                comp = sortType && !(t1 instanceof Directory) && !(t2 instanceof Directory)?
                        FilenameUtils.getExtension(t1.getName()).compareTo(FilenameUtils.getExtension(t2.getName())) :
                        t1.getName().compareTo(t2.getName());
            }
            return comp;
        });
        for (Component component : list) {
            if(component instanceof Directory) {
                if (flattern) {
                    StringBuilder builder = new StringBuilder();
                    Directory direct = (Directory) component;

                    Lists.reverse(direct.getParents()).forEach(directory1 -> builder.append(directory1.getName()).append(separator));
                    builder.append(direct.getName());

                    TreeItem<ExplorerItem> dir = getNodesForDirectory(direct);
                    dir.setValue(new ExplorerItem(builder.toString(), direct));
                    main.getChildren().add(dir);
                } else {
                    StringBuilder name = new StringBuilder(component.getName());
                    Directory current = (Directory) component;
                    while (compremise && current.getFiles().size() == 1 && current.getFiles().get(0) instanceof Directory) {
                        current = (Directory) current.getFiles().get(0);
                        name.append(separator).append(current.getName());
                    }
                    TreeItem<ExplorerItem> dir = getNodesForDirectory(current);
                    dir.setValue(new ExplorerItem(name.toString(), current));
                    root.getChildren().add(dir);
                }
            } else {
                TreeItem<ExplorerItem> file = new TreeItem<>(new ExplorerItem(showFileType ? component.getName() : FilenameUtils.getBaseName(component.getName()), component));
                root.getChildren().add(file);
            }
        }
        return root;
    }

    public TreeItem<ExplorerItem> getMain() {
        return main;
    }

    public void setMain(TreeItem<ExplorerItem> main) {
        this.main = main;
    }

    public Directory getMainPackage() {
        return mainPackage;
    }

    public void setMainPackage(Directory mainPackage) {
        this.mainPackage = mainPackage;
    }
}

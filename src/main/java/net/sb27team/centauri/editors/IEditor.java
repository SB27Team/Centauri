package net.sb27team.centauri.editors;

import javafx.scene.control.Tab;
import net.sb27team.centauri.ResourceItem;

import java.io.InputStream;

/*
 * Created by Cubxity on 18/05/2018
 */
public interface IEditor {
    void open(ResourceItem file, InputStream stream, Tab tab);

    boolean supports(String type, String name);

    String name();
}

package net.sb27team.centauri.editors;

import javafx.scene.control.Tab;

import java.io.File;
import java.io.InputStream;

/*
 * Created by Cubxity on 18/05/2018
 */
public class HexEditor implements IEditor {

    @Override
    public void open(File file, InputStream stream, Tab tab) {

    }

    @Override
    public boolean supports(String type, String name) {
        return true;
    }

    @Override
    public String name() {
        return "Hex editor";
    }
}

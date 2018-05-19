package net.sb27team.centauri.editors;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.sb27team.centauri.ResourceItem;
import net.sb27team.centauri.controller.utils.Utils;

import java.io.InputStream;

/*
 * Created by Cubxity on 18/05/2018
 */
public class ImageEditor implements IEditor {
    @Override
    public void open(ResourceItem file, InputStream stream, Tab tab) {
        try {
            ScrollPane pane = new ScrollPane();
            Image image = new Image(stream);
            pane.setContent(new ImageView(image));
            tab.setContent(pane);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean supports(String type, String name) {
        return type.equals("image") || Utils.isImage(name);
    }

    @Override
    public String name() {
        return "Image viewer";
    }
}

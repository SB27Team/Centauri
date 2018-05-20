/*
 * Copyright (c) 2018 SinC (superblaubeere27, Cubixy, Xc3pt1on, Cython)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWAR
 */
package net.sb27team.centauri.editors;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.sb27team.centauri.controller.utils.Utils;
import net.sb27team.centauri.explorer.FileComponent;

import java.io.InputStream;

/*
 * Created by Cubxity on 18/05/2018
 */
public class ImageEditor implements IEditor {

    @Override
    public void open(FileComponent file, InputStream stream, Tab tab) {
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

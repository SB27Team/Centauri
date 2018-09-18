/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.editors.bytecode;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.editors.IEditor;
import net.sb27team.centauri.explorer.FileComponent;
import net.sb27team.centauri.resource.ResourceManager;

import java.io.InputStream;

public class BytecodeEditor implements IEditor {

    @Override
    public void open(FileComponent file, InputStream stream, Tab tab) {
        try {
            tab.setContent(
//                    Utils.scrollPane(
                    new BytecodeEditorPane(file.getClassNode(), file::updateClassNode)
//                    )
            );
        } catch (Exception e) {
            Label l = new Label("  The class file isn't valid", new ImageView(ResourceManager.SAD_FACE));
            l.setTextAlignment(TextAlignment.CENTER);
            l.setFont(javafx.scene.text.Font.font("Roboto", FontWeight.BOLD, 20));
            tab.setContent(l);
            Centauri.INSTANCE.report(e);
        }
    }

    @Override
    public boolean supports(String type, String name) {
        return name.endsWith(".class");
    }

    @Override
    public String name() {
        return "BytecodeEditor";
    }


    @Override
    public int priority() {
        return PRIORITY_SPECIAL_EDITOR;
    }
}

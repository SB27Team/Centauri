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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import net.sb27team.centauri.utils.Alerts;
import net.sb27team.centauri.utils.NameUtils;
import org.objectweb.asm.tree.ClassNode;

public class BytecodeEditorPane extends GridPane {
    private int access;

    BytecodeEditorPane(ClassNode classNode, IClassUpdateCallback callback) {
        Button changeAccess = new Button("Edit");
        changeAccess.setOnAction(e -> AccessEditor.showDialog(MemberType.CLASS, classNode.access, acc -> access = acc));
        TextField className = new TextField(classNode.name);
        Label classNameStateLabel = new Label();
        className.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replace('.', '/');
            className.setText(newValue);

            if (!NameUtils.checkClassName(newValue)) {
                classNameStateLabel.setText("Invalid classname. (please use '/' instead of '.')");
            } else {
                classNode.name = className.getText();
                classNameStateLabel.setText("Name is valid");
            }
        });
        TextField sourceFile = new TextField(classNode.sourceFile == null ? "" : classNode.sourceFile);
        sourceFile.textProperty().addListener(e -> classNode.sourceFile = sourceFile.getText());

        TextField classVersion = new TextField();
        Label classVersionStateLabel = new Label();
        classVersion.textProperty().addListener((observableValue, oldValue, newValue) -> {
            int i;

            try {
                i = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                Alerts.errorDialog("Not a number: " + e.getMessage());
                classVersionStateLabel.setText("Not a number: " + e.getMessage());
                classVersion.setText(oldValue);
                return;
            }

            classVersionStateLabel.setText("Java " + NameUtils.versionToString(i));
            classNode.version = i;
        });
        classVersion.setText(Integer.toString(classNode.version));

        int index = 0;

        addRow(index++, new Label("Name: "), className, classNameStateLabel);
        addRow(index++, new Label("Access: "), changeAccess);
        addRow(index++, new Label("SourceFile: "), sourceFile);
        addRow(index++, new Label("Version (Number): "), classVersion, classVersionStateLabel);
        addRow(index++);

        Button updateButton = new Button("Update");

        updateButton.setOnAction(e -> callback.updateClass(classNode));

        addRow(index++, updateButton);
    }

}

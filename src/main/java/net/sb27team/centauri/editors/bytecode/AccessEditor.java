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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.WindowEvent;
import net.sb27team.centauri.Centauri;
import org.objectweb.asm.Opcodes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;

public class AccessEditor extends Dialog<Integer> implements EventHandler<ActionEvent> {
    private int currentAccess;
    private TextField accessField = new TextField();
    private HashMap<CheckBox, Integer> accesses = new HashMap<>();

    private AccessEditor(MemberType type, int currentAccess) {
        this.currentAccess = currentAccess;

        registerAccesses(type);

        setTitle("Access editor (" + type + ")");
        setResizable(true);


        getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            getDialogPane().getScene().getWindow().hide();
            windowEvent.consume();
        });


        BorderPane pane = new BorderPane();
        FlowPane accessPane = new FlowPane();
        accessPane.setPrefWrapLength(800.0);
//        FlowPane bottomPane = new FlowPane(Orientation.VERTICAL);

        for (CheckBox key : accesses.keySet()) {
            accessPane.getChildren().add(key);
        }

        accessField.setEditable(false);
//        bottomPane.getChildren().add(accessField);

        pane.setCenter(accessPane);
        pane.setBottom(accessField);


//        getDialogPane().getButtonTypes().add(ButtonType.APPLY);
//        Node closeButton = getDialogPane().lookupButton(ButtonType.APPLY);
//        closeButton.managedProperty().bind(closeButton.visibleProperty());

        getDialogPane().getChildren().add(pane);

        Centauri.applyStyle(this);

        updateAccess();

        setOnShown(e -> getDialogPane().getScene().getWindow().centerOnScreen());
    }

    public static void showDialog(MemberType type, int currentAccess, Consumer<Integer> callback) {
        AccessEditor editor = new AccessEditor(type, currentAccess);
        editor.showAndWait();

        if (callback != null) {
            callback.accept(editor.currentAccess);
        }
    }

    private void registerAccesses(MemberType type) {
        if (type == MemberType.CLASS || type == MemberType.FIELD || type == MemberType.METHOD) {
            registerAccess("public", Opcodes.ACC_PUBLIC);
            registerAccess("private", Opcodes.ACC_PRIVATE);
            registerAccess("protected", Opcodes.ACC_PROTECTED);
        }
        if (type == MemberType.FIELD || type == MemberType.METHOD) {
            registerAccess("static", Opcodes.ACC_STATIC);
        }
        if (type == MemberType.CLASS || type == MemberType.FIELD || type == MemberType.METHOD || type == MemberType.PARAMETER) {
            registerAccess("final", Opcodes.ACC_FINAL);
        }
        if (type == MemberType.CLASS) {
            registerAccess("super", Opcodes.ACC_SUPER);
            registerAccess("interface", Opcodes.ACC_INTERFACE);
            registerAccess("annotation", Opcodes.ACC_ANNOTATION);
            registerAccess("module", Opcodes.ACC_MODULE);
            registerAccess("enum", Opcodes.ACC_ENUM);
        }
        if (type == MemberType.METHOD) {
            registerAccess("synchronized", Opcodes.ACC_SYNCHRONIZED);
            registerAccess("bridge", Opcodes.ACC_BRIDGE);
            registerAccess("varargs", Opcodes.ACC_VARARGS);
            registerAccess("native", Opcodes.ACC_NATIVE);
            registerAccess("strictfp", Opcodes.ACC_STRICT);
        }

        if (type == MemberType.CLASS || type == MemberType.METHOD) {
            registerAccess("abstract", Opcodes.ACC_ABSTRACT);
        }
        if (type == MemberType.FIELD) {
            registerAccess("volatile", Opcodes.ACC_VOLATILE);
            registerAccess("transient", Opcodes.ACC_TRANSIENT);
        }
        if (type == MemberType.MODULE) {
            registerAccess("open", Opcodes.ACC_OPEN);
            registerAccess("transitive", Opcodes.ACC_TRANSITIVE);
            registerAccess("static_phase", Opcodes.ACC_STATIC_PHASE);
        }
        if (type == MemberType.MODULE || type == MemberType.PARAMETER) {
            registerAccess("mandated", Opcodes.ACC_MANDATED);
        }
        registerAccess("syntetic", Opcodes.ACC_SYNTHETIC);

        HashMap<CheckBox, Integer> sorted = new HashMap<>();

        accesses.keySet().stream().sorted(Comparator.comparingInt(c -> -accesses.get(c))).forEach(checkBox -> sorted.put(checkBox, accesses.get(checkBox)));

        accesses = sorted;
    }

    private void registerAccess(String name, int acc) {
        CheckBox box = new CheckBox(name);
        box.setSelected((currentAccess & acc) != 0);
        box.setOnAction(this);
        accesses.put(box, acc);
    }

    private void updateAccess() {
        accessField.setText(Integer.toHexString(currentAccess));
    }

    @Override
    public void handle(ActionEvent event) {
        //noinspection SuspiciousMethodCalls
        int acc = accesses.get(event.getSource());
        currentAccess = (((CheckBox) event.getSource()).isSelected() ? currentAccess | acc : currentAccess & ~acc);
        updateAccess();
    }
}

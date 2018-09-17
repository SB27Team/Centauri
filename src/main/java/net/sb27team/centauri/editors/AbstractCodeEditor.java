/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.editors;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.Main;
import net.sb27team.centauri.explorer.FileComponent;
import net.sb27team.centauri.resource.ResourceManager;
import net.sb27team.centauri.utils.Mapper;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/*
 * Created by Cubxity on 18/05/2018
 */
public abstract class AbstractCodeEditor implements IEditor {
    static Theme theme;

    static {
        Theme theme = null;
        try {
            theme = Theme.load(Main.class.getResourceAsStream("/code/style.xml"));
        } catch (IOException e) {
            Centauri.INSTANCE.report(e);
        } finally {
            AbstractCodeEditor.theme = theme;
        }
    }

    @Override
    public void open(FileComponent file, InputStream stream, Tab tab) {
        Platform.setImplicitExit(false);
        FlowPane pane = new FlowPane();
        Label label = new Label("DECOMPILING...", new ImageView(ResourceManager.ANIMATED_LOADING_ICON));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(javafx.scene.text.Font.font("Roboto", FontWeight.BOLD, 20));
        pane.getChildren().add(label);
        Button stopButton = new Button("Stop decompiler");
        pane.getChildren().add(stopButton);
        tab.setContent(pane);
        Thread thread = new Thread(() -> {
            String raw;
            try {
                raw = getContext(file, Centauri.INSTANCE.getOpenedFile());
                raw = Mapper.getInstance().replace(raw);
            } catch (InterruptedException e) {
                Centauri.LOGGER.fine("Successfully interrupted " + name());
                return;
            } catch (Exception ex) {
                raw = "Count not get Decompiled context: " + ex.getMessage();
                System.err.println(raw);
                Centauri.INSTANCE.report(ex);
            }
            final String text = raw;

            Platform.runLater(() -> {
                SwingNode sn = new SwingNode();
                RSyntaxTextArea sta = new RSyntaxTextArea();
                sta.setEditable(false);
                sta.setText(text);
                sta.setSyntaxEditingStyle(getSyntax(file.getName()));
                sta.setFont(new Font("Consolas", Font.PLAIN, 14));

                if (theme != null)
                    theme.apply(sta);

                sn.setContent(new RTextScrollPane(sta));
                tab.setContent(sn);
            });
        }, "Decompiler thread");
        EventHandler<javafx.scene.input.MouseEvent> handler = event -> {
            thread.interrupt();
//            thread.suspend();
//            thread.stop();
            Label l = new Label("  DECOMPILER STOPPED. (Close and reopen the tab)", new ImageView(ResourceManager.SAD_FACE));
            l.setTextAlignment(TextAlignment.CENTER);
            l.setFont(javafx.scene.text.Font.font("Roboto", FontWeight.BOLD, 20));
            tab.setContent(l);
        };
        stopButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, handler);
        Centauri.INSTANCE.addThread(thread);
        thread.start();
    }

    abstract String getSyntax(String fileName);

    abstract String getContext(FileComponent classFile, File jar) throws Exception;


    @Override
    public int priority() {
        return PRIORITY_DECOMPILER;
    }
}

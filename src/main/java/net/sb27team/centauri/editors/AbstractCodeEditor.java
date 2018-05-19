package net.sb27team.centauri.editors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.Main;
import net.sb27team.centauri.ResourceItem;
import net.sb27team.centauri.resource.ResourceManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/*
 * Created by Cubxity on 18/05/2018
 */
public abstract class AbstractCodeEditor implements IEditor {
    private static Theme theme;

    static {
        try {
            theme = Theme.load(Main.class.getResourceAsStream("/code/style.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open(ResourceItem file, InputStream stream, Tab tab) {
        Platform.setImplicitExit(false);
        FlowPane pane = new FlowPane();
        javafx.scene.control.Label label = new Label("DECOMPILING...", new ImageView(ResourceManager.ANIMATED_LOADING_ICON));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(javafx.scene.text.Font.font("Roboto", FontWeight.BOLD, 20));
        pane.getChildren().add(label);
        Button stopButton = new Button("Stop decompiler");
        pane.getChildren().add(stopButton);
        tab.setContent(pane);
        Thread thread = new Thread(() -> {
                    String text = getContext(file, Centauri.INSTANCE.getOpenedFile());
                    Platform.runLater(() -> {
                        SwingNode sn = new SwingNode();
                        RSyntaxTextArea sta = new RSyntaxTextArea();
                        sta.setEditable(false);
                        sta.setText(text);
                        sta.setSyntaxEditingStyle(getSyntax());
                        sta.setFont(new Font("Consolas", Font.PLAIN, 14));

                        if (theme != null)
                            theme.apply(sta);

                        sn.setContent(new RTextScrollPane(sta));
                        tab.setContent(sn);
                    });
                }, "Decompiler thread");
        EventHandler<javafx.scene.input.MouseEvent> handler = event -> {
//            thread.interrupt();
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

    abstract String getSyntax();

    abstract String getContext(ResourceItem classFile, File jar);
}

package net.sb27team.centauri.editors;

import javafx.embed.swing.SwingNode;
import javafx.scene.control.Tab;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.Main;
import net.sb27team.centauri.ResourceItem;
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
        SwingNode sn = new SwingNode();
        RSyntaxTextArea sta = new RSyntaxTextArea();
        sta.setEditable(false);
        sta.setText(getContext(file, Centauri.INSTANCE.getOpenedFile()));
        sta.setSyntaxEditingStyle(getSyntax());
        sta.setFont(new Font("Consolas", Font.PLAIN, 14));
        if(theme != null)
            theme.apply(sta);
        sn.setContent(new RTextScrollPane(sta));
        tab.setContent(sn);
    }

    abstract String getSyntax();

    abstract String getContext(ResourceItem classFile, File jar);
}

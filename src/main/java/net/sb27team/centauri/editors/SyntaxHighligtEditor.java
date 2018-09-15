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

import com.google.common.io.ByteStreams;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Tab;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.explorer.FileComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class SyntaxHighligtEditor implements IEditor {
    private static HashMap<String[], String> SUPPORTED_SYNTAXES = new HashMap<>();

    static {
        register(SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT, "as", "actionscript");
        register(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86, "s", "asm");
        register(SyntaxConstants.SYNTAX_STYLE_C, "c", "h");
        register(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS, "cpp", "hpp");
        register(SyntaxConstants.SYNTAX_STYLE_CSHARP, "cs");
        register(SyntaxConstants.SYNTAX_STYLE_CSS, "css");
        register(SyntaxConstants.SYNTAX_STYLE_FORTRAN, "f", "f90");
        register(SyntaxConstants.SYNTAX_STYLE_GROOVY, "groovy");
        register(SyntaxConstants.SYNTAX_STYLE_HTACCESS, "htaccess");
        register(SyntaxConstants.SYNTAX_STYLE_HTML, "htm", "html");
        register(SyntaxConstants.SYNTAX_STYLE_INI, "ini");
        register(SyntaxConstants.SYNTAX_STYLE_JAVA, "java");
        register(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, "js");
        register(SyntaxConstants.SYNTAX_STYLE_JSON, "json");
        register(SyntaxConstants.SYNTAX_STYLE_LUA, "lua");
        register(SyntaxConstants.SYNTAX_STYLE_MAKEFILE, "Makefile");
        register(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE, "properties");
        register(SyntaxConstants.SYNTAX_STYLE_PYTHON, "py");
        register(SyntaxConstants.SYNTAX_STYLE_SCALA, "scala");
        register(SyntaxConstants.SYNTAX_STYLE_JAVA, "java");
        register(SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT, "ts");
        register(SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL, "sh");
        register(SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC, "vb");
        register(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH, "bat");
        register(SyntaxConstants.SYNTAX_STYLE_XML, "xml");
        register(SyntaxConstants.SYNTAX_STYLE_YAML, "yml");
    }

    private static void register(String syntax, String... endings) {
        SUPPORTED_SYNTAXES.put(endings, syntax);
    }

    @Override
    public void open(FileComponent file, InputStream stream, Tab tab) {
        Platform.runLater(() -> {
            SwingNode sn = new SwingNode();
            RSyntaxTextArea sta = new RSyntaxTextArea();
            sta.setEditable(false);
            try {
                sta.setText(new String(ByteStreams.toByteArray(stream), StandardCharsets.UTF_8));
            } catch (IOException e) {
                Centauri.INSTANCE.report(e);
            }
            sta.setSyntaxEditingStyle(getSyntaxForFile(file.getName()));
            sta.setFont(new Font("Consolas", Font.PLAIN, 14));

            if (AbstractCodeEditor.theme != null)
                AbstractCodeEditor.theme.apply(sta);

            sn.setContent(new RTextScrollPane(sta));
            tab.setContent(sn);
        });
    }

    @Override
    public boolean supports(String type, String name) {
        return getSyntaxForFile(name) != null;
    }

    private String getSyntaxForFile(String name) {
        AtomicReference<String> ref = new AtomicReference<>();
        SUPPORTED_SYNTAXES.entrySet().stream().filter(entry -> Arrays.stream(entry.getKey()).anyMatch(str -> name.equalsIgnoreCase(str) || name.toLowerCase().endsWith(str.toLowerCase()))).findFirst().ifPresent(entry -> ref.set(entry.getValue()));
        return ref.get();
    }

    @Override
    public String name() {
        return "SyntaxHighlightingEditor";
    }
}

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
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.controller.MainMenuController;
import net.sb27team.centauri.explorer.FileComponent;
import net.sb27team.centauri.utils.Utils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.File;
import java.nio.file.Files;

public class JASMEditor extends AbstractCodeEditor {

    @Override
    String getContext(FileComponent classFile, File jar) throws Exception {
        File temp = File.createTempFile("centauri", "_tmp.class");
        Files.write(temp.toPath(), ByteStreams.toByteArray(Centauri.INSTANCE.getInputStream(classFile)));

        Platform.runLater(() -> MainMenuController.INSTANCE.setStatus("Decompiling: " + classFile + "..."));
        Process process = Runtime.getRuntime().exec("javap -c " + (Utils.getOS() == Utils.OSType.WINDOWS ? "\"" + temp.getAbsolutePath() + "\"" : Utils.unixPathReplacer(temp.getAbsolutePath())));

        int exitCode = process.waitFor();

        if (exitCode < 0) {
            throw new IllegalStateException(String.format("Javap returned %d instead of 0", exitCode));
        }

        Platform.runLater(() -> MainMenuController.INSTANCE.setStatus("Ready"));

        return new String(ByteStreams.toByteArray(process.getInputStream()), "UTF-8");
    }

    @Override
    String getSyntax(String name) {
        return SyntaxConstants.SYNTAX_STYLE_JAVA;
    }

    @Override
    public boolean supports(String type, String name) {
        return name.endsWith(".class");
    }

    @Override
    public String name() {
        return "JavaP Disassembler";
    }
}

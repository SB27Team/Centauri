/*
 * Copyright (c) 2018 SinC (superblaubeere27, Cubixy, Xc3pt1on, Cython)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWAR
 */

package net.sb27team.centauri.editors;

import com.google.common.io.ByteStreams;
import javafx.application.Platform;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.ResourceItem;
import net.sb27team.centauri.controller.MainMenuController;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jboss.windup.decompiler.api.DecompilationResult;
import org.jboss.windup.decompiler.fernflower.FernflowerDecompiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/*
 * Created by Cubxity on 18/05/2018
 */
public class FFEditor extends AbstractCodeEditor {
    private static FernflowerDecompiler decompiler = new FernflowerDecompiler();

    @Override
    String getSyntax() {
        return SyntaxConstants.SYNTAX_STYLE_JAVA;
    }

    @Override
    String getContext(ResourceItem classFile, File jar) {
        File tmpFile;
        try {
            tmpFile = File.createTempFile("centauri", "_tmp.class");
//            File outputFile = File.createTempFile("centauri", "_output.class");
            Files.write(tmpFile.toPath(), ByteStreams.toByteArray(Centauri.INSTANCE.getInputStream(classFile)));
            // System.out.println(tmpFile.getName());
//            MainMenuController.INSTANCE.setStatus("Decompiling "+classFile+"...");
            Platform.runLater(() -> MainMenuController.INSTANCE.setStatus("Decompiling "+classFile+"..."));
            DecompilationResult result = decompiler.decompileClassFile(jar.toPath(), tmpFile.toPath(), Paths.get(System.getProperty("java.io.tmpdir")));
            Platform.runLater(() -> MainMenuController.INSTANCE.setStatus("Ready"));
//            for (DecompilationFailure decompilationFailure : result.getFailures()) {
//                System.out.println(decompilationFailure.getPath() + "/" + decompilationFailure.getMessage());
//            }
            tmpFile.deleteOnExit();

            if (result.getFailures().isEmpty()) {
                String file = new ArrayList<>(result.getDecompiledFiles().values()).get(0);
                String content = new String(Files.readAllBytes(Paths.get(file)));
                new File(file).delete();
                return content;
            } else return "Error: " + result.getFailures().get(0).getMessage();
        } catch (IOException e) {
            return "Error: " + e;
        }

    }

    @Override
    public boolean supports(String type, String name) {
        return name.endsWith(".class");
    }

    @Override
    public String name() {
        return "FernFlower Decompiler";
    }
}

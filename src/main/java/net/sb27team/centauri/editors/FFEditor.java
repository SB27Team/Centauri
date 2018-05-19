package net.sb27team.centauri.editors;

import com.google.common.io.ByteStreams;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.ResourceItem;
import net.sb27team.centauri.controller.MainMenuController;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jboss.windup.decompiler.api.DecompilationFailure;
import org.jboss.windup.decompiler.api.DecompilationResult;
import org.jboss.windup.decompiler.fernflower.FernflowerDecompiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
            MainMenuController.INSTANCE.setStatus("Decompiling "+classFile+"...");
            DecompilationResult result = decompiler.decompileClassFile(jar.toPath(), tmpFile.toPath(), Paths.get(System.getProperty("java.io.tmpdir")));
            MainMenuController.INSTANCE.setStatus("Ready");
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

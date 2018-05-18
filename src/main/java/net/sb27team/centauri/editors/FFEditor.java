package net.sb27team.centauri.editors;

import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.controller.MainMenuController;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jboss.windup.decompiler.api.DecompilationResult;
import org.jboss.windup.decompiler.fernflower.FernflowerDecompiler;

import java.io.File;
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
    String getContext(File classFile, File jar) {
        MainMenuController.INSTANCE.setStatus("Decompiling "+classFile.getName()+"...");
        DecompilationResult result = decompiler.decompileClassFile(jar.toPath(), classFile.toPath(), new File(System.getProperty("java.io.tmpdir")).toPath());
        MainMenuController.INSTANCE.setStatus("Ready");
        return result.getFailures().isEmpty() ? new ArrayList<>(result.getDecompiledFiles().values()).get(0) : "Error: " + result.getFailures().get(0).getMessage();
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

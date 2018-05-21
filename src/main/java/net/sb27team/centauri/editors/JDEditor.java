package net.sb27team.centauri.editors;

import jd.core.Decompiler;
import jd.core.DecompilerException;
import jd.ide.intellij.JavaDecompiler;
import net.sb27team.centauri.explorer.FileComponent;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.File;

public class JDEditor extends AbstractCodeEditor {

    private JavaDecompiler decompiler = new JavaDecompiler();

    @Override
    String getContext(FileComponent classFile, File jar) {
        System.out.println(classFile.getFullPath() + " " + jar.getPath() + " " + jar.exists());
        return decompiler.decompile(jar.getPath(), classFile.getFullPath());
    }

    @Override
    public boolean supports(String type, String name) {
        return name.endsWith(".class");
    }

    @Override
    public String name() {
        return "JD Decompiler";
    }

    @Override
    String getSyntax() {
        return SyntaxConstants.SYNTAX_STYLE_JAVA;
    }

}

package net.sb27team.centauri.editors;

import com.google.common.io.ByteStreams;
import javafx.application.Platform;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.controller.MainMenuController;
import net.sb27team.centauri.explorer.FileComponent;
import org.benf.cfr.reader.entities.ClassFile;
import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.relationship.MemberNameResolver;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.state.TypeUsageCollector;
import org.benf.cfr.reader.util.CannotLoadClassException;
import org.benf.cfr.reader.util.ListFactory;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.getopt.PermittedOptionProvider;
import org.benf.cfr.reader.util.output.ToStringDumper;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

public class CFREditor extends AbstractCodeEditor {

    @Override
    String getContext(FileComponent classFile, File jar) throws IOException {
        File temp = File.createTempFile("centauri", "_tmp.class");
        Files.write(temp.toPath(), ByteStreams.toByteArray(Centauri.INSTANCE.getInputStream(classFile)));

        Platform.runLater(() -> MainMenuController.INSTANCE.setStatus("Decompiling: " + classFile + "..."));
        String result = decompile(temp);
        Platform.runLater(() -> MainMenuController.INSTANCE.setStatus("Ready"));

        return result;
    }

    private String decompile(File file) {
        GetOptParser getOptParser = new GetOptParser();
        Options options = getOptParser.parse(new String[]{file.getAbsolutePath()}, OptionsImpl.getFactory());

        ClassFileSourceImpl classFileSource = new ClassFileSourceImpl(options);
        DCCommonState dcCommonState = new DCCommonState(options, classFileSource);

        ToStringDumper stringDumper = new ToStringDumper();
        try {
            ClassFile classFile = dcCommonState.getClassFileMaybePath(file.getAbsolutePath());
            dcCommonState.configureWith(classFile);
            try {
                classFile = dcCommonState.getClassFile(classFile.getClassType());
            } catch (CannotLoadClassException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
            classFile.loadInnerClasses(dcCommonState);
            if ((Boolean) options.getOption((PermittedOptionProvider.ArgumentParam) OptionsImpl.RENAME_DUP_MEMBERS)) {
                MemberNameResolver.resolveNames(dcCommonState, ListFactory.newList((Collection) dcCommonState.getClassCache().getLoadedTypes()));
            }
            classFile.analyseTop(dcCommonState);
            TypeUsageCollector collectingDumper = new TypeUsageCollector(classFile);
            classFile.collectTypeUsages(collectingDumper);

            String methname = (String) options.getOption((PermittedOptionProvider.ArgumentParam) OptionsImpl.METHODNAME);
            if (methname == null) {
                classFile.dump(stringDumper);
            } else {
                try {
                    for (Method method : classFile.getMethodByName(methname)) {
                        method.dump(stringDumper, true);
                    }
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException("No such method '" + methname + "'.");
                }
            }
            stringDumper.print("");
        } finally {
            String result=stringDumper.toString();
            stringDumper.close();
            return result;
        }
    }

    @Override
    String getSyntax() {
        return SyntaxConstants.SYNTAX_STYLE_JAVA;
    }

    @Override
    public boolean supports(String type, String name) {
        return name.endsWith(".class");
    }

    @Override
    public String name() {
        return "CFR Decompiler";
    }
}

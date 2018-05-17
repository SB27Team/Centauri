package net.sb27team.centauri.scanner.classes;

import net.sb27team.centauri.scanner.IClassScanner;
import net.sb27team.centauri.scanner.Scanner;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LPK
 */
public class CClassLoader implements IClassScanner {
    @Override
    public Scanner.Threat scan(ClassNode cn) {
        List<String> fields = new ArrayList<>();
        if (cn.superName.contains("ClassLoader"))
            return new Scanner.Threat("Extended Classloader", "This class allows loading new classes at runtime.", cn.name);
        // Scan fields for classloaders.
        for (Object o : cn.fields) {
            FieldNode fn = (FieldNode) o;
            if (fn == null)
                continue;
            if (fn.desc.contains("ClassLoader"))
                fields.add(fn.name + "-" + fn.desc);
        }
        if (fields.size() == 0)
            return null;
        StringBuilder out = new StringBuilder();
        for (String field : fields)
            out.append(field).append(", ");
        return new Scanner.Threat("Extended Classloader", "This class has fields that allow loading new classes at runtime.", cn.name + ": Fields: " + out, Scanner.Level.MEDIUM);
    }
}

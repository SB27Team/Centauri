package net.sb27team.centauri.scanner.method;

import net.sb27team.centauri.scanner.IMethodScanner;
import net.sb27team.centauri.scanner.Scanner;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LPK
 */
public class MClassLoader implements IMethodScanner {
    @Override
    public Scanner.Threat scan(MethodNode mn, ClassNode cn) {
        // Scan method for classloaders
        List<String> methods = new ArrayList<>();
        int opIndex = 0;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.contains("ClassLoader") || min.desc.contains("ClassLoader"))
                    methods.add(toLocation(opIndex, mn.name, min));
            } else if (ain.getType() == AbstractInsnNode.FIELD_INSN) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.contains("ClassLoader") || fin.desc.contains("ClassLoader"))
                    methods.add(toLocation(opIndex, mn.name, fin));
            }
            opIndex++;
        }
        if (methods.size() == 0)
            return null;
        return new Scanner.Threat("ClassLoader Call", "This class can load new classes at runtime.", cn, mn, methods.toString(), Scanner.Level.MEDIUM);
    }
}

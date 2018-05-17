package net.sb27team.centauri.scanner.method;

import net.sb27team.centauri.scanner.IMethodScanner;
import net.sb27team.centauri.scanner.Scanner;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LPK
 */
public class MFileIO implements IMethodScanner {
    @Override
    public Scanner.Threat scan(MethodNode mn, ClassNode cn) {
        List<String> methods = new ArrayList<>();
        int opIndex = 0;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.contains("File")
                        && (min.name.contains("delete") || min.name.contains("mkdir") || min.name.contains("createNew") || min.name.contains("createTemp"))) {
                    methods.add(toLocation(opIndex, mn.name, min));
                }
            }
            opIndex++;
        }
        if (methods.size() == 0)
            return null;
        return new Scanner.Threat("FileIO", "This class has methods that interact with the file system.", cn, mn, methods.toString());
    }
}

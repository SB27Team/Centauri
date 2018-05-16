package net.sb27team.centauri.scanner.method;

import net.sb27team.centauri.scanner.IMethodScanner;
import net.sb27team.centauri.scanner.Scanner;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LPK
 */
public class MRuntime implements IMethodScanner {
    @Override
    public Scanner.Threat scan(MethodNode mn) {
        List<String> methods = new ArrayList<>();
        int opIndex = 0;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) ain;
                // Sun's windows registry implementation and a third party's
                if (min.owner.equals("java/lang/Runtime"))
                    methods.add(toLocation(opIndex, mn.name, min));
            }
            opIndex++;
        }
        if (methods.size() == 0)
            return null;
        return new Scanner.Threat("Runtime call", "This method uses the Runtime class, which can be used for running external programs, gathering information about the executing machine, and modifying how the program closes (Such as prevention)", mn, methods.toString());
    }
}

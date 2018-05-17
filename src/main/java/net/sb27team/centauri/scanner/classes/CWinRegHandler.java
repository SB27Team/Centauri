package net.sb27team.centauri.scanner.classes;

import net.sb27team.centauri.scanner.IClassScanner;
import net.sb27team.centauri.scanner.Scanner;
import org.objectweb.asm.tree.*;

/**
 * @author LPK
 */
public class CWinRegHandler implements IClassScanner {
    @Override
    public Scanner.Threat scan(ClassNode cn) {
        boolean regHKLU = false, regHKLM = false, regReadAll = false, sunRegistry = false;
        // Scan fields for registry constants.
        for (Object o : cn.fields) {
            FieldNode fn = (FieldNode) o;
            if (fn == null)
                continue;
            if (fn.desc.equals("I")) {
                if (fn.value == null)
                    continue;
                if (fn.value.equals(0x80000001))
                    regHKLU = true;
                else if (fn.value.equals(0x80000002))
                    regHKLM = true;
                else if (fn.value.equals(0xf003f))
                    regReadAll = true;
            }
        }
        // Scan methods registry method calls.
        for (Object o : cn.methods) {
            MethodNode mn = (MethodNode) o;
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
                    MethodInsnNode min = (MethodInsnNode) ain;
                    // Sun's windows registry implementation
                    if (min.owner.startsWith("com/sun/jna") && (min.owner.contains("Advapi32Util") || min.owner.contains("WinReg")))
                        sunRegistry = true;
                }
            }
        }
        if (regHKLU && regHKLM && regReadAll || sunRegistry)
            return new Scanner.Threat("Windows RegEdit", "This class can modify the window's registry.", cn.name, Scanner.Level.CRITICAL);
        return null;
    }
}

package net.sb27team.centauri.scanner;

import org.objectweb.asm.tree.*;

/*
 * Created by Cubxity on 16/05/2018
 */
public interface IMethodScanner {
    Scanner.Threat scan(MethodNode mn, ClassNode cn);

    default String toLocation(int opIndex, String methodNode, MethodInsnNode min) {
        return ("Opcode:Method@" + methodNode + "@" + opIndex + " - " + min.owner + "." + min.name + "." + min.desc);
    }

    default String toLocation(int opIndex, String methodNode, FieldInsnNode fin) {
        return ("Opcode:Field@" + methodNode + "@" + opIndex + " - " + fin.owner + "." + fin.name + "." + fin.desc);
    }

    default String toLocation(int opIndex, String methodNode, LdcInsnNode ldc) {
        return ("Opcode:Field@" + methodNode + "@" + opIndex + " - \"" + ldc.cst.toString() + "\"");
    }
}

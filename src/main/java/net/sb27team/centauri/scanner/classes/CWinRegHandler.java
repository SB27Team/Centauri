/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

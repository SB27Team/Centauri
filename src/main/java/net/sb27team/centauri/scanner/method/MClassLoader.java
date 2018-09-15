/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

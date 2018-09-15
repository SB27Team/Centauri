/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

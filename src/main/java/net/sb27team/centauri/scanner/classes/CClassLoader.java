/*
 * Copyright (c) 2018 SinC (superblaubeere27, Cubixy, Xc3pt1on, Cython)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWAR
 */

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

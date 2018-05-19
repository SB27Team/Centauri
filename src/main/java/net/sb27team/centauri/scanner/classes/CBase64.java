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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author LPK
 */
public class CBase64 implements IClassScanner {
    @Override
    public Scanner.Threat scan(ClassNode cn) {
        Pattern regex = Pattern.compile("^(?:[A-Za-z0-9+\\/]{4})*(?:[A-Za-z0-9+\\/]{2}==|[A-Za-z0-9+\\/]{3}=)?$");
        List<String> decrypts = new ArrayList<>();
        for (Object o : cn.methods) {
            MethodNode mn = (MethodNode) o;
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getType() != AbstractInsnNode.LDC_INSN)
                    continue;
                LdcInsnNode ldc = (LdcInsnNode) ain;
                if (ldc.cst instanceof String) {
                    String encoded = ldc.cst.toString();
                    // TODO: Improve match regex
                    // It doesn't detect if it's valid. Just if it is the right length/chars
                    byte[] bytes = encoded.getBytes();
                    if (encoded.contains("==") && bytes.length >= 4 && regex.matcher(encoded).matches())
                        try {
                            String decoded = new String(Base64.getDecoder().decode(bytes));
                            decrypts.add(decoded);
                        } catch (Exception ignored) {
                        }
                }
            }
        }
        if (decrypts.size() > 0)
            return new Scanner.Threat("Base64 Encryption", "The class hase Base64 encoded strings.", decrypts.toString());
        return null;
    }
}

/*
 * Copyright (c) 2018 SinC (superblaubeere27, Cubixy, Xc3pt1on, Cython)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWAR
 */

package net.sb27team.centauri.scanner.method;

import net.sb27team.centauri.scanner.IMethodScanner;
import net.sb27team.centauri.scanner.Scanner;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LPK
 */
public class MNetworkRef implements IMethodScanner {
    @Override
    public Scanner.Threat scan(MethodNode mn, ClassNode cn) {
        List<String> methods = new ArrayList<>();
        int opIndex = 0;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.contains("java/net/") || min.owner.contains("java/nio/channels/") || min.owner.contains("javax/net/") || min.owner.contains("sun/net/"))
                    methods.add(toLocation(opIndex, mn.name, min));
            } else if (ain.getType() == AbstractInsnNode.LDC_INSN) {
                LdcInsnNode ldc = (LdcInsnNode) ain;
                if (isIP(ldc.cst.toString()) || isLink(ldc.cst.toString()))
                    methods.add(toLocation(opIndex, mn.name, ldc));
            }
            opIndex++;
        }
        if (methods.size() == 0)
            return null;
        return new Scanner.Threat("Networking", "This class has online interactions.", cn, mn, methods.toString());
    }

    private boolean isIP(String input) {
        String regex = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        String ss = input.replace(".", "");
        return m.find() && isNumeric(ss) && (input.length() - ss.length() > 2);
    }

    private boolean isLink(String input) {
        String regex = "[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&\\/=]*)";
        if (input.contains("/") && input.contains(".") && input.matches(regex))
            return true;
        // TODO: This is old, swap this section out for regex. The above does
        // not catch every URL.
        String[] lookFor = new String[] { "http://", "https://", "www.", ".", "ftp:", ".net", ".gov", ".com", ".org", ".php", ".tk", "www", ".io", ".xyz", ".cf",
                "upload" };
        int i = 0;
        for (String lf : lookFor)
            if (input.toLowerCase().contains(lf.toLowerCase())) {
                i++;
                if (i > 2)
                    return true;
            }
        return false;
    }

    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

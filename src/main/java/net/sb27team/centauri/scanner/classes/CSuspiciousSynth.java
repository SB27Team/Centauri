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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;

/**
 * @author LPK
 */
public class CSuspiciousSynth implements IClassScanner {
    private final boolean ignoreDollas = true;
    private final double threshold = 0.55;

    @Override
    public Scanner.Threat scan(ClassNode cn) {
        int synthFields = 0, synthMethods = 0, totalFields = 0, totalMethods = 0;
        // Scan for synthetic fields
        for (Object o : cn.fields) {
            FieldNode fn = (FieldNode) o;
            if (fn == null)
                continue;
            totalFields++;
            if ((fn.access & ACC_SYNTHETIC) != 0  && (!ignoreDollas || !fn.name.contains("$")))
                synthFields++;
        }
        // Scan methods for unnatural synthetic tag occurrences.
        for (Object o : cn.methods) {
            MethodNode mn = (MethodNode) o;
            totalMethods++;
            if ((mn.access & ACC_SYNTHETIC) != 0 && (!ignoreDollas || !mn.name.contains("$"))) {
                // Discount <init> and <clinit> from calculations. Shouldn't be synthetic anyways...
                if (mn.name.contains("<"))
                    continue;
                synthMethods++;
            }
        }
        double synthFieldPercent = totalFields == 0 ? 0 : synthFields / (double) totalFields;
        double synthMethodPercent = totalMethods == 0 ? 0 : synthMethods / (double) totalMethods;
        if ((synthFieldPercent + synthMethodPercent) / 2 > threshold)
            return new Scanner.Threat("Unnatural Synthetics", "The class seems to be modified forcing an unnatural amount of members to be synthetic. Known tactic for anti-reverse engineering.", cn.name, Scanner.Level.MEDIUM);
        return null;
    }
}

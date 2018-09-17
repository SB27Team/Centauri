/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.utils;

import org.objectweb.asm.Opcodes;

import java.util.regex.Pattern;

public class NameUtils {
    private static final Pattern CLASS_NAMES = Pattern.compile("([a-zA-Z0-9_]+[/])*[a-zA-Z0-9_]+");

    public static boolean checkClassName(String name) {
        return CLASS_NAMES.matcher(name).matches();
    }

    public static String versionToString(int i) {
        switch (i) {
            case Opcodes.V1_1:
                return "1.1";
            case Opcodes.V1_2:
                return "1.2";
            case Opcodes.V1_3:
                return "1.3";
            case Opcodes.V1_4:
                return "1.4";
            case Opcodes.V1_5:
                return "1.5";
            case Opcodes.V1_6:
                return "1.6";
            case Opcodes.V1_7:
                return "1.7";
            case Opcodes.V1_8:
                return "1.8";
            case Opcodes.V9:
                return "9";
            case Opcodes.V10:
                return "10";
            case Opcodes.V11:
                return "11";
            case Opcodes.V12:
                return "12";
            default:
                return "Not a valid version";
        }
    }
}

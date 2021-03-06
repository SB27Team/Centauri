/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.actions.impl;

import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.actions.Action;
import net.sb27team.centauri.actions.DataFactory;
import net.sb27team.centauri.actions.DataKeys;
import net.sb27team.centauri.utils.Utils;

import java.io.File;

public class OpenAction extends Action {
    @Override
    public void call(DataFactory factory) {
        try {
            File file = factory.getData(DataKeys.OPEN_FILE);
            if (file == null) file = Utils.openFileDialog(null);

            Centauri.INSTANCE.openFile(file);
        } catch (Exception e1) {
            Centauri.INSTANCE.report(e1);
        }
    }

    @Override
    public String getDisplayName() {
        return "Open...";
    }

}

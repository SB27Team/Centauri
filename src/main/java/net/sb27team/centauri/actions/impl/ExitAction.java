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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.actions.Action;
import net.sb27team.centauri.actions.DataFactory;

import java.util.Optional;

public class ExitAction extends Action {

    @Override
    public void call(DataFactory factory) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setContentText("Are you sure that you want to Exit Centauri?");

        ButtonType exit = new ButtonType("Exit");

        alert.getButtonTypes().setAll(exit, ButtonType.CANCEL);


        Centauri.applyStyle(alert);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == exit) {
            System.exit(0);
        }
    }

    @Override
    public String getDisplayName() {
        return "Exit";
    }
}

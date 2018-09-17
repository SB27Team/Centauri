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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import net.sb27team.centauri.Centauri;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Mapper {

    private static Mapper instance = new Mapper();

    private Mapper() {}

    private HashMap<String, String> map = new HashMap<>();

    public void clear() {
        map.clear();
    }

    private static final String[] SEPERATORS = {", ", ",", ":"};

    public void addFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Centauri.INSTANCE.getEncoding()))) {
            String line;
            int skip = 0;
            while ((line = reader.readLine()) != null) {
                String[] split = new String[0];
                for (String seperator : SEPERATORS) {
                    if (split.length == 2) break;
                    split = line.split(seperator);
                }
                if(split.length == 2) map.put(split[0], split[1]);
                else skip++;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Mapped " + map.size() + " values!\nSkipped: " + skip + " lines!", ButtonType.OK);
            alert.setTitle("Mapping Complete");
            alert.setHeaderText("Mapping Complete");
            alert.show();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Failed reading File!", ButtonType.CLOSE).show();
        }
    }

    public String replace(String text) {
        for (Map.Entry<String, String> entry : map.entrySet())
            text = text.replaceAll(entry.getKey(), entry.getValue());
        return text;
    }


    public static Mapper getInstance() {
        return instance;
    }
}

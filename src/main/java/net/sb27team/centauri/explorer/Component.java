/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.sb27team.centauri.explorer;

import com.google.common.collect.Lists;
import net.sb27team.centauri.controller.MainMenuController;

import java.util.ArrayList;
import java.util.List;

public class Component {

    private String name;
    private Directory parent;

    public Component(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public List<Directory> getParents() {
        List<Directory> parents = new ArrayList<>();
        Directory currentParent = parent;
        while (currentParent.getParent() != null) {
            parents.add(currentParent);
            currentParent = currentParent.getParent();
        }
        return parents;
    }

    public String getFullPath() {
        StringBuilder builder = new StringBuilder();
        Lists.reverse(getParents()).forEach(directory -> builder.append(directory.getName()).append("/"));
        return builder.append(name).toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }
}

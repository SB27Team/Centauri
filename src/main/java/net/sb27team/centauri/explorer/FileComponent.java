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

import net.sb27team.centauri.Centauri;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public class FileComponent extends Component {

    private ZipEntry zipEntry;

    public FileComponent(String name, Directory parent, ZipEntry zipEntry) {
        super(name, parent);
        this.zipEntry = zipEntry;
    }

    public ZipEntry getZipEntry() {
        return zipEntry;
    }

    public InputStream openInputStream() throws IOException {
        return Centauri.INSTANCE.getInputStream(this);
    }
    public void update(byte[] newData) {
        Centauri.INSTANCE.updateData(this, newData);
    }

    public void setZipEntry(ZipEntry zipEntry) {
        this.zipEntry = zipEntry;
    }

    public ClassNode getClassNode() throws IOException {
        return Centauri.INSTANCE.getClassNode(this);
    }

    public void updateClassNode(ClassNode classNode) {
        update(Centauri.INSTANCE.classNodeToBytes(classNode));
    }
}

package net.sb27team.centauri.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
 * Created by Cubxity on 16/05/2018
 */
public class ASMUtils {
    public static List<ClassNode> loadClasses(File jar) throws IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(jar)));
        ZipEntry entry;
        List<ClassNode> cns = new ArrayList<>();
        while((entry = zis.getNextEntry()) != null){
            if(entry.isDirectory())
                continue;
            byte[] data = new byte[4096];
            ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
            int len;
            do {
                len = zis.read(data);
                if (len > 0)
                    entryBuffer.write(data, 0, len);
            } while (len != -1);
            byte[] entryData = entryBuffer.toByteArray();
            if(entry.getName().endsWith(".class")){
                ClassReader cr = new ClassReader(entryData);
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);
                cns.add(cn);
            }
        }
        return cns;
    }
}

package net.sb27team.centauri.scanner;

import org.objectweb.asm.tree.ClassNode;

/*
 * Created by Cubxity on 16/05/2018
 */
public interface IClassScanner {
    Scanner.Threat scan(ClassNode cn);
}

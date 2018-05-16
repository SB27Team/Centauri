package net.sb27team.centauri.scanner;

import org.objectweb.asm.tree.MethodNode;

/*
 * Created by Cubxity on 16/05/2018
 */
public interface IMethodScanner {
    Scanner.Threat scan(MethodNode mn);
}

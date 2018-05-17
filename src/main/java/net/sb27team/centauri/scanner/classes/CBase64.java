package net.sb27team.centauri.scanner.classes;

import net.sb27team.centauri.scanner.IClassScanner;
import net.sb27team.centauri.scanner.Scanner;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author LPK
 */
public class CBase64 implements IClassScanner {
    @Override
    public Scanner.Threat scan(ClassNode cn) {
        Pattern regex = Pattern.compile("^(?:[A-Za-z0-9+\\/]{4})*(?:[A-Za-z0-9+\\/]{2}==|[A-Za-z0-9+\\/]{3}=)?$");
        List<String> decrypts = new ArrayList<>();
        for (Object o : cn.methods) {
            MethodNode mn = (MethodNode) o;
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getType() != AbstractInsnNode.LDC_INSN)
                    continue;
                LdcInsnNode ldc = (LdcInsnNode) ain;
                if (ldc.cst instanceof String) {
                    String encoded = ldc.cst.toString();
                    // TODO: Improve match regex
                    // It doesn't detect if it's valid. Just if it is the right length/chars
                    byte[] bytes = encoded.getBytes();
                    if (encoded.contains("==") && bytes.length >= 4 && regex.matcher(encoded).matches())
                        try {
                            String decoded = new String(Base64.getDecoder().decode(bytes));
                            decrypts.add(decoded);
                        } catch (Exception ignored) {
                        }
                }
            }
        }
        if (decrypts.size() > 0)
            return new Scanner.Threat("Base64 Encryption", "The class hase Base64 encoded strings.", decrypts.toString());
        return null;
    }
}

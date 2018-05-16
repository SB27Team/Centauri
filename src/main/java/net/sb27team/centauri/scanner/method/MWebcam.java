package net.sb27team.centauri.scanner.method;

import net.sb27team.centauri.scanner.IMethodScanner;
import net.sb27team.centauri.scanner.Scanner;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LPK
 */
public class MWebcam implements IMethodScanner {

    @Override
    public Scanner.Threat scan(MethodNode mn) {
        List<String> methods = new ArrayList<>();
        int opIndex = 0;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) ain;
                //OpenCV and Sarxos
                if (min.owner.contains("OpenCVFrameRecorder") || min.owner.contains("OpenCVFrameGrabber") || min.owner.contains("Webcam"))
                    methods.add(toLocation(opIndex, mn.name, min));
            } else if (ain.getType() == AbstractInsnNode.LDC_INSN) {
                LdcInsnNode ldc = (LdcInsnNode) ain;
                // Sarxos
                if (ldc.cst.toString().contains("Webcam device") || ldc.cst.toString().contains("Notify webcam"))
                    methods.add(toLocation(opIndex, mn.name, ldc));
            }
            opIndex++;
        }
        if (methods.size() == 0) {
            return null;
        }
        return new Scanner.Threat("OpenCV/Sarxos Webcam Call", "This class has methods that can access the webcam.", methods.toString());
    }
}

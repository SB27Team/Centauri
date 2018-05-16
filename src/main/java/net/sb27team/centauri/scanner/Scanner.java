package net.sb27team.centauri.scanner;

import net.sb27team.centauri.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This will scan classes in the jar ex. Networking, Potential rat, etc...
 */
public class Scanner {
    private File jar;
    private List<IClassScanner> classScanners = Arrays.asList(

    );

    private List<IMethodScanner> methodScanners = Arrays.asList(

    );

    public Scanner(File jar) {
        this.jar = jar;
    }

    public List<Threat> runScan() {
        List<Threat> threats = new ArrayList<>();
        try {
            for (ClassNode cn : ASMUtils.loadClasses(jar)) {
                threats.addAll(classScanners.stream().map(cs -> cs.scan(cn)).filter(Objects::nonNull).collect(Collectors.toList()));
                for (Object o : cn.methods) {
                    MethodNode mn = (MethodNode) o;
                    threats.addAll(methodScanners.stream().map(ms -> ms.scan(mn)).filter(Objects::nonNull).collect(Collectors.toList()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return threats;
    }

    public class Threat {
        private final String name, description, location;

        public Threat(String name, String description, String location) {
            this.name = name;
            this.description = description;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getLocation() {
            return location;
        }
    }
}

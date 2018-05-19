package net.sb27team.centauri.scanner;

import net.sb27team.centauri.Centauri;
import net.sb27team.centauri.scanner.classes.CBase64;
import net.sb27team.centauri.scanner.classes.CClassLoader;
import net.sb27team.centauri.scanner.classes.CSuspiciousSynth;
import net.sb27team.centauri.scanner.classes.CWinRegHandler;
import net.sb27team.centauri.scanner.method.*;
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
            new CSuspiciousSynth(),
            new CWinRegHandler(),
            new CClassLoader(),
            new CBase64()
    );

    private List<IMethodScanner> methodScanners = Arrays.asList(
            new MWebcam(),
            new MRuntime(),
            new MNetworkRef(),
            new MNativeInterface(),
            new MFileIO(),
            new MClassLoader()
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
                    threats.addAll(methodScanners.stream().map(ms -> ms.scan(mn, cn)).filter(Objects::nonNull).collect(Collectors.toList()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return threats;
    }

    public static void main(String[] args) {
        // For testing
        Scanner s = new Scanner(new File(args[0]));
        s.runScan().forEach(t -> Centauri.LOGGER.finer("Threat: " + t.name + ": " + t.description + " loc: " + t.location + " LEVEL: " + t.level.name()));
    }

    public static class Threat {
        private final String name, description, location;
        private final Level level;

        public Threat(String name, String description, String location) {
            this(name, description, location, Level.LOW);
        }

        public Threat(String name, String description, String location, Level level) {
            this.name = name;
            this.description = description;
            this.location = location;
            this.level = level;
        }

        public Threat(String name, String description, ClassNode owner, MethodNode mn, String location) {
            this(name, description, owner, mn, location, Level.LOW);
        }

        public Threat(String name, String description, ClassNode owner, MethodNode mn, String location, Level level) {
            this(name, description, owner.name + "." + mn.name + mn.desc + ": " + location, level);
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

        public Level getLevel() {
            return level;
        }
    }

    public enum Level { // risk level
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}

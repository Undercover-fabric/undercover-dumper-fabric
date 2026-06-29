import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

// -javaagent:ClassDumpAgent.jar=<outDir>|mode:mod|include:com/foo
public class ClassDumpAgent {
    private static File outDir;
    private static DumpMode mode = DumpMode.MOD;
    private static final Set<String> dumped = ConcurrentHashMap.newKeySet();
    private static final Set<String> includePrefixes = new HashSet<>();
    private static final Set<String> excludePrefixes = new HashSet<>(Arrays.asList(
            "java/", "javax/", "jdk/", "sun/", "com/sun/",
            "net/minecraft/", "net/fabricmc/", "net/java/",
            "com/mojang/", "com/google/", "com/fasterxml/", "com/jcraft/", "com/github/",
            "org/lwjgl/", "org/spongepowered/", "org/apache/", "org/json/", "org/joml/",
            "org/ow2/", "org/objectweb/", "org/jetbrains/", "org/slf4j/", "org/yaml/",
            "org/antlr/", "org/fusesource/", "io/netty/", "it/unimi/", "kotlin/",
            "joptsimple/", "oshi/", "meteordevelopment/", "ca/weblite/", "blue/endless/"
    ));
    private static final Pattern HASH_SEGMENT = Pattern.compile("/[a-f0-9]{8,}/");

    private enum DumpMode {
        MOD, OBFUSCATED, INCLUDE, ALL
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        init(agentArgs);
        install(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        init(agentArgs);
        install(inst);
    }

    private static void init(String agentArgs) {
        String output = System.getProperty("java.io.tmpdir") + File.separator + "class_dump";
        if (agentArgs != null && !agentArgs.trim().isEmpty()) {
            String[] parts = agentArgs.split("\\|");
            output = parts[0].trim();
            for (int i = 1; i < parts.length; i++) {
                parseOption(parts[i].trim());
            }
        }
        outDir = new File(output);
        outDir.mkdirs();
        System.out.println("[ClassDumpAgent] output=" + outDir.getAbsolutePath());
        System.out.println("[ClassDumpAgent] mode=" + mode.name().toLowerCase(Locale.ROOT));
        if (!includePrefixes.isEmpty()) {
            System.out.println("[ClassDumpAgent] include=" + includePrefixes);
        }
    }

    private static void parseOption(String part) {
        if (part.startsWith("include:")) {
            for (String prefix : part.substring(8).split(",")) {
                String normalized = normalizePrefix(prefix.trim());
                if (!normalized.isEmpty()) {
                    includePrefixes.add(normalized);
                }
            }
            return;
        }
        if (part.startsWith("exclude:")) {
            for (String prefix : part.substring(8).split(",")) {
                String normalized = normalizePrefix(prefix.trim());
                if (!normalized.isEmpty()) {
                    excludePrefixes.add(normalized);
                }
            }
            return;
        }
        if (part.startsWith("mode:")) {
            String value = part.substring(5).trim().toLowerCase(Locale.ROOT);
            switch (value) {
                case "all":
                    mode = DumpMode.ALL;
                    break;
                case "include":
                    mode = DumpMode.INCLUDE;
                    break;
                case "obfuscated":
                    mode = DumpMode.OBFUSCATED;
                    break;
                case "mod":
                default:
                    mode = DumpMode.MOD;
                    break;
            }
        }
    }

    private static String normalizePrefix(String prefix) {
        return prefix.replace('.', '/');
    }

    private static void install(Instrumentation inst) {
        ClassFileTransformer transformer = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (className == null || classfileBuffer == null || classfileBuffer.length < 4) {
                    return null;
                }
                if (!shouldDump(className)) {
                    return null;
                }
                writeClass(className, classfileBuffer);
                return null;
            }
        };
        inst.addTransformer(transformer, true);
    }

    private static boolean shouldDump(String className) {
        for (String prefix : excludePrefixes) {
            if (className.startsWith(prefix)) {
                return false;
            }
        }

        boolean includeMatch = matchesInclude(className);
        boolean obfuscated = looksObfuscated(className);

        switch (mode) {
            case ALL:
                return true;
            case INCLUDE:
                return includeMatch;
            case OBFUSCATED:
                return obfuscated;
            case MOD:
            default:
                return includeMatch || obfuscated;
        }
    }

    private static boolean matchesInclude(String className) {
        for (String prefix : includePrefixes) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static boolean looksObfuscated(String className) {
        if (HASH_SEGMENT.matcher(className).find()) {
            return true;
        }
        String simple = className.contains("/")
                ? className.substring(className.lastIndexOf('/') + 1)
                : className;
        return simple.length() <= 6 && simple.matches("[a-zA-Z][a-zA-Z0-9_$]*");
    }

    private static void writeClass(String className, byte[] bytes) {
        if (!dumped.add(className)) {
            return;
        }
        File path = new File(outDir, className + ".class");
        path.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes);
            System.out.println("[ClassDumpAgent] " + className + " (" + bytes.length + " bytes)");
        } catch (Exception e) {
            System.err.println("[ClassDumpAgent] failed " + className + ": " + e.getMessage());
        }
    }
}
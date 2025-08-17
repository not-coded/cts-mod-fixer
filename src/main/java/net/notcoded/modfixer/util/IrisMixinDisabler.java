package net.notcoded.modfixer.util;

import net.fabricmc.loader.api.FabricLoader;
import net.notcoded.modfixer.ModFixer;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.io.*;
import java.nio.file.*;
import javax.swing.JOptionPane;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class IrisMixinDisabler {
    private static final Set<String> MIXINS_TO_REMOVE;

    static {
        MIXINS_TO_REMOVE = new HashSet<>();
        MIXINS_TO_REMOVE.add("MixinMinecraft_NoAuthInDev");
    }

    private static final Set<String> VERTEX_FORMAT_MIXINS_TO_REMOVE;

    static {
        VERTEX_FORMAT_MIXINS_TO_REMOVE = new HashSet<>();
        VERTEX_FORMAT_MIXINS_TO_REMOVE.add("block_rendering.MixinChunkRebuildTask");
    }

    public static void iris() {
        Path modsDir = FabricLoader.getInstance().getGameDir().resolve("mods");
        if (!Files.exists(modsDir)) {
            ModFixer.LOGGER.error("Mods directory not found: {}", modsDir);
            return;
        }
        
        try {
            Files.walkFileTree(modsDir, new SimpleFileVisitor<Path>() {
                @Override
                public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().toLowerCase().contains("iris") && 
                        file.getFileName().toString().endsWith(".jar") &&
                        !file.getFileName().toString().endsWith("-modified.jar")) {
                        processIrisJar(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            ModFixer.LOGGER.error("Error processing mods directory", e);
        }
    }
    
    private static void processIrisJar(Path jarPath) throws IOException {
        ModFixer.LOGGER.info("Processing Iris JAR: {}", jarPath);
        
        // Create a temporary directory for extraction
        Path tempDir = Files.createTempDirectory("iris-mod-");
        Path outputJar = Paths.get(jarPath.toString().replace(".jar", "-modified.jar"));
        
        try {
            // Extract JAR
            extractJar(jarPath, tempDir);
            
            // Process mixin files
            boolean modified = false;
            modified |= processMixinFile(tempDir, "mixins.iris.json", MIXINS_TO_REMOVE);
            modified |= processMixinFile(tempDir, "mixins.iris.vertexformat.json", VERTEX_FORMAT_MIXINS_TO_REMOVE);
            
            if (modified) {
                // Create mixin changes file
                createMixinChangesFile(tempDir);
                
                // Create new JAR
                createJar(tempDir, outputJar);
                
                // Delete original file
                Files.deleteIfExists(jarPath);
                ModFixer.LOGGER.info("Created modified JAR and removed original: {}", outputJar);
                showRestartNotification();
            } else {
                ModFixer.LOGGER.info("No mixins were removed, skipping JAR modification");
            }
        } finally {
            // Clean up
            deleteDirectory(tempDir);
        }
    }
    
    private static boolean processMixinFile(Path baseDir, String fileName, Set<String> mixinsToRemove) throws IOException {
        Path mixinFile = baseDir.resolve(fileName);
        if (!Files.exists(mixinFile)) {
            ModFixer.LOGGER.warn("Mixin file not found: {}", fileName);
            return false;
        }
        
        String content = new String(Files.readAllBytes(mixinFile));
        String originalContent = content;
        
        for (String mixin : mixinsToRemove) {
            content = content.replaceAll("\\s*\"" + mixin + "\"\\s*,\\s*", "");
            content = content.replaceAll("\\s*\"" + mixin + "\"\\s*", "");
        }
        
        if (!content.equals(originalContent)) {
            Files.write(mixinFile, content.getBytes());
            ModFixer.LOGGER.info("Removed mixins from {}: {}", fileName, String.join(", ", mixinsToRemove));
            return true;
        }
        
        return false;
    }
    
    private static void createMixinChangesFile(Path baseDir) throws IOException {
        StringBuilder changes = new StringBuilder("Removed mixins:\n");
        
        // Add regular mixins with their source file
        for (String mixin : MIXINS_TO_REMOVE) {
            changes.append("- ").append(mixin).append(" (mixins.iris.json)\n");
        }
        
        // Add vertex format mixins with their source file
        for (String mixin : VERTEX_FORMAT_MIXINS_TO_REMOVE) {
            changes.append("- ").append(mixin).append(" (mixins.iris.vertexformat.json)\n");
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(baseDir.resolve("mixinChanges.txt"))) {
            writer.write(changes.toString().trim());
        }
    }
    
    private static void extractJar(Path jarPath, Path targetDir) throws IOException {
        try (ZipFile zipFile = new ZipFile(jarPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = targetDir.resolve(entry.getName());
                
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = Files.newOutputStream(entryPath.toFile().toPath())) {
                        IOUtils.copy(in, out);
                    }
                }
            }
        }
    }
    
    private static void createJar(Path sourceDir, Path targetJar) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetJar.toFile().toPath()))) {
            Files.walk(sourceDir)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    String zipPath = sourceDir.relativize(path).toString().replace("\\", "/");
                    try {
                        zos.putNextEntry(new ZipEntry(zipPath));
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        ModFixer.LOGGER.error("Error adding file to JAR: {}", zipPath, e);
                    }
                });
        }
    }
    
    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted((a, b) -> b.compareTo(a)) // reverse; files before dirs
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        ModFixer.LOGGER.error("Error deleting file: {}", path, e);
                    }
                });
        }
    }
    
    private static void showRestartNotification() {
        ModFixer.LOGGER.warn("Iris has been modified. Ignore the crash and restart Minecraft.");

        try {
            JOptionPane.showMessageDialog(
                    null,
                    "Iris has been modified\nIgnore the crash and restart Minecraft.",
                    "Restart Required",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (HeadlessException ignored) { }
    }
}

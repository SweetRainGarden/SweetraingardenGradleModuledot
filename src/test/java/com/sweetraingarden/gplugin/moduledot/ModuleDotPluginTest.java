package com.sweetraingarden.gplugin.moduledot;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.internal.tasks.TaskExecuter;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModuleDotPluginTest {
    @TempDir
    Path tempDir;
    private Project rootProject;
    private Project childProject1;
    private Project childProject2;
    private ModuleDotPlugin plugin;

    @BeforeEach
    void setUp() {
        rootProject = ProjectBuilder.builder()
                .withName("root")
                .withProjectDir(tempDir.toFile())
                .build();
        
        childProject1 = ProjectBuilder.builder()
                .withName("child1")
                .withParent(rootProject)
                .build();
        
        childProject2 = ProjectBuilder.builder()
                .withName("child2")
                .withParent(rootProject)
                .build();

        plugin = new ModuleDotPlugin();
    }

    @Test
    void testGenerateDotGraph() throws IOException {
        // Apply plugin
        plugin.apply(rootProject);

        // Generate graph directly
        plugin.generateDotGraph(rootProject);

        // Verify output file exists
        File outputFile = rootProject.getLayout().getBuildDirectory().file("module-dependencies.dot").get().getAsFile();
        assertTrue(outputFile.exists(), "DOT file should be created");

        // Read and verify content
        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertTrue(lines.stream().anyMatch(line -> line.contains("root")), "Should contain root project");
        assertTrue(lines.stream().anyMatch(line -> line.contains("child1")), "Should contain child1 project");
        assertTrue(lines.stream().anyMatch(line -> line.contains("child2")), "Should contain child2 project");
    }

    @Test
    void testTaskRegistration() {
        // Apply plugin
        plugin.apply(rootProject);

        // Verify task is registered
        Task task = rootProject.getTasks().findByName("generateModuleDotGraph");
        assertNotNull(task, "Task should be registered");
        assertEquals("documentation", task.getGroup(), "Task should be in documentation group");
        assertEquals("Generates a DOT graph of module dependencies", task.getDescription(), "Task description should match");
    }
} 
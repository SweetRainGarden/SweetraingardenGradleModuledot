package com.sweetraingarden.gplugin.moduledot

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.*

class ModuleDotPluginTest {
    @TempDir
    lateinit var tempDir: Path
    
    private lateinit var rootProject: Project
    private lateinit var childProject1: Project
    private lateinit var childProject2: Project
    private lateinit var plugin: ModuleDotPlugin

    @BeforeEach
    fun setUp() {
        rootProject = ProjectBuilder.builder()
            .withName("root")
            .withProjectDir(tempDir.toFile())
            .build()
        
        childProject1 = ProjectBuilder.builder()
            .withName("child1")
            .withParent(rootProject)
            .build()
        
        childProject2 = ProjectBuilder.builder()
            .withName("child2")
            .withParent(rootProject)
            .build()

        plugin = ModuleDotPlugin()
    }

    @Test
    fun testGenerateDotGraph() {
        // Apply plugin
        plugin.apply(rootProject)

        // Generate graph directly
        plugin.generateDotGraph(rootProject)

        // Verify output file exists
        val outputFile = rootProject.layout.buildDirectory.file("module-dependencies.dot").get().asFile
        assertTrue(outputFile.exists(), "DOT file should be created")

        // Read and verify content
        val lines = Files.readAllLines(outputFile.toPath())
        assertTrue(lines.any { it.contains("root") }, "Should contain root project")
        assertTrue(lines.any { it.contains("child1") }, "Should contain child1 project")
        assertTrue(lines.any { it.contains("child2") }, "Should contain child2 project")
    }

    @Test
    fun testTaskRegistration() {
        // Apply plugin
        plugin.apply(rootProject)

        // Verify task is registered
        val task = rootProject.tasks.findByName("generateModuleDotGraph")
        assertNotNull(task, "Task should be registered")
        assertEquals("documentation", task?.group, "Task should be in documentation group")
        assertEquals("Generates a DOT graph of module dependencies", task?.description, "Task description should match")
    }
} 
package com.sweetraingarden.gplugin.moduledot

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.dot.DOTExporter
import java.io.File
import java.io.FileWriter

class ModuleDotPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("generateModuleDotGraph") { task ->
            task.group = "documentation"
            task.description = "Generates a DOT graph of module dependencies"
            
            task.doLast {
                try {
                    generateDotGraph(project)
                } catch (e: Exception) {
                    throw RuntimeException("Failed to generate module dependency graph", e)
                }
            }
        }
    }

    private fun generateDotGraph(project: Project) {
        val graph: Graph<String, DefaultEdge> = DefaultDirectedGraph(DefaultEdge::class.java)
        
        // Add all projects to the graph
        project.allprojects.forEach { p -> graph.addVertex(p.name) }
        
        // Add dependencies between projects
        project.allprojects.forEach { p ->
            p.configurations.forEach { config ->
                config.dependencies.forEach { dep ->
                    if (dep is org.gradle.api.artifacts.ProjectDependency) {
                        graph.addEdge(p.name, dep.dependencyProject.name)
                    }
                }
            }
        }

        // Create DOT exporter
        val exporter = DOTExporter<String, DefaultEdge>()
        exporter.setVertexAttributeProvider { v ->
            mapOf(
                "label" to DefaultAttribute.createAttribute(v),
                "shape" to DefaultAttribute.createAttribute("box")
            )
        }

        // Export to file
        val outputFile = project.layout.buildDirectory.file("module-dependencies.dot").get().asFile
        outputFile.parentFile.mkdirs()
        FileWriter(outputFile).use { writer ->
            exporter.exportGraph(graph, writer)
        }
        
        project.logger.lifecycle("Module dependency graph generated at: ${outputFile.absolutePath}")
    }
} 
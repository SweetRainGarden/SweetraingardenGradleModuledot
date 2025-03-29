package com.sweetraingarden.gplugin.moduledot

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.dot.DOTExporter
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

    internal fun generateDotGraph(project: Project) {
        val graph: Graph<String, DefaultEdge> = DefaultDirectedGraph(DefaultEdge::class.java)
        
        // Build graph using project paths as unique identifiers
        buildProjectGraph(project, graph)
        
        // Create DOT exporter with custom vertex labeling
        val exporter = DOTExporter<String, DefaultEdge>()
        exporter.setVertexAttributeProvider { vertex ->
            mapOf(
                "label" to DefaultAttribute.createAttribute(getProjectNameFromPath(vertex)),
                "shape" to DefaultAttribute.createAttribute("box"),
                "tooltip" to DefaultAttribute.createAttribute(vertex)
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
    
    private fun buildProjectGraph(rootProject: Project, graph: Graph<String, DefaultEdge>) {
        // First, create a map of project paths to their configurations
        val projectMap = mutableMapOf<String, Project>()
        
        // Collect all projects and their paths
        rootProject.allprojects.forEach { project ->
            projectMap[project.path] = project
            graph.addVertex(project.path)
        }
        
        // Process dependencies using the map
        projectMap.values.forEach { project ->
            project.configurations.forEach { config ->
                config.dependencies
                    .filterIsInstance<ProjectDependency>()
                    .map { it.path }  // Use path instead of dependencyProject
                    .distinct()
                    .forEach { dependencyPath ->
                        if (projectMap.containsKey(dependencyPath)) {
                            graph.addEdge(project.path, dependencyPath)
                        }
                    }
            }
        }
    }
    
    private fun getProjectNameFromPath(path: String): String {
        return path.substringAfterLast(':').ifEmpty { "root" }
    }
} 
package com.sweetraingarden.gplugin.moduledot;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModuleDotPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().register("generateModuleDotGraph", task -> {
            task.setGroup("documentation");
            task.setDescription("Generates a DOT graph of module dependencies");
            
            task.doLast(t -> {
                try {
                    generateDotGraph(project);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to generate module dependency graph", e);
                }
            });
        });
    }

    public void generateDotGraph(Project project) throws IOException {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        
        // Add all projects to the graph
        project.getAllprojects().forEach(p -> graph.addVertex(p.getName()));
        
        // Add dependencies between projects
        project.getAllprojects().forEach(p -> {
            p.getConfigurations().forEach(config -> {
                config.getDependencies().forEach(dep -> {
                    if (dep instanceof org.gradle.api.artifacts.ProjectDependency) {
                        org.gradle.api.artifacts.ProjectDependency projectDep = 
                            (org.gradle.api.artifacts.ProjectDependency) dep;
                        graph.addEdge(p.getName(), projectDep.getDependencyProject().getName());
                    }
                });
            });
        });

        // Create DOT exporter
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, org.jgrapht.nio.Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            map.put("shape", DefaultAttribute.createAttribute("box"));
            return map;
        });

        // Export to file
        File outputFile = project.getLayout().getBuildDirectory().file("module-dependencies.dot").get().getAsFile();
        outputFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(outputFile)) {
            exporter.exportGraph(graph, writer);
        }
        
        project.getLogger().lifecycle("Module dependency graph generated at: " + outputFile.getAbsolutePath());
    }
} 
# SweetRainGarden Module Dependency Graph Generator [![Branch Checker](https://github.com/SweetRainGarden/SweetraingardenGradleModuledot/actions/workflows/branch_checker.yml/badge.svg)](https://github.com/SweetRainGarden/SweetraingardenGradleModuledot/actions/workflows/branch_checker.yml)

A Gradle plugin that generates a DOT graph visualization of module dependencies in your Android project.

## Installation

Add the plugin to your project's `settings.gradle` or `build.gradle`:

```groovy
plugins {
    id 'com.sweetraingarden.gplugin.moduledot' version '1.0.0'
}
```

## Usage

After applying the plugin, you can generate the module dependency graph by running:

```bash
./gradlew generateModuleDotGraph
```

This will create a `module-dependencies.dot` file in your project's build directory. You can visualize this file using tools like Graphviz:

```bash
dot -Tpng build/module-dependencies.dot -o module-dependencies.png
```

## Features

- Automatically detects all modules in your project
- Creates a directed graph showing module dependencies
- Generates DOT format output for easy visualization
- Works with any Android project using Gradle

## License

MIT License 

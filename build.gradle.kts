plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.9.22"
}

group = "com.sweetraingarden.gplugin"
val artifactId = "moduledot"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(gradleApi())
    implementation("org.jgrapht:jgrapht-core:1.5.1")
    implementation("org.jgrapht:jgrapht-io:1.5.1")
    
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.10.0")
}

tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("moduledot") {
            id = "$group.$artifactId"
            implementationClass = "com.sweetraingarden.gplugin.moduledot.ModuleDotPlugin"
            displayName = "Module Dependency Graph Generator"
            description = "Generates a DOT graph of module dependencies in an Gradle project"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "com.sweetraingarden.gplugin"
            artifactId = "moduledot"
            version = "1.0.0"
        }
    }
    repositories {
        maven {
            name = "local"
            url = uri("${buildDir}/repo")
        }
    }
}
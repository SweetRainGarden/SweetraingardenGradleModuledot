plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.9.25"
}

group = "com.sweetraingarden.gplugin"
version = "1.0.6"

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
            id = "$group.moduledot"
            implementationClass = "com.sweetraingarden.gplugin.moduledot.ModuleDotPlugin"
            displayName = "Module Dependency Graph Generator"
            description = "Generates a DOT graph of module dependencies in an Gradle project"
        }
    }
}

//./gradlew clean build publishGithubPublicationToGithubPackagesRepository
publishing {
    publications {
        create<MavenPublication>("github") {
            from(components["java"])
            groupId = "$group"
            artifactId = "moduledot"
            version = version
        }
    }
    repositories {
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/SweetRainGarden/SweetraingardenGradleModuledot")

            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
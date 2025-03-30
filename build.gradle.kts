import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.9.22"
    id("com.vanniktech.maven.publish") version "0.30.0"
    signing
}

group = "com.sweetraingarden.gplugin"
val artifactId = "moduledot"
version = "1.0.1"

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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(group.toString(), artifactId, version.toString())
    pom {
        name.set("Module Dependency Graph Generator")
        description.set("Generates a DOT graph of module dependencies in an Gradle project")
        url.set("https://github.com/SweetRainGarden/SweetraingardenGradleModuledot")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("SweetRainGarden")
                name.set("Sweet Rain Garden")
                url.set("https://github.com/SweetRainGarden/")
            }
        }
        scm {
            url.set("https://github.com/SweetRainGarden/SweetraingardenGradleModuledot")
            connection.set("scm:git:git://github.com/SweetRainGarden/SweetraingardenGradleModuledot.git")
            developerConnection.set("scm:git:ssh://git@github.com/SweetRainGarden/SweetraingardenGradleModuledot.git")
        }
    }
    signing {
        val signingKey = findProperty("signing.key") as? String
        val signingPassword = findProperty("signing.password") as? String
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
    }
}


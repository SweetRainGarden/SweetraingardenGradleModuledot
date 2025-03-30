plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.9.22"
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
            id = "${group}.$artifactId"
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
            groupId = group.toString()
            artifactId = artifactId
            version = version.toString()
            
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
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/SweetRainGarden/SweetraingardenGradleModuledot.git")
                    developerConnection.set("scm:git:ssh://github.com:SweetRainGarden/SweetraingardenGradleModuledot.git")
                    url.set("https://github.com/SweetRainGarden/SweetraingardenGradleModuledot")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("sonatypeUsername") as? String
                password = findProperty("sonatypePassword") as? String
            }
        }
    }
}


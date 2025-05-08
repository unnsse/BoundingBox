plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testCompileOnly(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation(libs.guava)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

application {
    // Define the main class for the application.
    mainClass = "com.example.BoundingBox"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Configure the JAR task to create an executable JAR with a manifest
tasks.jar {
    // Set the archive file name to bounding-box.jar
    archiveFileName.set("bounding-box.jar")

    // Configure the manifest to include the Main-Class attribute
    manifest {
        attributes(
            "Main-Class" to "com.example.BoundingBox"
        )
    }

    // Include dependencies in the JAR (create a "fat" JAR)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith(".jar") }.map { zipTree(it) }
    })

    // Avoid duplicate files in the JAR
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<CreateShellScriptTask>("createShellScript") {
    group = "build"
    description = "Creates a Unix shell script to run the bounding-box as an executable (e.g. ./bounding-box)"
    outputFile.set(layout.buildDirectory.file("libs/bounding-box"))
    outputs.upToDateWhen { false }
}

tasks.named("createShellScript") {
    mustRunAfter("test")
}

tasks.build {
    dependsOn("createShellScript")
}
plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("tablelayout:TableLayout:20050920")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Keep the original NetBeans layout: code and resources (img, txt, ...) both live in src/
sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources {
            setSrcDirs(listOf("src"))
            exclude("**/*.java")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass = "ics3u_summative_tower_defense.Main"
}

// The game currently loads its .wav files relative to the working directory
tasks.named<JavaExec>("run") {
    workingDir = projectDir
}

// Build a single runnable jar that includes TableLayout
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

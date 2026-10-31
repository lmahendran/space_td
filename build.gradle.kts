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

// Downloadable app: a folder with "Space Tower Defense.exe" and its own Java runtime, zipped up
val appName = "Space Tower Defense"
val appVersion = "1.0.1"
val jpackageInput = layout.buildDirectory.dir("jpackage/input")
val jpackageOutput = layout.buildDirectory.dir("jpackage/output")
val toolchainLauncher = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(21) }

val stageJpackageInput = tasks.register<Sync>("stageJpackageInput") {
    from(tasks.jar)
    into(jpackageInput)
}

val jpackageImage = tasks.register<Exec>("jpackageImage") {
    group = "distribution"
    description = "Builds a self-contained app folder with jpackage."
    dependsOn(stageJpackageInput)
    inputs.dir(jpackageInput)
    inputs.file("packaging/space_td.ico")
    outputs.dir(jpackageOutput)
    doFirst {
        // jpackage refuses to overwrite an existing app folder
        delete(jpackageOutput)
        executable = toolchainLauncher.get().metadata.installationPath.file("bin/jpackage").asFile.path
    }
    args(
        "--type", "app-image",
        "--name", appName,
        "--app-version", appVersion,
        "--input", jpackageInput.get().asFile.path,
        "--main-jar", "space_td.jar",
        "--main-class", application.mainClass.get(),
        "--icon", file("packaging/space_td.ico").path,
        "--add-modules", "java.desktop",
        "--dest", jpackageOutput.get().asFile.path
    )
}

// Windows installer (.exe) built from the app folder above; needs WiX Toolset 3 installed
tasks.register<Exec>("packageInstaller") {
    group = "distribution"
    description = "Builds a Windows installer with jpackage (requires WiX Toolset 3)."
    dependsOn(jpackageImage)
    val installerOutput = layout.buildDirectory.dir("jpackage/installer")
    outputs.dir(installerOutput)
    doFirst {
        delete(installerOutput)
        executable = toolchainLauncher.get().metadata.installationPath.file("bin/jpackage").asFile.path
    }
    args(
        "--type", "exe",
        "--name", appName,
        "--app-version", appVersion,
        "--app-image", jpackageOutput.get().dir(appName).asFile.path,
        "--vendor", "Logesh Mahendran",
        "--description", appName,
        "--win-per-user-install",
        "--win-menu",
        "--win-menu-group", appName,
        "--win-shortcut-prompt",
        "--win-shortcut",
        // Keep this fixed so new versions upgrade the existing install instead of adding a second copy
        "--win-upgrade-uuid", "1d846aa3-de62-475d-a710-79238f859ba3",
        "--dest", installerOutput.get().asFile.path
    )
}

tasks.register<Zip>("packageZip") {
    group = "distribution"
    description = "Zips the jpackage app folder for download."
    from(jpackageImage)
    archiveFileName = "SpaceTowerDefense-$appVersion-windows.zip"
    destinationDirectory = layout.buildDirectory.dir("distributions")
}

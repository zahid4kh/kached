import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import java.util.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.hotReload)
}

val appPackageVersion = "1.0.0"
group = "kached"
version = appPackageVersion

repositories {
    maven { url = uri("https://jitpack.io") }
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.components.resources)
    implementation(compose.materialIconsExtended)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)

    // Koin for dependency injection
    implementation(libs.koin.core)
    
    // PreCompose for Compose navigation and view models
    api(libs.precompose)
    api(libs.precompose.viewmodel)
    api(compose.foundation)
    api(compose.animation)
    implementation(libs.markdownRenderer)

    // Kotlin's datetime library
    implementation(libs.kotlinx.datetime)

    // SLF4J Logging (for hot reload)
    implementation(libs.bundles.slf4j)

    // Deskit - for Material3 file chooser and information dialogs
    implementation(libs.deskit)

    implementation("dev.snipme:highlights:1.0.0")
    implementation("dev.snipme:kodeview:0.9.0")
}


compose.desktop {
    application {
        /*
        must match the annotation in Main.kt
        @file:JvmName("Kached").
         */
        mainClass = "Kached"

        nativeDistributions {

            jvmArgs += listOf("-Dfile.encoding=UTF-8")
            
            buildTypes.release.proguard {
                configurationFiles.from("proguard-rules.pro")
                isEnabled = true
                obfuscate = false
                optimize = true
            }
            
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "kached"
            packageVersion = appPackageVersion

            linux{
                shortcut = true
                iconFile.set(project.file("icons/compose.png"))
                description = "Code snippet manager desktop app"
            }

            windows{
                shortcut = true
                dirChooser = true
                menu = true
                vendor = "Your Name or Company Name"
                upgradeUuid = "run the 'generateUpgradeUuid' task and paste the generated UUID here only once"
                iconFile.set(project.file("icons/compose.ico"))
            }

            macOS{
                dockName = "Kached"
                iconFile.set(project.file("icons/compose.icns"))
            }
        }
    }
}
    
//https://github.com/JetBrains/compose-hot-reload
composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}
  
tasks.register("generateUpgradeUuid") {
    group = "help"
    description = "Generates a unique UUID to be used for the Windows MSI upgradeUuid."
    doLast {
        println("--------------------------------------------------")
        println("Generated Upgrade UUID (must be pasted in the upgradeUuid for windows block only once so the MSI installer recognizes the update and does the uninstall/install):")
        println(UUID.randomUUID().toString())
        println("--------------------------------------------------")
    }
}

compose.resources{
    publicResClass = false
    packageOfResClass = "kached.resources"
    generateResClass = auto
}


// only for LinuxOS
val workDir = file("deb-temp")
val packageName = "kached"
val desktopRelativePath = "opt/$packageName/lib/$packageName-$packageName.desktop"
val appDisplayName = "Kached"
val mainClass = "Kached"
val maintainer = "Zahid Khalilov <halilzahid@gmail.com>"
val controlDescription = "Code snippet manager desktop app"


fun promptUserChoice(): String {
    println(
        """
        🧩 Which packaging task do you want to run?
        1 = packageDeb (default)
        2 = packageReleaseDeb
        """.trimIndent()
    )
    print("👉 Enter your choice [1/2]: ")

    return Scanner(System.`in`).nextLine().trim().ifEmpty { "1" }
}

tasks.register("addStartupWMClassToDebDynamic") {
    group = "release"
    description = "Finds .deb file, modifies .desktop, control files, and DEBIAN scripts, and rebuilds it"

    doLast {
        val debRoot = file("build/compose/binaries")
        if (!debRoot.exists()) throw GradleException("❌ Folder not found: ${debRoot}")

        val allDebs = debRoot.walkTopDown().filter { it.isFile && it.extension == "deb" }.toList()
        if (allDebs.isEmpty()) throw GradleException("❌ No .deb files found under ${debRoot}")

        // picking the latest .deb file
        val originalDeb = allDebs.maxByOrNull { it.lastModified() }!!
        println("📦 Found deb package: ${originalDeb.relativeTo(rootDir)}")

        val modifiedDeb = File(originalDeb.parentFile, originalDeb.nameWithoutExtension + "-wm.deb")

        // cleaning up "deb-temp" folder, if exists
        if (workDir.exists()) workDir.deleteRecursively()
        workDir.mkdirs()

        // Step 1: Extracting generated debian package
        exec {
            commandLine("dpkg-deb", "-R", originalDeb.absolutePath, workDir.absolutePath)
        }

        // Step 2: Modifying the desktop entry file
        val desktopFile = File(workDir, desktopRelativePath)
        if (!desktopFile.exists()) throw GradleException("❌ .desktop file not found: ${desktopRelativePath}")

        val lines = desktopFile.readLines().toMutableList()

        // Modifying the Name field (app's display name on dock)
        var nameModified = false
        for (i in lines.indices) {
            if (lines[i].trim().startsWith("Name=")) {
                lines[i] = "Name=$appDisplayName"
                nameModified = true
                println("✅ Modified Name entry to: $appDisplayName")
                break
            }
        }

        // adding Name field if it doesn't exist
        if (!nameModified) {
            lines.add("Name=$appDisplayName")
            println("✅ Added Name entry: $appDisplayName")
        }

        for (i in lines.indices) {
            if (lines[i].trim().startsWith("StartupWMClass=")) {
                if (lines[i] != "StartupWMClass=$mainClass") {
                    lines[i] = "StartupWMClass=$mainClass"
                    println("✅ Updated StartupWMClass entry to: $mainClass")
                } else {
                    println("ℹ️ StartupWMClass already correctly set to: $mainClass")
                }
                break
            }
        }

        // Adding StartupWMClass if it doesn't exist
        if (!lines.any { it.trim().startsWith("StartupWMClass=") }) {
            lines.add("StartupWMClass=$mainClass")
            println("✅ Added StartupWMClass entry: $mainClass")
        }

        // Writing changes back to file
        desktopFile.writeText(lines.joinToString("\n"))

        println("\n📄 Final .desktop file content:")
        println("--------------------------------")
        desktopFile.readLines().forEach { println(it) }
        println("--------------------------------\n")

        // Step 3: Modifying the DEBIAN/control file
        val controlFile = File(workDir, "DEBIAN/control")
        if (!controlFile.exists()) throw GradleException("❌ control file not found: DEBIAN/control")

        val controlLines = controlFile.readLines().toMutableList()

        // Update maintainer field
        var maintainerModified = false
        for (i in controlLines.indices) {
            if (controlLines[i].trim().startsWith("Maintainer:")) {
                controlLines[i] = "Maintainer: $maintainer"
                maintainerModified = true
                println("✅ Modified Maintainer entry")
                break
            }
        }

        // Add maintainer field if it doesn't exist
        if (!maintainerModified) {
            controlLines.add("Maintainer: $maintainer")
            println("✅ Added Maintainer entry")
        }

        // Update description field for better info
        for (i in controlLines.indices) {
            if (controlLines[i].trim().startsWith("Description:")) {
                controlLines[i] = "Description: $controlDescription"
                println("✅ Modified Description entry")
                break
            }
        }

        // Write changes back to control file
        controlFile.writeText(controlLines.joinToString("\n"))

        println("\n📄 Final control file content:")
        println("--------------------------------")
        controlFile.readLines().forEach { println(it) }
        println("--------------------------------\n")

        // Step 4: Modifying the DEBIAN/postinst script
        val postinstFile = File(workDir, "DEBIAN/postinst")
        if (!postinstFile.exists()) throw GradleException("❌ postinst file not found: DEBIAN/postinst")

        val postinstContent = """#!/bin/sh
# postinst script for $packageName
#
# see: dh_installdeb(1)

set -e

# summary of how this script can be called:
#        * <postinst> `configure` <most-recently-configured-version>
#        * <old-postinst> `abort-upgrade` <new version>
#        * <conflictor's-postinst> `abort-remove` `in-favour` <package>
#          <new-version>
#        * <postinst> `abort-remove`
#        * <deconfigured's-postinst> `abort-deconfigure` `in-favour`
#          <failed-install-package> <version> `removing`
#          <conflicting-package> <version>
# for details, see https://www.debian.org/doc/debian-policy/ or
# the debian-policy package

case "$1" in
    configure)
        # Install desktop menu entry
        xdg-desktop-menu install /opt/$packageName/lib/$packageName-$packageName.desktop
        
        # Create symlink for terminal access
        if [ ! -L /usr/local/bin/$packageName ]; then
            ln -sf /opt/$packageName/bin/$packageName /usr/local/bin/$packageName
            echo "Created symlink: /usr/local/bin/$packageName -> /opt/$packageName/bin/$packageName"
        fi
    ;;

    abort-upgrade|abort-remove|abort-deconfigure)
    ;;

    *)
        echo "postinst called with unknown argument `$1`" >&2
        exit 1
    ;;
esac

exit 0"""

        postinstFile.writeText(postinstContent)
        println("✅ Updated postinst script to create terminal symlink")

        // Step 5: Modifying the DEBIAN/prerm script
        val prermFile = File(workDir, "DEBIAN/prerm")
        if (!prermFile.exists()) throw GradleException("❌ prerm file not found: DEBIAN/prerm")

        val prermContent = """#!/bin/sh
# prerm script for $packageName
#
# see: dh_installdeb(1)

set -e

# summary of how this script can be called:
#        * <prerm> `remove`
#        * <old-prerm> `upgrade` <new-version>
#        * <new-prerm> `failed-upgrade` <old-version>
#        * <conflictor's-prerm> `remove` `in-favour` <package> <new-version>
#        * <deconfigured's-prerm> `deconfigure` `in-favour`
#          <package-being-installed> <version> `removing`
#          <conflicting-package> <version>
# for details, see https://www.debian.org/doc/debian-policy/ or
# the debian-policy package

case "$1" in
    remove|upgrade|deconfigure)
        # Remove desktop menu entry
        xdg-desktop-menu uninstall /opt/$packageName/lib/$packageName-$packageName.desktop
        
        # Remove terminal symlink
        if [ -L /usr/local/bin/$packageName ]; then
            rm -f /usr/local/bin/$packageName
            echo "Removed symlink: /usr/local/bin/$packageName"
        fi
    ;;

    failed-upgrade)
    ;;

    *)
        echo "prerm called with unknown argument `$1`" >&2
        exit 1
    ;;
esac

exit 0"""

        prermFile.writeText(prermContent)
        println("✅ Updated prerm script to remove terminal symlink")

        // Make sure scripts are executable
        exec {
            commandLine("chmod", "+x", postinstFile.absolutePath)
        }
        exec {
            commandLine("chmod", "+x", prermFile.absolutePath)
        }

        println("\n📄 Final postinst script content:")
        println("--------------------------------")
        postinstFile.readLines().forEach { println(it) }
        println("--------------------------------\n")

        println("\n📄 Final prerm script content:")
        println("--------------------------------")
        prermFile.readLines().forEach { println(it) }
        println("--------------------------------\n")

        // Step 6: Repackaging the debian package back
        exec {
            commandLine("dpkg-deb", "-b", workDir.absolutePath, modifiedDeb.absolutePath)
        }

        println("✅ Done: Rebuilt with Name=$appDisplayName, StartupWMClass=$mainClass, updated control file, and terminal symlink -> ${modifiedDeb.name}")
    }
}


tasks.register("packageDebWithWMClass") {
    group = "release"
    description = "Runs packaging task (packageDeb or packageReleaseDeb), then adds StartupWMClass"

    doLast {
        val choice = promptUserChoice()

        val packagingTask = when (choice) {
            "2" -> "packageReleaseDeb"
            else -> "packageDeb"
        }

        println("▶️ Running: ${packagingTask}")
        gradle.includedBuilds.forEach { it.task(":${packagingTask}") } // just in case of composite builds

        exec {
            commandLine("./gradlew clean")
            commandLine("./gradlew", packagingTask)
        }

        tasks.named("addStartupWMClassToDebDynamic").get().actions.forEach { it.execute(this) }
    }
}
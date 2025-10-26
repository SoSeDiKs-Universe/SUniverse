import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

description = "Holds a void generator, that's it"

plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1" // Test server
}

tasks {
    runServer {
        minecraftVersion(rootProject.property("mcVersion").toString())
        runDirectory = rootProject.projectDir.resolve("server")
        serverJar(rootProject.projectDir.resolve("server/server.jar"))
        repositories {
            mavenLocal()
        }
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition", "--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

paper {
    name = "LimboWorldGenerator"
    main = "me.sosedik.limboworldgenerator.LimboWorldGenerator"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

description = "Holds a void generator, that's it"

plugins {
    alias(libs.plugins.run.paper) // Test server
}

tasks {
    runServer {
        minecraftVersion(libs.versions.mcVersion.get())
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
        languageVersion = JavaLanguageVersion.of(libs.versions.javaVersion.get())
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition", "--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

paper {
    name = "LimboWorldGenerator"
    main = "me.sosedik.limboworldgenerator.LimboWorldGenerator"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

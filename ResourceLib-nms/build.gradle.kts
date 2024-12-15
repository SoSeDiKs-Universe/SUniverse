import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Server resource pack generator"

dependencies {
    compileOnly(project(":Utilizer"))
    compileOnly(project(":FancyMotd"))

    paperLibrary("net.lingala.zip4j:zip4j:2.11.5")

    compileOnly("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    compileOnly("org.incendo:cloud-annotations:${project.property("cloudVersion")}")
    compileOnly("com.github.retrooper:packetevents-spigot:${project.property("packeteventsVersion")}")
}

tasks.withType<ShadowJar> {
    val rpDir = "${projectDir.parentFile.parentFile.path}/ResourcePacker/output"
    from("$rpDir/mappings") {
        include("fonts.json", "items.json", "tripwire.json")
        into("mappings")
    }
    from(rpDir) {
        include("resource-pack.zip")
    }
}

paper {
    name = "ResourceLib"
    loader
    main = "me.sosedik.resourcelib.ResourceLib"
    loader = "me.sosedik.resourcelib.PaperPluginLibrariesLoader"
    bootstrapper = "me.sosedik.resourcelib.ResourceLibBootstrap"
    generateLibrariesJson = true

    bootstrapDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("FancyMotd") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
}

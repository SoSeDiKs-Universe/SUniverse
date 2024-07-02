import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Server resource pack generator"

dependencies {
    compileOnly(project(":Utilizer"))
    compileOnly(project(":FancyMotd"))

    paperLibrary("net.lingala.zip4j:zip4j:2.11.5")

    compileOnly("com.github.retrooper:packetevents-spigot:${project.property("packeteventsVersion")}")
}

paper {
    name = "ResourceLib"
    loader
    main = "me.sosedik.resourcelib.ResourceLib"
    loader = "me.sosedik.resourcelib.PaperPluginLibrariesLoader"
    bootstrapper = "me.sosedik.resourcelib.ResourceLibBootstrap"
    generateLibrariesJson = true

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

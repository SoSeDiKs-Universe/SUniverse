import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Shared dumping ground for SoSeDiK's Universe witchery"

dependencies {
    paperLibrary("com.zaxxer:HikariCP:7.0.2")

    paperLibrary("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    paperLibrary("org.incendo:cloud-annotations:${project.property("cloudVersion")}")

    compileOnly("de.tr7zw:item-nbt-api-plugin:${project.property("nbtApiVersion")}")
    compileOnly("com.github.retrooper:packetevents-spigot:${project.property("packeteventsVersion")}")
}

paper {
    name = "Utilizer"
    main = "me.sosedik.utilizer.Utilizer"
    loader = "me.sosedik.utilizer.PaperPluginLibrariesLoader"
    bootstrapper = "me.sosedik.utilizer.UtilizerBootstrap"
    generateLibrariesJson = true

    serverDependencies {
        register("NBTAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

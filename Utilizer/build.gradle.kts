import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Shared dumping ground for SoSeDiK's Universe witchery"

dependencies {
    paperLibrary("com.zaxxer:HikariCP:5.1.0")

    paperLibrary("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    paperLibrary("org.incendo:cloud-annotations:${project.property("cloudVersion")}")

    compileOnly("com.github.retrooper:packetevents-spigot:${project.property("packeteventsVersion")}")
}

paper {
    name = "Utilizer"
    main = "me.sosedik.utilizer.Utilizer"
    loader = "me.sosedik.utilizer.PaperPluginLibrariesLoader"
    generateLibrariesJson = true

    serverDependencies {
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

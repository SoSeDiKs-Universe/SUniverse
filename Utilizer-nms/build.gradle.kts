import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Shared dumping ground for SoSeDiK's Universe witchery"

dependencies {
    compileOnly(project(":LimboWorldGenerator"))

    paperLibrary(libs.hikariCP)

    paperLibrary(libs.cloud.paper)
    paperLibrary(libs.cloud.annotations)

    paperLibrary(libs.invui)

    compileOnly(libs.item.nbt.api.plugin)
    compileOnly(libs.packetevents.spigot)
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
        register("LimboWorldGenerator") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
    }
}

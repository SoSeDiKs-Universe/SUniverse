import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Shared dumping ground for SoSeDiK's Universe witchery"

repositories {
    maven("https://jitpack.io") // TODO
}

dependencies {
    paperLibrary("com.github.onebeastchris.cloud-minecraft:cloud-paper:jitpack-SNAPSHOT")
//    paperLibrary("org.incendo:cloud-paper:${project.property("cloudImplVersion")}") // TODO
    paperLibrary("org.incendo:cloud-annotations:${project.property("cloudVersion")}")

    compileOnly("com.github.retrooper:packetevents-spigot:2.3.1-SNAPSHOT")
}

paper {
    name = "Utilizer"
    main = "me.sosedik.utilizer.Utilizer"
    loader = "me.sosedik.utilizer.PaperPluginLibrariesLoader"
    generateLibrariesJson = true

    serverDependencies {
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}
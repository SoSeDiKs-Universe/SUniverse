import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "The burden of life"

dependencies {
    compileOnly(project(":Utilizer"))

    compileOnly("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    compileOnly("org.incendo:cloud-annotations:${project.property("cloudVersion")}")

    compileOnly("com.github.retrooper:packetevents-spigot:${project.property("packeteventsVersion")}")
}

paper {
    name = "Requiem"
    main = "me.sosedik.requiem.Requiem"

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Insane tools for insane people"

dependencies {
    compileOnly(project(":Utilizer"))

    compileOnly("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    compileOnly("org.incendo:cloud-annotations:${project.property("cloudVersion")}")
}

paper {
    name = "Essence"
    main = "me.sosedik.essence.Essence"

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

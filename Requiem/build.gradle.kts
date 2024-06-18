import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "The burden of life"

dependencies {
    compileOnly(project(":Utilizer"))

    compileOnly("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    compileOnly("org.incendo:cloud-annotations:${project.property("cloudVersion")}")
}

paper {
    name = "Requiem"
    main = "me.sosedik.requiem.Requiem"

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

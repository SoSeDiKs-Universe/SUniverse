import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "The burden of life"

dependencies {
    compileOnly(project(":Utilizer"))
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

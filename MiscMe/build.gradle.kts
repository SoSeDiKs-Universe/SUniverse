import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Various miscellaneous features"

dependencies {
    compileOnly(project(":Utilizer"))
}

paper {
    name = "MiscMe"
    main = "me.sosedik.miscme.MiscMe"

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

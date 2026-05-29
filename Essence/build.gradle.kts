import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Insane tools for insane people"

dependencies {
    compileOnly(project(":Utilizer-nms"))

    compileOnly(libs.cloud.paper)
    compileOnly(libs.cloud.annotations)
}

paper {
    name = "Essence"
    main = "me.sosedik.essence.Essence"

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Server motd fancifier"

dependencies {
    compileOnly(project(":Utilizer-nms"))
}

paper {
    name = "FancyMotd"
    main = "me.sosedik.fancymotd.FancyMotd"

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
}

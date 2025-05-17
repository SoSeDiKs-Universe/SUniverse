import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Endless farming simulator (so fun!)"

dependencies {
    compileOnly(project(":Utilizer"))
    compileOnly(project(":ResourceLib-nms"))
}

paper {
    name = "DelightfulFarming"
    main = "me.sosedik.delightfulfarming.DelightfulFarming"
    bootstrapper = "me.sosedik.delightfulfarming.DelightfulFarmingBootstrap"

    bootstrapDependencies {
        register("ResourceLib") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("ResourceLib") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

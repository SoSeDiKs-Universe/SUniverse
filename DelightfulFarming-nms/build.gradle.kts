import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Endless farming simulator (so fun!) for sweet-sweet sugar eaters"

dependencies {
    compileOnly(project(":Utilizer"))
    compileOnly(project(":ResourceLib-nms"))

    compileOnly("de.tr7zw:item-nbt-api-plugin:${project.property("nbtApiVersion")}")
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
        register("NBTAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
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

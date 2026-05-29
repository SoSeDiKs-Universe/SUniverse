import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Endless farming simulator (so fun!) for sweet-sweet sugar eaters"

dependencies {
    compileOnly(project(":Utilizer-nms"))
    compileOnly(project(":ResourceLib-nms"))
    compileOnly(project(":MiscMe"))

    compileOnly(libs.item.nbt.api.plugin)
    compileOnly(libs.packetevents.spigot)
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
        register("packetevents") {
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
        register("MiscMe") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "The burden of life"

dependencies {
    compileOnly(project(":Utilizer-nms"))
    compileOnly(project(":ResourceLib-nms"))
    compileOnly(project(":Moves"))

    compileOnly(libs.cloud.paper)
    compileOnly(libs.cloud.annotations)

    compileOnly(libs.item.nbt.api.plugin)
    compileOnly(libs.packetevents.spigot)
}

paper {
    name = "Requiem"
    main = "me.sosedik.requiem.Requiem"
    bootstrapper = "me.sosedik.requiem.RequiemBootstrap"

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
        register("Moves") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("NBTAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

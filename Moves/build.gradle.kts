import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Shake your booty"

dependencies {
    compileOnly(project(":Utilizer-nms"))

    compileOnly(libs.cloud.paper)
    compileOnly(libs.cloud.annotations)

    compileOnly(libs.item.nbt.api.plugin)
}

paper {
    name = "Moves"
    main = "me.sosedik.moves.Moves"

    serverDependencies {
        register("NBTAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

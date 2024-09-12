import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Shake your booty"

dependencies {
    compileOnly(project(":Utilizer"))

    compileOnly("de.tr7zw:item-nbt-api-plugin:${project.property("nbtApiVersion")}")
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

import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Shake your booty"

dependencies {
    compileOnly(project(":Utilizer"))
}

paper {
    name = "Moves"
    main = "me.sosedik.moves.Moves"

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

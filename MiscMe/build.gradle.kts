import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Various miscellaneous features"

dependencies {
    compileOnly(project(":Utilizer"))
    compileOnly(project(":UglyChatter"))

    compileOnly("de.tr7zw:item-nbt-api-plugin:${project.property("nbtApiVersion")}")
}

paper {
    name = "MiscMe"
    main = "me.sosedik.miscme.MiscMe"

    serverDependencies {
        register("NBTAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("UglyChatter") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

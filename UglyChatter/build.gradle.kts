import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Uglifies everything to make you look beautiful"

dependencies {
    compileOnly(project(":Utilizer"))
    compileOnly(project(":ResourceLib-nms"))

    paperLibrary("org.commonmark:commonmark:${project.property("commonmarkVersion")}")
    paperLibrary("org.commonmark:commonmark-ext-autolink:${project.property("commonmarkVersion")}")

    compileOnly("com.github.retrooper:packetevents-spigot:${project.property("packeteventsVersion")}")
}

paper {
    name = "UglyChatter"
    main = "me.sosedik.uglychatter.UglyChatter"
    loader = "me.sosedik.uglychatter.PaperPluginLibrariesLoader"
    generateLibrariesJson = true

    serverDependencies {
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("ResourceLib") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

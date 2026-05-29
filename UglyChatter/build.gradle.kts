import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Uglifies everything to make you look beautiful"

dependencies {
    compileOnly(project(":Utilizer-nms"))
    compileOnly(project(":ResourceLib-nms"))

    paperLibrary(libs.commonmark)
    paperLibrary(libs.commonmark.ext.autolink)

    compileOnly(libs.packetevents.spigot)
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

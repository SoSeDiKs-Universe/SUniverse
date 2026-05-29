import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Because community is good"

dependencies {
    compileOnly(project(":Utilizer-nms"))
    compileOnly(project(":ResourceLib-nms"))
    compileOnly(project(":UglyChatter"))

    compileOnly(libs.item.nbt.api.plugin)

    compileOnly(libs.cloud.paper)
    compileOnly(libs.cloud.annotations)

    paperLibrary(libs.jda) {
        exclude("opus-java")
    }
    paperLibrary(libs.discord.webhooks)
}

paper {
    name = "Socializer"
    main = "me.sosedik.socializer.Socializer"
    loader = "me.sosedik.socializer.PaperPluginLibrariesLoader"
    generateLibrariesJson = true

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
        register("UglyChatter") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

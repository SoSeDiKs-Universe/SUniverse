import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Because community is good"

dependencies {
    compileOnly(project(":Utilizer"))
    compileOnly(project(":ResourceLib-nms"))
    compileOnly(project(":UglyChatter"))

    compileOnly("de.tr7zw:item-nbt-api-plugin:${project.property("nbtApiVersion")}")

    compileOnly("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    compileOnly("org.incendo:cloud-annotations:${project.property("cloudVersion")}")

    paperLibrary("net.dv8tion:JDA:5.6.1") {
        exclude("opus-java")
    }
    paperLibrary("club.minnced:discord-webhooks:0.8.4")
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

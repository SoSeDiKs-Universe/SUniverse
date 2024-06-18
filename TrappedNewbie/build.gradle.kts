import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Once you're in, there's no way out"

dependencies {
    compileOnly(project(":LimboWorldGenerator"))
    compileOnly(project(":Utilizer"))
    compileOnly(project(":Moves"))
    compileOnly(project(":Requiem"))

    compileOnly("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
}

paper {
    name = "TrappedNewbie"
    main = "me.sosedik.trappednewbie.TrappedNewbie"

    serverDependencies {
        register("LimboWorldGenerator") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Moves") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Requiem") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        // TrappedNewbie registers custom world parser that Essence's /world can use
        register("Essence") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
    }
}

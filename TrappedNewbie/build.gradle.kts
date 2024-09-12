import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Once you're in, there's no way out"

dependencies {
    compileOnly(project(":LimboWorldGenerator"))
    compileOnly(project(":Utilizer"))
    compileOnly(project(":ResourceLib-nms"))
    compileOnly(project(":MiscMe"))
    compileOnly(project(":Moves"))
    compileOnly(project(":Requiem"))

    compileOnly("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
}

paper {
    name = "TrappedNewbie"
    main = "me.sosedik.trappednewbie.TrappedNewbie"
    bootstrapper = "me.sosedik.trappednewbie.TrappedNewbieBootstrap"

    bootstrapDependencies {
        register("ResourceLib") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }

    serverDependencies {
        register("LimboWorldGenerator") {
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
        register("MiscMe") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("Moves") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("Requiem") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        // TrappedNewbie registers custom world parser that Essence's /world can use
        register("Essence") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
            required = false
        }
    }
}

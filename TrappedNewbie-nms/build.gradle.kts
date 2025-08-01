import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Once you're in, there's no way out"

dependencies {
    compileOnly(project(":LimboWorldGenerator"))
    compileOnly(project(":Utilizer"))
    compileOnly(project(":ResourceLib-nms"))
    compileOnly(project(":MiscMe"))
    compileOnly(project(":Moves"))
    compileOnly(project(":FancyMotd"))
    compileOnly(project(":Requiem"))
    compileOnly(project(":Socializer"))
    compileOnly(project(":DelightfulFarming-nms"))

    compileOnly("me.sosedik:PacketAdvancements-api:1.0-SNAPSHOT")
    compileOnly("de.tr7zw:item-nbt-api-plugin:${project.property("nbtApiVersion")}")
    compileOnly("com.github.retrooper:packetevents-spigot:${project.property("packeteventsVersion")}")

    compileOnly("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    compileOnly("org.incendo:cloud-annotations:${project.property("cloudVersion")}")
}

tasks.withType<ShadowJar> {
    val rpDir = "${projectDir.parentFile.parentFile.path}/ResourcePacker/output"
    from("$rpDir/data-pack") {
        into("datapack")
    }
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
        register("NBTAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("PacketAdvancements") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
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
        register("Socializer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("DelightfulFarming") {
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

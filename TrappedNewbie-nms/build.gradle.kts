import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.paper.PaperPluginDescription

description = "Once you're in, there's no way out"

dependencies {
    compileOnly(project(":LimboWorldGenerator"))
    compileOnly(project(":Utilizer-nms"))
    compileOnly(project(":ResourceLib-nms"))
    compileOnly(project(":Essence"))
    compileOnly(project(":MiscMe"))
    compileOnly(project(":Moves"))
    compileOnly(project(":FancyMotd"))
    compileOnly(project(":Requiem"))
    compileOnly(project(":Socializer"))
    compileOnly(project(":UglyChatter"))
    compileOnly(project(":DelightfulFarming-nms"))

    compileOnly(libs.packetadvancements.api)
    compileOnly(libs.item.nbt.api.plugin)
    compileOnly(libs.packetevents.spigot)
    compileOnly(libs.bettermodel.bukkit.api)

    compileOnly(libs.cloud.paper)
    compileOnly(libs.cloud.annotations)

    compileOnly(libs.invui)
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
            joinClasspath = true
        }
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("BetterModel") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("PacketAdvancements") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("LimboWorldGenerator") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("Utilizer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("ResourceLib") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("Essence") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
            required = false
            joinClasspath = true
        }
        register("MiscMe") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("Moves") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("Requiem") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("Socializer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("UglyChatter") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("DelightfulFarming") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
    }
}

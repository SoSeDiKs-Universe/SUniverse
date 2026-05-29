plugins {
    `java-library`
    alias(libs.plugins.paperweight.userdev) // Accessing NMS
    alias(libs.plugins.plugin.yml.paper) // Generates paper-plugin.yml
    alias(libs.plugins.shadow) // Shading
}

val mcVersion: String = libs.versions.mcVersion.get()
val paperDevVersion: String = "${mcVersion}.local-SNAPSHOT"
dependencies {
    paperweight.paperDevBundle(paperDevVersion, "me.sosedik.kiterino")
}

allprojects {
    group = "me.sosedik"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-public/") // NBT-API, packetevents
        maven("https://repo.xenondevs.xyz/releases") // InvUI
        mavenLocal() // Kiterino
    }
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply(plugin = "com.gradleup.shadow")

    tasks {
        build {
            if (project.name == "ResourceLib-nms" || project.name == "TrappedNewbie-nms") {
                dependsOn(shadowJar)
            }

            doLast {
                val pluginsPath: String = projectDir.parentFile.path + "/server/plugins"
                val finalFileName: String = project.name.replace("-nms", "")
                var fileName: String = project.name + "-" + version + "-all.jar"
                if (!file("./build/libs/$fileName").exists())
                    fileName = project.name + "-" + version + ".jar"
                copy {
                    from("./build/libs/$fileName")
                    into(pluginsPath)
                    rename(fileName, "${finalFileName}.jar")
                }
            }
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(libs.versions.javaVersion.get().toInt())
        }
        javadoc {
            options.encoding = Charsets.UTF_8.name()
        }
        processResources {
            filteringCharset = Charsets.UTF_8.name()
        }
    }

    if (project.name.endsWith("-nms")) {
        apply(plugin = "io.papermc.paperweight.userdev")
        dependencies {
            paperweight.paperDevBundle(paperDevVersion, "me.sosedik.kiterino")
        }
    } else {
        dependencies {
            compileOnly("me.sosedik.kiterino:kiterino-api:$paperDevVersion")
        }
    }

    apply(plugin = "de.eldoria.plugin-yml.paper")
    paper {
        apiVersion = mcVersion
        authors = listOf("SoSeDiK")
    }
}

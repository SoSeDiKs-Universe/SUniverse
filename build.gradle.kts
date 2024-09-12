plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.2" // Accessing NMS
    id("net.minecrell.plugin-yml.paper") version "0.6.0" // Generates paper-plugin.yml
}

val mcVersion: String = project.property("mcVersion").toString()
dependencies {
    paperweight.paperDevBundle(mcVersion, "me.sosedik.kiterino")
}

allprojects {
    group = "me.sosedik"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-public/") // NBT-API, packetevents
        mavenLocal() // Kiterino
    }
}

subprojects {
    apply<JavaLibraryPlugin>()

    tasks {
        build {
            doLast {
                val pluginsPath: String = projectDir.parentFile.path + "/server/plugins"
                val finalFileName: String = project.name.replace("-nms", "")
                val fileName: String = if (project.name.endsWith("-nms")) project.name + "-" + version + "-dev.jar" else project.name + "-" + version + ".jar"
                copy {
                    from("./build/libs/$fileName")
                    into(pluginsPath)
                    rename(fileName, "${finalFileName}.jar")
                }
            }
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(21)
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
            paperweight.paperDevBundle(mcVersion, "me.sosedik.kiterino")
        }
    } else {
        dependencies {
            compileOnly("me.sosedik.kiterino:kiterino-api:$mcVersion")
        }
    }

    apply(plugin = "net.minecrell.plugin-yml.paper")
    paper {
        apiVersion = "${project.property("apiVersion")}"
        authors = listOf("SoSeDiK")
    }
}

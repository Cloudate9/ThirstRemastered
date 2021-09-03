import kr.entree.spigradle.kotlin.*

plugins {
    id("com.github.johnrengelman.shadow") version ("7.0.0")
    `java-gradle-plugin`
    id("kr.entree.spigradle") version ("2.2.4")
    kotlin("jvm") version ("1.5.21")
    kotlin("kapt") version "1.5.30"
}

group = "io.github.awesomemoder316.thirstremastered"
version = "1.0.0"

repositories {
    codemc()
    mavenCentral()
    spigotmc()
    sonatype()
    maven { url = uri("https://repo.mattstudios.me/artifactory/public/") }
}

dependencies {
    implementation("com.google.dagger:dagger:2.38.1")
    kapt("com.google.dagger:dagger-compiler:2.38.1")
    implementation("dev.triumphteam:triumph-gui:3.0.3")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
    implementation("net.wesjd:anvilgui:1.5.3-SNAPSHOT")
    implementation(bStats("2.2.1"))
    implementation(kotlin("stdlib"))
    spigot("1.17.1")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "16"
}

artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    archiveFileName.set(rootProject.name + "-" + rootProject.version + ".jar")

    relocate("dev.triumphteam", "io.github.awesomemoder316.lib.api")
    relocate("kotlin", "io.github.awesomemoder316.lib.dependencies")
    relocate("net.kyori", "io.github.awesomemoder316.lib.api")
    relocate("net.wesjd", "io.github.awesomemoder316.lib.api")
    relocate("org.bstats", "io.github.awesomemoder316.lib.api")
}

spigot {
    authors = listOf("Awesomemoder316")
    apiVersion = "1.17"
    description = "A plugin introducing thirst into Minecraft!"
    website = "https://github.com/awesomemoder316/ThirstRemastered"

    /*commands {
        create("") {
            description = ""
            usage = ""
        }
    }

    permissions {
        create("moderslib.receivepluginupdates") {
            description = "Permission to view updates managed by ModersLib."
            defaults = "op"
        }
    }*/
}

class BumpLatestVersionMd: Plugin<Project> {
    override fun apply(project: Project) {
        project.task("bumpLastestVersionMd") {
            val latestVersionMd = File("${project.rootDir}/latestVersion.md")
            doLast {
                val read = latestVersionMd.bufferedReader()
                val oldVersion = read.readLine() //Only one line expected.
                read.close()

                if (oldVersion != project.version) latestVersionMd.writeText(project.version.toString())
            }
        }
    }
}

apply<BumpLatestVersionMd>()
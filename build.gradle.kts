import kr.entree.spigradle.kotlin.*

plugins {
    id("com.github.johnrengelman.shadow") version ("7.0.0")
    id("kr.entree.spigradle") version ("2.2.4")
    kotlin("jvm") version ("1.5.30")
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
    //DI stuff
    implementation("io.insert-koin:koin-core:3.1.2")

    implementation("dev.triumphteam:triumph-gui:3.0.3")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
    implementation("net.wesjd:anvilgui:1.5.3-SNAPSHOT")
    implementation(bStats("2.2.1"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    compileOnly(spigot("1.17.1"))
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "16"
    finalizedBy("bumpLatestVersionMd") //Make updater run all the time.
}

artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    archiveFileName.set(rootProject.name + "-" + rootProject.version + ".jar")


    relocate("dev.triumphteam", "io.github.awesomemoder316.lib.api")
    relocate("kotlin", "io.github.awesomemoder316.lib.dependencies")
    relocate("net.kyori", "io.github.awesomemoder316.lib.api")
    relocate("net.wesjd", "io.github.awesomemoder316.lib.api")
    relocate("org.bstats", "io.github.awesomemoder316.lib.api")
    relocate("org.koin", "io.github.awesomemoder316.lib.api")


}

spigot {
    authors = listOf("Awesomemoder316")
    apiVersion = "1.17"
    description = "A plugin introducing thirst into Minecraft!"
    website = "https://github.com/awesomemoder316/ThirstRemastered"

    commands {
        create("ThirstRemastered") {
            aliases = listOf("tr", "tm", "thirstr", "tremastered")
            description = "The thirst remastered command."
            usage = "/tr"
        }
    }

    permissions {
        create("thirstremastered.configure") {
            description = "Permission to configure ThirstRemastered for the server."
            defaults = "op"
        }
    }
}

class BumpLatestVersionMd: Plugin<Project> {
    override fun apply(project: Project) {
        project.task("bumpLatestVersionMd") {
            val latestVersionMd = File("${project.rootDir}/src/main/resources", "latestVersion.md")
            if (!latestVersionMd.exists()) latestVersionMd.createNewFile()
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
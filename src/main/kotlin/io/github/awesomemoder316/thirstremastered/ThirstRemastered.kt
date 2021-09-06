package io.github.awesomemoder316.thirstremastered

import io.github.awesomemoder316.thirstremastered.koin.*
import kr.entree.spigradle.annotations.SpigotPlugin
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

@SpigotPlugin
class ThirstRemastered: JavaPlugin() {

    companion object {
        lateinit var adventure: BukkitAudiences
        lateinit var INSTANCE: ThirstRemastered
    }

    private lateinit var koinComponents: KoinComponents
    var firstCheck = true
    var updateDetected = false


    override fun onEnable() {

        INSTANCE = this
        adventure = BukkitAudiences.create(this)

        config.options().copyDefaults(true)
        config.options().copyHeader()
        saveConfig()

        startKoin {
            modules(listOf(commands, data, gui, listeners, libs))
        }

        koinComponents = KoinComponents()

        updateCheck()

        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(koinComponents.startStopPassiveThirst, this)

        getCommand("thirstremastered")?.setExecutor(koinComponents.viewChangeCommand)
    }

    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) koinComponents.iPlayerDataManager.removePlayer(player.uniqueId)

        adventure.close()
        stopKoin()
    }

    private fun updateCheck() { //The proper method can be found in "ModersLib" by Awesomemoder316
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val readGit = BufferedReader(
                        InputStreamReader(
                            URL(
                                "https://raw.githubusercontent.com/awesomemoder316/ThirstRemastered/master/src/main/resources/latestVersion.md"
                            ).openStream()
                        )
                    )
                    val output = readGit.readLine() //Only one line expected.
                    readGit.close()

                    val versionFile = getResource("latestVersion.md")
                    val version = versionFile!!.bufferedReader().readLine() //Assume only one line

                    if (output == version ||
                        output.lowercase().endsWith("alpha")
                        || output.lowercase().endsWith("beta")
                    ) {
                        if (firstCheck) {
                            logger.info(ChatColor.AQUA.toString() + "is up to date!")
                            firstCheck = false
                        }
                        val scheduler = server.scheduler
                        scheduler.scheduleSyncDelayedTask(this@ThirstRemastered, { updateCheck() }, 576000)
                        return
                    }
                     updateDetected = true
                    logger.info(ChatColor.AQUA.toString() + "can be updated at "/*TODO(Add the spigot link.)*/)
                } catch (ex: IOException) {
                    logger.info(ChatColor.RED.toString() + "Failed to check for updates!")
                    val scheduler = server.scheduler
                    scheduler.scheduleSyncDelayedTask(this@ThirstRemastered, { updateCheck() }, 576000)
                }
            }
        }.runTaskAsynchronously(this)
    }

}


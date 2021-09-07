package io.github.awesomemoder316.thirstremastered.data

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class PlayerDataConfig(private val plugin: JavaPlugin): IPlayerDataConfig {

    private fun addDefaults(dataFile: File, dataConfig: YamlConfiguration) {
        dataConfig.options().header(
            "The data in this file is not recommended to be manually edited.\n" +
                    "thirstLevel: The amount of thirst a player has, with 20 being the maximum and 0 being the minimum.\n" +
                    "passiveThirst: The number of ticks till the player takes one point of thirst.\n" +
                    "viewType: The position where the amount of thirst is shown to the player. Possible values are \"BOSSBAR\" and \"SCOREBOARD\""
        )

        dataConfig.addDefault("thirstLevel", 20)
        dataConfig.addDefault("passiveThirst", plugin.config.get("passiveThirst"))
        dataConfig.addDefault("viewType", ViewTypes.BOSSBAR.name)

        saveData(dataConfig, dataFile)
    }

    //Getters (yes, in Kotlin. I know.)

    override fun getPlayerFile(uuid: UUID): File {
        val dataFolder = File(plugin.dataFolder, "data")
        if (!dataFolder.exists()) dataFolder.mkdirs() //Create the data folder if not alr existing.

        val dataFile = File(dataFolder, "$uuid.yml")

        if (!dataFile.exists()) dataFile.createNewFile()

        return dataFile
    }

    override fun getPlayer(uuid: UUID): YamlConfiguration {

        val dataFile = getPlayerFile(uuid)

        val dataConfig = YamlConfiguration.loadConfiguration(dataFile)
        addDefaults(dataFile, dataConfig)

        return dataConfig
    }


    override fun saveData(dataConfig: YamlConfiguration, dataFile: File) {
        dataConfig.options().copyDefaults(true)
        dataConfig.options().copyHeader(true)
        dataConfig.save(dataFile)
    }

}

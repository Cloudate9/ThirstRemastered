package io.github.awesomemoder316.thirstremastered.data

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

interface IPlayerDataConfig {

    /**
     * Gets the .yml file containing the appropriate player's data.
     * @param uuid The player's UUID
     * @return The .yml file containing the player's data.
     */
    fun getPlayerFile(uuid: UUID): File

    /**
     * Gets the .yml file in yaml configuration containing the appropriate player's data.
     * @param uuid The player's UUID
     * @return The yaml configuration of the player's data.
     */
    fun getPlayer(uuid: UUID): YamlConfiguration

    /**
     * Saves the .yml file of a player after changes. If not called, the changes will not be saved to disk.
     * @param dataConfig The yaml configuration of the .yml file
     * @param dataFile The .yml file containing player data.
     */
    fun saveData(dataConfig: YamlConfiguration, dataFile: File)
}

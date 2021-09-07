package io.github.awesomemoder316.thirstremastered.data

import java.util.*
import kotlin.collections.HashMap

interface IPlayerDataManager {
    /**
     * Get the PlayerData of all online players.
     */
    val onlinePlayers: HashMap<UUID, IPlayerData>

    /**
     * Start managing a player's thirst. This also calls updateThirst()
     * @param uuid The player's UUID
     */
    fun addPlayer(uuid: UUID)

    /**
     * Stop managing a player's thirst. This saves the data to disk, then stops managing the player.
     * @param uuid The player's UUID
     */
    fun removePlayer(uuid: UUID)

    /**
     * Alter the thirst level of a player. Use this instead of direct access as this will also update the view.
     * @param uuid The player's UUID
     * @param change The amount of thirst changed. Can be both positive and negative.
     */
    fun updateThirst(uuid: UUID, change: Int)
}

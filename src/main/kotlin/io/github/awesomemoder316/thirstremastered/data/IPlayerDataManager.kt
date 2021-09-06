package io.github.awesomemoder316.thirstremastered.data

import net.kyori.adventure.bossbar.BossBar
import java.util.*
import kotlin.collections.HashMap

interface IPlayerDataManager {
    /**
     * Get the PlayerData of all online players.
     */
    val onlinePlayers: HashMap<UUID, IPlayerData>

    /**
     * Add a player that has just come online. Add a bossBar if the player uses it.
     * @param uuid The player's UUID
     */
    fun addPlayer(uuid: UUID)

    /**
     * Remove a player that just logged off. This saves the data, then removes the player that logged off.
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

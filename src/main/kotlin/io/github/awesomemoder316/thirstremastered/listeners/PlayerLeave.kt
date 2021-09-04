package io.github.awesomemoder316.thirstremastered.listeners

import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeave(private val iPlayerDataManager: IPlayerDataManager): Listener {

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        val uuid = e.player.uniqueId

        iPlayerDataManager.onlinePlayers[uuid]!!.passiveThirstTaskId?.let { Bukkit.getScheduler().cancelTask(it) }

        iPlayerDataManager.removePlayer(uuid)
    }
}
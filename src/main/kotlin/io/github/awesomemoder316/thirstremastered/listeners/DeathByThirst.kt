package io.github.awesomemoder316.thirstremastered.listeners

import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class DeathByThirst(private val iPlayerDataManager: IPlayerDataManager,
                    private val plugin: JavaPlugin): Listener {

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        if (e.deathMessage == "${e.entity.displayName} died")
            e.deathMessage = "${e.entity.displayName} has died of thirst."
        //TODO(Replace hard coded message)
        iPlayerDataManager.removePlayer(e.entity.uniqueId) //Stop managing a dead player.
    }

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        if (e.player.hasPermission("thirstremastered.nothirst")) return
        when (e.player.gameMode) {
            GameMode.ADVENTURE, GameMode.SURVIVAL -> {

                object : BukkitRunnable() {
                    override fun run() {

                        iPlayerDataManager.addPlayer(e.player.uniqueId)

                        //Always restore thirst on respawn, like how hunger is restored on respawn.
                        //Doesn't matter if death by thirst.
                        iPlayerDataManager.updateThirst(
                            e.player.uniqueId,
                            20 - iPlayerDataManager.onlinePlayers[e.player.uniqueId]!!.thirstLevel!!
                        )
                    }
                }.runTaskLater(plugin, 20) //Gives time for the scoreboard to load. Will fail if no delay.

            }
            else -> return
        }
    }
}
package io.github.awesomemoder316.thirstremastered.listeners


import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import io.github.awesomemoder316.thirstremastered.data.ViewTypes
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class StartStopPassiveThirst(private val iPlayerDataManager: IPlayerDataManager,
                             private val plugin: JavaPlugin,
                             private val adventure: BukkitAudiences): Listener {

    //TODO(start and stop thirst on gamemode change. Do not show thirst and do thirst for creative or specator.)

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {

        val uuid = e.player.uniqueId

        iPlayerDataManager.addPlayer(uuid)

        when (iPlayerDataManager.onlinePlayers[uuid]!!.viewTypes) {
            ViewTypes.ABOVEEXPBAR -> {}
            ViewTypes.SCOREBOARD -> {}
            //Else default to Boss bar
            else -> {
                val name = Component.text("Thirst level")
                val bossBar = BossBar.bossBar(name, 1f, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10)

                val aPlayer = adventure.player(uuid)
                aPlayer.showBossBar(bossBar)

                iPlayerDataManager.onlinePlayers[uuid]?.bossBar = bossBar //Set the boss bar. Previously null.
            }
        }
        updatePassiveThirstTime(uuid)
        startPassiveThirst(uuid)
    }

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        val uuid = e.player.uniqueId

        iPlayerDataManager.onlinePlayers[uuid]!!.passiveThirstTaskId?.let { Bukkit.getScheduler().cancelTask(it) }

        iPlayerDataManager.removePlayer(uuid)
    }

    private fun startPassiveThirst(uuid: UUID) {

        val passiveThirst =
        object: BukkitRunnable() {
            override fun run() {

                val playerData = iPlayerDataManager.onlinePlayers[uuid]!!

                playerData.passiveThirstTaskId = null
                playerData.ticksTillPassiveThirstDrop = plugin.config.getDouble("passiveThirst")

               Bukkit.getPlayer(uuid) ?: return //Shouldn't even happen, cause cancelled on Player leave.
                iPlayerDataManager.updateThirst(uuid, -5)

               if (playerData.thirstLevel!! <= 0.0) return //Player should not have negative thirst.

                updatePassiveThirstTime(uuid)
                startPassiveThirst(uuid)
            }

        }.runTaskLater(plugin, iPlayerDataManager.onlinePlayers[uuid]!!.ticksTillPassiveThirstDrop!!.toLong())

        iPlayerDataManager.onlinePlayers[uuid]!!.passiveThirstTaskId = passiveThirst.taskId
        //Stored so that it is possible to cancel.

    }

    private fun updatePassiveThirstTime(uuid: UUID) {

        object: BukkitRunnable() {
            override fun run() {
                Bukkit.getPlayer(uuid) ?: return

                val playerData = iPlayerDataManager.onlinePlayers[uuid]

                playerData!!.ticksTillPassiveThirstDrop =
                    playerData.ticksTillPassiveThirstDrop!!.minus(
                    20
                )

                if (playerData.ticksTillPassiveThirstDrop!! > 20)
                    updatePassiveThirstTime(uuid) //If less, startPassiveThirst will restart this task again.
                //This is so that the timers don't get de-synced over time.

                println(playerData.ticksTillPassiveThirstDrop)
            }
        }.runTaskLater(plugin, 20)
    }
}
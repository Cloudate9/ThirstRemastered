package io.github.awesomemoder316.thirstremastered.effects

import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.random.Random

object ThirstEffects {

    fun dealDamage(uuid: UUID, iPlayerDataManager: IPlayerDataManager, plugin: JavaPlugin) {
        val player = Bukkit.getPlayer(uuid) ?: return //Player is offline, stop giving effect.

        object: BukkitRunnable() {
            override fun run() {

                val playerData = iPlayerDataManager.onlinePlayers[uuid] ?: return //Player should no longer be managed.

                if (playerData.thirstLevel > 0) { //Player is no longer dehydrated.
                    return
                }
                if (player.world.difficulty == Difficulty.PEACEFUL) return //Don't do stuff on peaceful.
                player.damage(1.0)

                dealDamage(uuid, iPlayerDataManager, plugin)
            }
        }.runTaskLater(plugin,

        when (player.world.difficulty) {
            Difficulty.EASY -> 40
            Difficulty.NORMAL -> 20
            Difficulty.HARD -> 10
            Difficulty.PEACEFUL -> 0 //Peaceful should be cancelled anyway.
        }

        )
    }

    fun startNausea(uuid: UUID, iPlayerDataManager: IPlayerDataManager, plugin: JavaPlugin) {

        object: BukkitRunnable() {
            override fun run() {

                val player = Bukkit.getPlayer(uuid) ?: return //Player is offline, stop giving effect.

                val playerData = iPlayerDataManager.onlinePlayers[uuid] ?: return //Player should no longer be managed.

                if (playerData.thirstLevel > 6 || //Player is no longer thirsty enough.
                   player.world.difficulty == Difficulty.PEACEFUL) { //Don't do stuff on peaceful.
                    playerData.isThirstNauseaSlowness = false
                    return
                }

                playerData.isThirstNauseaSlowness = true //So that the manager will not call another of this method.

                player.addPotionEffect(PotionEffect(
                    PotionEffectType.CONFUSION,
                    Random.nextInt(40, 200),
                    0,
                    false,
                    false,
                    false
                ))

                startNausea(uuid, iPlayerDataManager, plugin)
            }
        }.runTaskLater(plugin, Random.nextLong(260, 3600))
    }

    fun startSlowness(uuid: UUID, iPlayerDataManager: IPlayerDataManager, plugin: JavaPlugin) {
        object: BukkitRunnable() {
            override fun run() {

                val player = Bukkit.getPlayer(uuid) ?: return //Player is offline, stop giving effect.

                val playerData = iPlayerDataManager.onlinePlayers[uuid] ?: return //Player should no longer be managed.

                if (playerData.thirstLevel > 6 || //Player is no longer thirsty enough.
                    player.world.difficulty == Difficulty.PEACEFUL) { //Don't do stuff on peaceful.
                    playerData.isThirstNauseaSlowness = false
                    return
                }

                playerData.isThirstNauseaSlowness = true //So that the manager will not call another of this method.

                player.addPotionEffect(PotionEffect(
                    PotionEffectType.SLOW,
                    41,
                    1,
                    false,
                    false,
                    false
                ))

                startSlowness(uuid, iPlayerDataManager, plugin)
            }
        }.runTaskLater(plugin, 40)
    }
}
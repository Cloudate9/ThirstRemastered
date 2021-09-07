package io.github.awesomemoder316.thirstremastered.listeners


import dev.jcsoftware.jscoreboards.JPerPlayerMethodBasedScoreboard
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard
import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class StartStopThirstManagement(private val iPlayerDataManager: IPlayerDataManager): Listener {

    //Remove player from list when in creative/spectator, as they should be immune from thirst.

    @EventHandler
    fun gamemodeAdventureSurvival(e: PlayerGameModeChangeEvent) {
        if (e.player.hasPermission("thirstremastered.nothirst")) return
        when (e.newGameMode) {
            GameMode.ADVENTURE, GameMode.SURVIVAL -> iPlayerDataManager.addPlayer(e.player.uniqueId)
            else -> return
        }
    }

    @EventHandler
    fun gamemodeCreativeSpectator(e: PlayerGameModeChangeEvent) {
        if (e.player.hasPermission("thirstremastered.nothirst")) return
        when (e.newGameMode) {
            GameMode.CREATIVE, GameMode.SPECTATOR -> iPlayerDataManager.removePlayer(e.player.uniqueId)
            else -> return
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (e.player.hasPermission("thirstremastered.nothirst")) return
        when (e.player.gameMode) {
            GameMode.ADVENTURE, GameMode.SURVIVAL -> iPlayerDataManager.addPlayer(e.player.uniqueId)
            else -> return
        }
    }

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        if (e.player.hasPermission("thirstremastered.nothirst")) return
        iPlayerDataManager.removePlayer(e.player.uniqueId)
    }
}

package io.github.awesomemoder316.thirstremastered.listeners


import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.event.server.ServerCommandEvent

class StartStopThirstManagement(private val iPlayerDataManager: IPlayerDataManager): Listener {

    //Remove player from list when in creative/spectator, as they should be immune from thirst.

    @EventHandler
    fun playerChangeDifficulty(e: PlayerCommandPreprocessEvent) {
        if (!e.message.startsWith("/difficulty")) return
        val splitCommand = e.message.split(" ")

        if (splitCommand.size < 2) return //No args

        if (splitCommand[1].lowercase() != "peaceful") //Check for not peaceful so that custom difficulties can be added.

            for (player in e.player.world.players)
                iPlayerDataManager.updateThirst(player.uniqueId, 0) //Unregistered players will be filtered out.

    }

    @EventHandler
    fun serverChangeDifficulty(e: ServerCommandEvent) {
        if (!e.command.startsWith("difficulty")) return
        val splitCommand = e.command.split(" ")

        if (splitCommand.size < 2) return //No args

        if (splitCommand[1].lowercase() != "peaceful") //Check for not peaceful so that custom difficulties can be added.
            for (world in Bukkit.getServer().worlds)

                for (player in world.players)
                    iPlayerDataManager.updateThirst(player.uniqueId, 0) //Unregistered players will be filtered out.

    }

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

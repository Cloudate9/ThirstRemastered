package io.github.awesomemoder316.thirstremastered.data

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class PlayerDataManager(private val iPlayerDataConfig: IPlayerDataConfig,
                        private val iPlayerData: IPlayerData,
                        private val plugin: JavaPlugin): IPlayerDataManager {
    
    /*
    IMPORTANT: iPlayerData will have vars, but as they have all no been populated, most of it is null.
    The vars will only contain the correct stuff once called using IPlayerData#create()
     */
    
    
    override val onlinePlayers = HashMap<UUID, IPlayerData>()

    override fun addPlayer(uuid: UUID) {
        val dataConfig = iPlayerDataConfig.getPlayer(uuid)

        //Get all the things to make PlayerData.
        var thirstLevel = dataConfig.getInt("thirstLevel")

        //Ensure that values weren't meddled with.
        if (thirstLevel > 100) thirstLevel = 100
        if (thirstLevel < 0) thirstLevel = 0

        var ticksTillPassiveThirstDrop = dataConfig.getDouble("passiveThirst")

        if (ticksTillPassiveThirstDrop > plugin.config.getDouble("passiveThirst"))
            ticksTillPassiveThirstDrop = plugin.config.getDouble("passiveThirst")


        val viewPreferenceInString = dataConfig.getString("viewType")
        val viewPreference = ViewTypes.valueOf(viewPreferenceInString!!.uppercase())
        //No need to check, cause if value is wrong, will automatically default to BOSSBAR.

        onlinePlayers[uuid] = iPlayerData.create(thirstLevel, ticksTillPassiveThirstDrop, viewPreference, null, null)
    }

    override fun removePlayer(uuid: UUID) {
        val currentPlayerData = onlinePlayers[uuid]!!
        val dataConfig = iPlayerDataConfig.getPlayer(uuid)
        val dataFile = iPlayerDataConfig.getPlayerFile(uuid)

        dataConfig.set("thirstLevel", currentPlayerData.thirstLevel)
        dataConfig.set("passiveThirst", currentPlayerData.ticksTillPassiveThirstDrop)
        dataConfig.set("viewType", currentPlayerData.viewTypes.name)

        //No need to store boss bar. New boss bar is made on join using the thirst level.
        iPlayerDataConfig.saveData(dataConfig, dataFile)

        onlinePlayers.remove(uuid)
    }

    override fun updateThirst(uuid: UUID, change: Int) {
        val currentPlayerData = onlinePlayers[uuid]!!

        //Not null as a proper instance of playerData has been created in IPlayerData#create()
        currentPlayerData.thirstLevel = currentPlayerData.thirstLevel!!.plus(change)


        when (currentPlayerData.viewTypes) {
            ViewTypes.ABOVEEXPBAR -> {}
            ViewTypes.SCOREBOARD -> {}
            //Else default to Boss bar
            else ->
                currentPlayerData.bossBar!!
                    .progress(
                        (currentPlayerData.thirstLevel!!.toFloat() / 100)
                        //This is the new value
                    )
        }
    }

}
package io.github.awesomemoder316.thirstremastered.data

import dev.jcsoftware.jscoreboards.JPerPlayerMethodBasedScoreboard
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.HashMap

class PlayerDataManager(private val iPlayerDataConfig: IPlayerDataConfig,
                        private val iPlayerData: IPlayerData,
                        private val plugin: JavaPlugin,
                        private val playerScoreboard: JPerPlayerMethodBasedScoreboard,
                        private val adventure: BukkitAudiences
): IPlayerDataManager {
    
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
        if (thirstLevel > 20) thirstLevel = 20
        if (thirstLevel < 0) thirstLevel = 0

        var ticksTillPassiveThirstDrop = dataConfig.getDouble("passiveThirst")

        if (ticksTillPassiveThirstDrop > plugin.config.getDouble("passiveThirst"))
            ticksTillPassiveThirstDrop = plugin.config.getDouble("passiveThirst")


        val viewPreferenceInString = dataConfig.getString("viewType")
        val viewPreference = ViewTypes.valueOf(viewPreferenceInString!!.uppercase())
        //No need to check, cause if value is wrong, will automatically default to BOSSBAR.

        onlinePlayers[uuid] = iPlayerData.create(thirstLevel, ticksTillPassiveThirstDrop, viewPreference, null, null)

        val playerData = onlinePlayers[uuid]
        val player = Bukkit.getPlayer(uuid)

        when (playerData!!.viewTypes) {
            ViewTypes.ABOVEEXPBAR -> {}
            ViewTypes.SCOREBOARD -> {

                //In the scoreboard, the thirst left is displayed as "#", while the thirst taken is displayed as "|"

                val visualisedThirst = StringBuilder().append("&3") //Blue colour code for thirst left.

                for (x in 1..thirstLevel) visualisedThirst.append("#")

                visualisedThirst.append("&c")

                for (x in 1..(20 - thirstLevel)) visualisedThirst.append("|")

                playerScoreboard.addPlayer(player)
                playerScoreboard.setTitle(player,"Thirst level")
                playerScoreboard.setLines(player,
                    "",
                    "--------------------",
                    visualisedThirst.toString(),
                    "Thirst left: &3 $thirstLevel",
                    "--------------------",
                    "")

                playerData.scoreboard = playerScoreboard

            }
            //Else default to Boss bar
            else -> {
                val name = Component.text("Thirst level")
                val bossBar = BossBar.bossBar(name, 1f, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10)

                val aPlayer = adventure.player(uuid)
                aPlayer.showBossBar(bossBar)

                playerData.bossBar = bossBar //Set the boss bar. Previously null.
            }
        }
        if (playerData.thirstLevel!! > 0) {
            updatePassiveThirstTime(uuid)
            startPassiveThirst(uuid)
        }
    }

    override fun removePlayer(uuid: UUID) {
        val playerData = onlinePlayers[uuid]!!
        val dataConfig = iPlayerDataConfig.getPlayer(uuid)
        val dataFile = iPlayerDataConfig.getPlayerFile(uuid)

        playerData.passiveThirstTaskId?.let { Bukkit.getScheduler().cancelTask(it) }

        dataConfig.set("thirstLevel", playerData.thirstLevel)
        dataConfig.set("passiveThirst", playerData.ticksTillPassiveThirstDrop)
        dataConfig.set("viewType", playerData.viewTypes.name)

        //No need to store boss bar. New boss bar is made on join using the thirst level.
        iPlayerDataConfig.saveData(dataConfig, dataFile)

        onlinePlayers.remove(uuid)
    }

    private fun startPassiveThirst(uuid: UUID) {

        val passiveThirst =
            object: BukkitRunnable() {
                override fun run() {

                    val playerData = onlinePlayers[uuid]!!

                    playerData.passiveThirstTaskId = null
                    playerData.ticksTillPassiveThirstDrop = plugin.config.getDouble("passiveThirst")

                    Bukkit.getPlayer(uuid) ?: return //Shouldn't even happen, cause cancelled on Player leave.
                    updateThirst(uuid, -1)

                    if (playerData.thirstLevel!! <= 0.0) return //Player should not have negative thirst.

                    updatePassiveThirstTime(uuid)
                    startPassiveThirst(uuid)
                }

            }.runTaskLater(plugin, onlinePlayers[uuid]!!.ticksTillPassiveThirstDrop!!.toLong())

        onlinePlayers[uuid]!!.passiveThirstTaskId = passiveThirst.taskId
        //Stored so that it is possible to cancel.

    }

    private fun updatePassiveThirstTime(uuid: UUID) {

        object: BukkitRunnable() {
            override fun run() {
                Bukkit.getPlayer(uuid) ?: return

                val playerData = onlinePlayers[uuid]

                playerData!!.ticksTillPassiveThirstDrop =
                    playerData.ticksTillPassiveThirstDrop!!.minus(
                        20
                    )

                if (playerData.ticksTillPassiveThirstDrop!! > 20)
                    updatePassiveThirstTime(uuid) //If less, startPassiveThirst will restart this task again.
                //This is so that the timers don't get de-synced over time.

            }
        }.runTaskLater(plugin, 20)
    }

    override fun updateThirst(uuid: UUID, change: Int) {
        val playerData = onlinePlayers[uuid]!!

        //Not null as a proper instance of playerData has been created in IPlayerData#create()
        playerData.thirstLevel = playerData.thirstLevel!!.plus(change)


        when (playerData.viewTypes) {
            ViewTypes.ABOVEEXPBAR -> {}
            ViewTypes.SCOREBOARD -> {}
            //Else default to Boss bar
            else ->
                playerData.bossBar!!
                    .progress(
                        (playerData.thirstLevel!!.toFloat() / 20)
                        //This is the new value
                    )
        }
    }

}
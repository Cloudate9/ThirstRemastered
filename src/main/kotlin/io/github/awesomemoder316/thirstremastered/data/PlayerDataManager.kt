package io.github.awesomemoder316.thirstremastered.data

import dev.jcsoftware.jscoreboards.JPerPlayerMethodBasedScoreboard
import io.github.awesomemoder316.thirstremastered.effects.ThirstEffects
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

        val viewPreference: ViewTypes = try {
            ViewTypes.valueOf(viewPreferenceInString!!.uppercase()) //In case user puts wrong value
        } catch (ex: IllegalArgumentException) {
            ViewTypes.BOSSBAR
        }

        onlinePlayers[uuid] = iPlayerData.create(thirstLevel, ticksTillPassiveThirstDrop, viewPreference)

        val playerData = onlinePlayers[uuid]
        val player = Bukkit.getPlayer(uuid)

        when (playerData!!.viewTypes) {
            ViewTypes.SCOREBOARD -> {

                playerScoreboard.addPlayer(player)
                playerScoreboard.setTitle(player,"Thirst level")
                //Don't add lines yet. Lines will be added in updateThirst().

            }
            //Else default to Boss bar
            else -> {
                val name = Component.text("Thirst level")

                //Put a placeholder number for bossBar progress. Actual value will be updated in updateThirst().
                val bossBar = BossBar.bossBar(name, 0f, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10)

                val aPlayer = adventure.player(uuid)
                aPlayer.showBossBar(bossBar)

                playerData.bossBar = bossBar //Set the boss bar. Previously null.
            }
        }
        if (playerData.thirstLevel!! > 0) {
            updatePassiveThirstTime(uuid)
            startPassiveThirst(uuid)
        }

        updateThirst(uuid, 0) //So that thirst effects can activate if neccessary.
    }

    override fun removePlayer(uuid: UUID) {
        val playerData = onlinePlayers[uuid] ?: return
        val dataConfig = iPlayerDataConfig.getPlayer(uuid)
        val dataFile = iPlayerDataConfig.getPlayerFile(uuid)

        playerData.passiveThirstTaskId?.let { Bukkit.getScheduler().cancelTask(it) }

        dataConfig.set("thirstLevel", playerData.thirstLevel)
        dataConfig.set("passiveThirst", playerData.ticksTillPassiveThirstDrop)
        dataConfig.set("viewType", playerData.viewTypes.name)

        //No need to store boss bar or scoreboard. New ones are made on add using the thirst level.
        iPlayerDataConfig.saveData(dataConfig, dataFile)

        if (playerData.bossBar == null) {
            //Player used scoreboard
            val player = Bukkit.getPlayer(uuid)
            if (player != null)
                playerScoreboard.removePlayer(player)
            /*
            Don't do anything if null. We just want to stop unexpected scoreboard behaviour
            when removing and reading players from manager while they are online.
             */
        }

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
        val originalThirst = playerData.thirstLevel!!

        //Not null as a proper instance of playerData has been created in IPlayerData#create()
        playerData.thirstLevel = originalThirst + change
        val thirstLevel = playerData.thirstLevel!!

        //Update view.
        when (playerData.viewTypes) {
            ViewTypes.SCOREBOARD -> {

                val visualisedThirst = StringBuilder().append("&3") //Blue colour code for thirst left.

                for (x in 0 until thirstLevel) visualisedThirst.append("#")

                visualisedThirst.append("&c")


                for (x in 0 until 20 - thirstLevel) visualisedThirst.append("-")

                playerScoreboard.setLines(Bukkit.getPlayer(uuid),
                    "",
                    "--------------------",
                    visualisedThirst.toString(),
                    "Thirst left: &3 $thirstLevel",
                    "--------------------",
                    "")

            }
            //Else default to Boss bar
            else ->
                playerData.bossBar!!
                    .progress(
                        (playerData.thirstLevel!!.toFloat() / 20)
                        //This is the new value
                    )
        }

        if (thirstLevel <= 6 && !playerData.isThirstNauseaSlowness) {
            ThirstEffects.startSlowness(uuid, this, plugin)
            ThirstEffects.startNausea(uuid, this, plugin)
        }

        if (thirstLevel == 0) {
            //Damage will auto stop once above 0 thirst.
            ThirstEffects.dealDamage(uuid, this, plugin)
        }

        if (thirstLevel > 6 && playerData.isThirstNauseaSlowness) playerData.isThirstNauseaSlowness = false
        //Nausea and slowness will auto stop.

        if (originalThirst <= 0 && thirstLevel > 0)
            //Means that the passive thirst is no longer running, needs restarting
            startPassiveThirst(uuid)
    }

}
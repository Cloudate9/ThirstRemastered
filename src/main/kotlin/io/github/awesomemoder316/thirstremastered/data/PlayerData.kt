package io.github.awesomemoder316.thirstremastered.data

import net.kyori.adventure.bossbar.BossBar

class PlayerData: IPlayerData {

    override var thirstLevel: Int? = null
    override var ticksTillPassiveThirstDrop: Double? = null
    override var viewTypes: ViewTypes = ViewTypes.BOSSBAR
    override var bossBar: BossBar? = null
    override var passiveThirstTaskId: Int? = null

    /*
    Ticks till passive thirst drop is a double, so that a negative number can be stored.
    A negative number means passive thirst is disabled.

    Boss bar is stored so that the boss bar can be removed or changed, if needed.


    Passive thirst task id is stored so that it can be cancelled if player leaves the game.

    This theoretically makes it possible to avoid the passive thirst effect by
    leaving the game at the right moment. The window is tiny though, and should be negligible.
    */

    override fun create(
        thirstLevel: Int,
        ticksTillPassiveThirstDrop: Double,
        viewTypes: ViewTypes,
        bossBar: BossBar?,
        passiveThirstTaskId: Int?
    ): IPlayerData {

        val newInstance = PlayerData()
        newInstance.thirstLevel = thirstLevel
        newInstance.ticksTillPassiveThirstDrop = ticksTillPassiveThirstDrop
        newInstance.viewTypes = viewTypes
        newInstance.bossBar = bossBar
        newInstance.passiveThirstTaskId = passiveThirstTaskId
        return newInstance
    }
}

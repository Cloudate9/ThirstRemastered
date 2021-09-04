package io.github.awesomemoder316.thirstremastered.data

import net.kyori.adventure.bossbar.BossBar

interface IPlayerData {
    //Most of the time, these will not be null, unless an implementation was created withot the create() method.

    var thirstLevel: Int?
    var ticksTillPassiveThirstDrop: Double?
    var viewTypes: ViewTypes
    var bossBar: BossBar?
    var passiveThirstTaskId: Int?

    /**
     * Create a new implementation of IPlayerData. Without using create(),
     * all the above vars will be null (or ViewTypes.BOSSBAR).
     *
     * Once created using this method, the above methods will be properly populated.
     */
    fun create(thirstLevel: Int,
               ticksTillPassiveThirstDrop: Double,
               viewTypes: ViewTypes,
               bossBar: BossBar?,
               passiveThirstTaskId: Int?): IPlayerData
}

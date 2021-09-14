package io.github.awesomemoder316.thirstremastered.data

import net.kyori.adventure.bossbar.BossBar

interface IPlayerData {
    //Most of the time, these will not be null, unless an implementation was created withot the create() method.

    var thirstLevel: Int
    var ticksTillPassiveThirstDrop: Double
    var viewTypes: ViewTypes
    var isThirstNauseaSlowness: Boolean //If true, it means that player should be below 6 health, and has thirst effects of nausea and slowness.
    var bossBar: BossBar?
    var passiveThirstTaskId: Int?

    /**
     * Create a new implementation of IPlayerData. Without using create(),
     * all the above vars will be null (or ViewTypes.BOSSBAR).
     *
     * Once created using this method, the above methods will be properly populated.
     *
     * While IPlayerData has 7 var, only 3 are required to create an instance as the rest will have a default value.
     */
    fun create(thirstLevel: Int,
               ticksTillPassiveThirstDrop: Double,
               viewTypes: ViewTypes): IPlayerData
}

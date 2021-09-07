package io.github.awesomemoder316.thirstremastered.koin

import io.github.awesomemoder316.thirstremastered.commands.ThirstRemasteredCommand
import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import io.github.awesomemoder316.thirstremastered.listeners.DeathByThirst
import io.github.awesomemoder316.thirstremastered.listeners.StartStopThirstManagement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinComponents: KoinComponent {
    val deathByThirst by inject<DeathByThirst>()
    val iPlayerDataManager by inject<IPlayerDataManager>()
    val startStopThirstManagement by inject<StartStopThirstManagement>()
    val thirstRemasteredCommand by inject<ThirstRemasteredCommand>()
}
package io.github.awesomemoder316.thirstremastered.koin

import io.github.awesomemoder316.thirstremastered.commands.ViewChangeCommand
import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import io.github.awesomemoder316.thirstremastered.listeners.StartStopPassiveThirst
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinComponents: KoinComponent {
    val iPlayerDataManager by inject<IPlayerDataManager>()
    val startStopPassiveThirst by inject<StartStopPassiveThirst>()
    val viewChangeCommand by inject<ViewChangeCommand>()
}
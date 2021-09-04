package io.github.awesomemoder316.thirstremastered.koin

import io.github.awesomemoder316.thirstremastered.ThirstRemastered
import io.github.awesomemoder316.thirstremastered.commands.ViewChangeCommand
import io.github.awesomemoder316.thirstremastered.data.*
import io.github.awesomemoder316.thirstremastered.gui.INormalCommandGui
import io.github.awesomemoder316.thirstremastered.gui.IPrivilegedCommandGui
import io.github.awesomemoder316.thirstremastered.gui.NormalCommandGui
import io.github.awesomemoder316.thirstremastered.gui.PrivilegedCommandGui
import io.github.awesomemoder316.thirstremastered.listeners.StartStopPassiveThirst
import org.koin.dsl.factory
import org.koin.dsl.module

val adventure = ThirstRemastered.adventure
val plugin = ThirstRemastered.INSTANCE

    val commands = module {
        single { ViewChangeCommand(get(), get(), get()) }
    }

    val data = module {
        factory<IPlayerData> { PlayerData() }
        factory<IPlayerDataConfig> { PlayerDataConfig(plugin) }
        single<IPlayerDataManager> { PlayerDataManager(get(), get(), plugin) }
        factory<ViewTypes>()
    }

    val gui = module {
        factory<INormalCommandGui> { NormalCommandGui() }
        factory<IPrivilegedCommandGui> { PrivilegedCommandGui() }
    }

    val listeners = module {
        factory { StartStopPassiveThirst(get(), plugin, adventure) }
    }

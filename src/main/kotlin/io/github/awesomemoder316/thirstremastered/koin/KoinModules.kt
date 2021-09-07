package io.github.awesomemoder316.thirstremastered.koin

import dev.jcsoftware.jscoreboards.JPerPlayerMethodBasedScoreboard
import io.github.awesomemoder316.thirstremastered.ThirstRemastered
import io.github.awesomemoder316.thirstremastered.commands.ThirstRemasteredCommand
import io.github.awesomemoder316.thirstremastered.data.*
import io.github.awesomemoder316.thirstremastered.gui.INormalCommandGui
import io.github.awesomemoder316.thirstremastered.gui.IPrivilegedCommandGui
import io.github.awesomemoder316.thirstremastered.gui.NormalCommandGui
import io.github.awesomemoder316.thirstremastered.gui.PrivilegedCommandGui
import io.github.awesomemoder316.thirstremastered.listeners.StartStopThirstManagement
import io.github.awesomemoder316.thirstremastered.listeners.DeathByThirst
import org.koin.dsl.factory
import org.koin.dsl.module

val adventure = ThirstRemastered.adventure
val plugin = ThirstRemastered.INSTANCE

    val commands = module {
        single { ThirstRemasteredCommand(get(), get(), get()) }
    }

    val data = module {
        factory<IPlayerData> { PlayerData() }
        factory<IPlayerDataConfig> { PlayerDataConfig(plugin) }
        single<IPlayerDataManager> { PlayerDataManager(get(), get(), plugin, get(), adventure) }
        factory<ViewTypes>()
    }

    val gui = module {
        factory<INormalCommandGui> { NormalCommandGui() }
        factory<IPrivilegedCommandGui> { PrivilegedCommandGui() }
    }

    val listeners = module {
        factory { DeathByThirst(get(), plugin) }
        factory { StartStopThirstManagement(get()) }
    }

    val libs = module {
        factory { JPerPlayerMethodBasedScoreboard() }
    }

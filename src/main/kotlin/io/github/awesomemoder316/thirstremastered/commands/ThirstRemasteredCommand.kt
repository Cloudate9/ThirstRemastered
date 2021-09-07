package io.github.awesomemoder316.thirstremastered.commands

import io.github.awesomemoder316.thirstremastered.data.IPlayerData
import io.github.awesomemoder316.thirstremastered.data.IPlayerDataManager
import io.github.awesomemoder316.thirstremastered.gui.INormalCommandGui
import io.github.awesomemoder316.thirstremastered.gui.IPrivilegedCommandGui
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ThirstRemasteredCommand(private val iPlayerDataManager: IPlayerDataManager, private val iNormalCommandGui: INormalCommandGui,
                              private val iPrivilegedCommandGui: IPrivilegedCommandGui
): CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (args.isEmpty()) {
            if (sender !is Player) {
                TODO("Help command")
                return true
            }

            if (sender.hasPermission("thirstremastered.configure")) iNormalCommandGui.open(sender.uniqueId)
            else iPrivilegedCommandGui.open(sender.uniqueId)

            return true
        }

        when (args[0].lowercase()) {
            "resetthirst" -> {
                if (sender is Player && sender.hasPermission("thirstremastered.configure")) {
                    iPlayerDataManager.updateThirst(
                        sender.uniqueId,
                        20 - iPlayerDataManager.onlinePlayers[sender.uniqueId]!!.thirstLevel!!
                    )
                }
            }
        }



        return true
    }


    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {

        return null
    }
}
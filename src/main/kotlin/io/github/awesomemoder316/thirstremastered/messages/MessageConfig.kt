package io.github.awesomemoder316.thirstremastered.messages

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class MessageConfig(private val plugin: JavaPlugin): IMessageConfig {
    override lateinit var config: YamlConfiguration

    init {
        createMessageConfig()
    }

    private fun createMessageConfig() {
        val messageConfigFile = File(plugin.dataFolder, "messages.yml")

        if (!messageConfigFile.exists()) {
            messageConfigFile.parentFile.mkdirs()
            plugin.saveResource("messages.yml", false)
        }

        config = YamlConfiguration()

        config.load(messageConfigFile)
    }
}
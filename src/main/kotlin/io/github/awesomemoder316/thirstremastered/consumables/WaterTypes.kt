package io.github.awesomemoder316.thirstremastered.consumables

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

enum class WaterTypes {
    BOILED,
    CONTAMINATED,
    FRESH,
    SALT,
    SWAMP;

    fun createWaterBottle(plugin: JavaPlugin): ItemStack {
        val waterBottle = ItemStack(Material.POTION)
        val bottleMeta = waterBottle.itemMeta as PotionMeta
        bottleMeta.basePotionData = PotionData(PotionType.WATER, false, false)

        bottleMeta.setDisplayName(getWaterItemstackName(plugin))
        bottleMeta.lore = getWaterLore(plugin)
        bottleMeta.color = getWaterColour(plugin)

        for (effect in getWaterEffect(plugin)) bottleMeta.addCustomEffect(effect, true)

        waterBottle.itemMeta = bottleMeta
        return waterBottle
    }

    //Yes, I use unneccessary vowels.
    private fun getWaterColour(plugin: JavaPlugin): Color {
        val config = plugin.config
        val waterType = this.name

        val rGBValue = config.getString("water.$waterType.colour")!! //Never null, as there is a default value in config.yml.

            .removeSuffix("#") //JIC people put the '#' sign.
            .toInt(16) //Radix of 16 turn Hex into just int.

        return Color.fromRGB(rGBValue)
    }

    fun getWaterEffect(plugin: JavaPlugin): List<PotionEffect> {

        val config = plugin.config
        val waterType = this.name

        val effectSection = config.getConfigurationSection("water.$waterType.effect")
        val effectList = ArrayList<PotionEffect>()

        for (effectEntry in effectSection?.getKeys(false) ?: return effectList) {

            //Effect entry
            val effectEntrySection = effectSection.getConfigurationSection(effectEntry)
            var effectAmplifier = effectEntrySection?.getInt("amplifier")?.minus(1) ?: return effectList
            //Minus 1 cause amplifier 1 gives Effect II.
            var effectDuration = effectEntrySection.getInt("duration")
            val effectType = PotionEffectType.getByName(effectEntrySection.getString("name") ?: return effectList) ?: return effectList

            if (!isPositiveInt(effectAmplifier)) effectAmplifier = 0
            if (!isPositiveInt(effectDuration)) effectDuration = 60

            effectList.add(PotionEffect(effectType, effectDuration, effectAmplifier))

        }

        return effectList
    }

    private fun getWaterItemstackName(plugin: JavaPlugin): String {
        val waterType = this.name
        return ChatColor.translateAlternateColorCodes('&', plugin.config.getString("water.$waterType.name")!!)
    }

    fun getWaterLore(plugin: JavaPlugin): List<String> {

        val config = plugin.config
        val waterType = this.name

        val configLore = config.getList("water.$waterType.lore")
        val lore = ArrayList<String>()
        val waterValue = getWaterValue(plugin)
        lore.add(0, "A bottle of ${waterType.lowercase()} water.")

        if (configLore != null) {
            for (string in configLore) {
                var processedString = ChatColor.translateAlternateColorCodes('&', string.toString())
                processedString = processedString.replace("\$value", waterValue.toString())

                lore.add(processedString)
            }
        }

        return lore
    }

    fun getWaterValue(plugin: JavaPlugin): Int {

        val config = plugin.config
        val logger = plugin.logger
        val waterType = this.name

        var value = config.getInt("water.$waterType.value")

        if (value < -20) {
            value = -20
            logger.warning("water.$waterType.value in this plugin's config.yml is below -20. Defaulting to -20.")
        }

        if (value > 20) {
            value = 20
            logger.warning("water.$waterType.value in this plugin's config.yml is above 20. Defaulting to 20.")
        }

        return value
    }

    private fun isPositiveInt(int: Int): Boolean {
        if (int <= 0) return false
        return true
    }
}
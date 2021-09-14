package io.github.awesomemoder316.thirstremastered.listeners

import io.github.awesomemoder316.thirstremastered.consumables.WaterTypes
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.block.data.Lightable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionType
import org.bukkit.scheduler.BukkitRunnable


class CollectWater(private val plugin: JavaPlugin): Listener {

    @EventHandler
    fun attemptCollect(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return
        //You cannot click water, so just make sure the player right clicks something.
        if (e.material != Material.GLASS_BOTTLE) return

        val player = e.player
        val playerInventory = e.player.inventory
        //Store number of bottles for reference.
        val clickedBlock = e.clickedBlock //This may change from water_cauldron to cauldron, so store before waiting a tick.
        var emptyBottles = 0
        var waterBottles = 0

        for (item in playerInventory.contents) {
            if (item == null) continue
            when (item.type) {

                Material.GLASS_BOTTLE -> emptyBottles += item.amount

                Material.POTION -> {
                    val potionMeta = (item.itemMeta as PotionMeta)
                    if (potionMeta.hasLore()) continue //Only scan for vanilla water bottles
                    if (potionMeta.basePotionData.type == PotionType.WATER) waterBottles += item.amount
                }

                else -> continue
            }
        }

        object : BukkitRunnable() {
            override fun run() {
                var newEmptyBottles = 0
                var newWaterBottles = 0
                var firstWaterBottleStack: ItemStack? = null //This gets the first water bottle itemstack.

                for (item in playerInventory.contents) {
                    if (item == null) continue
                    when (item.type) {

                        Material.GLASS_BOTTLE -> newEmptyBottles += item.amount

                        Material.POTION -> {
                            val potionMeta = (item.itemMeta as PotionMeta)
                            if (potionMeta.hasLore()) continue //Only scan for vanilla water bottles
                            if (potionMeta.basePotionData.type == PotionType.WATER) newWaterBottles += item.amount
                            if (firstWaterBottleStack == null) firstWaterBottleStack = item
                        }

                        else -> continue
                    }
                }

                if (newEmptyBottles + 1 != emptyBottles || newWaterBottles - 1 != waterBottles) return
                //Return means that the player was not filling a bottle

                //Cancelling now will not do anything. Instead, we manually reduce the number of water bottles.

                -- firstWaterBottleStack!!.amount

            /*
            First bottle stack will not be null. As newWaterBottles - 1 needs to == waterBottles,

            If newWaterBottles is 0, which cannot be or check would've failed, waterBottles would have to be
            -1, which is not possible.
             */


                val waterBottle = if (clickedBlock?.type == Material.WATER_CAULDRON) {

                    val blockBelowCauldron = Location(
                        e.clickedBlock!!.world,
                        e.clickedBlock!!.x.toDouble(),
                        e.clickedBlock!!.y - 1.0,
                        e.clickedBlock!!.z.toDouble()
                    ).block


                    if (
                        (blockBelowCauldron.type == Material.CAMPFIRE
                                        || blockBelowCauldron.type == Material.SOUL_CAMPFIRE) &&
                        (blockBelowCauldron.blockData as Lightable).isLit)

                            //Water is boiled.
                            WaterTypes.BOILED.createWaterBottle(plugin)

                     else

                        //Default to contaminated.
                        WaterTypes.CONTAMINATED.createWaterBottle(plugin)

                } else {

                    when (e.clickedBlock?.biome ?: player.location.block.biome) {

                        Biome.RIVER, Biome.FROZEN_RIVER -> {

                            WaterTypes.FRESH.createWaterBottle(plugin)
                        }


                        Biome.BEACH, Biome.SNOWY_BEACH, Biome.OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.DEEP_FROZEN_OCEAN,
                        Biome.DEEP_OCEAN, Biome.FROZEN_OCEAN, Biome.LUKEWARM_OCEAN, Biome.WARM_OCEAN,
                        Biome.DEEP_LUKEWARM_OCEAN, Biome.DEEP_WARM_OCEAN -> {

                            WaterTypes.SALT.createWaterBottle(plugin)
                        }

                        Biome.SWAMP, Biome.SWAMP_HILLS -> {

                            WaterTypes.SWAMP.createWaterBottle(plugin)
                        }

                        else -> {

                            WaterTypes.CONTAMINATED.createWaterBottle(plugin)
                        }
                    }
                }


                if (playerInventory.contains(waterBottle)) {

                    for (item in playerInventory.contents) {
                        if (item == null) continue
                        if (item.isSimilar(waterBottle)) {
                            if (item.amount < plugin.config.getInt("bottleStack")) {
                                ++item.amount
                                return
                            }
                        }
                    }
                }

                //Not added yet
                if (hasAvailableSlot(player)) playerInventory.addItem(waterBottle)
                else player.world.dropItemNaturally(player.location, waterBottle)

                //TODO(Known issues: If there is an itemstack of two bottles, the third item will not join the itemstack, but instead make a new one.)
                //TODO(If inv full, a vanilla bottle will drop to ground. Make where the bottle is the new type of water on pickup?)
            }
        }.runTaskLater(plugin, 1)

    }

    private fun hasAvailableSlot(player: Player): Boolean {

        val inv: Inventory = player.inventory

        for (item in inv.contents) {
            if (item == null || item.type == Material.AIR) { //Has empty slot
                return true
            }
        }

        return false
    }

}













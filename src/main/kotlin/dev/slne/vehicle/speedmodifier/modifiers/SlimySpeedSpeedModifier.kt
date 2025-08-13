package dev.slne.vehicle.speedmodifier.modifiers

import dev.slne.surf.surfapi.core.api.util.objectSetOf
import dev.slne.vehicle.speedmodifier.VehicleSpeedModifier
import org.bukkit.Material

object SlimySpeedSpeedModifier : VehicleSpeedModifier {

    override val applicableMaterials = objectSetOf(
        Material.SLIME_BLOCK,
        Material.HONEY_BLOCK
    )

    override fun modify(speed: Double): Double = speed * 0.5
}
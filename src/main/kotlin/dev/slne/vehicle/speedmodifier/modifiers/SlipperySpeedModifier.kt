package dev.slne.vehicle.speedmodifier.modifiers

import dev.slne.surf.surfapi.core.api.util.objectSetOf
import dev.slne.vehicle.speedmodifier.VehicleSpeedModifier
import org.bukkit.Material

object SlipperySpeedModifier : VehicleSpeedModifier {

    override val applicableMaterials = objectSetOf(
        Material.ICE,
        Material.PACKED_ICE,
        Material.BLUE_ICE
    )

    override fun modify(speed: Double): Double = speed * 2
}
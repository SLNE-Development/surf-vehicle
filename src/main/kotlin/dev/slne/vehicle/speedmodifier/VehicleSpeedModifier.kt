package dev.slne.vehicle.speedmodifier

import dev.slne.vehicle.Vehicle
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Material

interface VehicleSpeedModifier {

    val applicableMaterials: ObjectSet<Material>

    fun modify(speed: Double): Double

}

fun Vehicle.applySpeedModifiers(material: Material, input: Double): Double {
    var input = input

    speedModifiers.forEach { modifier ->
        if (modifier.applicableMaterials.contains(material)) {
            input = modifier.modify(input)
        }
    }

    return input
}
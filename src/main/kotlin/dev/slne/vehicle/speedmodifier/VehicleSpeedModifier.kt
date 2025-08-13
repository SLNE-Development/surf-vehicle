package dev.slne.vehicle.speedmodifier

import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Material

interface VehicleSpeedModifier {

    val applicableMaterials: ObjectSet<Material>

    fun modify(speed: Double): Double

}
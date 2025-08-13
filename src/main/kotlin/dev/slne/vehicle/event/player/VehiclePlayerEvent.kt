package dev.slne.vehicle.event.player

import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.event.VehicleEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class VehiclePlayerEvent(
    vehicle: Vehicle,
    val player: Player,
    async: Boolean = !Bukkit.isPrimaryThread()
) : VehicleEvent(vehicle, async)
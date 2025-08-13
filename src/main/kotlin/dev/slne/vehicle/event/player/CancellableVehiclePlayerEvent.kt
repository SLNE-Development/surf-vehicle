package dev.slne.vehicle.event.player

import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.event.CancellableVehicleEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

open class CancellableVehiclePlayerEvent(
    vehicle: Vehicle,
    val player: Player,
    async: Boolean = !Bukkit.isPrimaryThread()
) : CancellableVehicleEvent(vehicle, async)
package dev.slne.vehicle.event.events

import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.event.player.CancellableVehiclePlayerEvent
import org.bukkit.entity.Player

class VehicleSpawnEvent(
    vehicle: Vehicle,
    player: Player,
) : CancellableVehiclePlayerEvent(vehicle, player)
package dev.slne.vehicle.event.events

import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.event.player.CancellableVehiclePlayerEvent
import dev.slne.vehicle.seat.Seat
import org.bukkit.entity.Player

class VehicleExitEvent(
    vehicle: Vehicle,
    player: Player,
    val seat: Seat,
) : CancellableVehiclePlayerEvent(vehicle, player)
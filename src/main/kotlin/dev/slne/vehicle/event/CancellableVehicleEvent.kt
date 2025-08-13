package dev.slne.vehicle.event

import dev.slne.vehicle.Vehicle
import org.bukkit.Bukkit
import org.bukkit.event.Cancellable

open class CancellableVehicleEvent(
    vehicle: Vehicle,
    async: Boolean = !Bukkit.isPrimaryThread()
) : VehicleEvent(vehicle, async), Cancellable {

    private var cancelled = false

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun isCancelled() = cancelled

}
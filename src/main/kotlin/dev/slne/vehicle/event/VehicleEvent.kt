package dev.slne.vehicle.event

import dev.slne.vehicle.Vehicle
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class VehicleEvent(
    val vehicle: Vehicle,
    async: Boolean = !Bukkit.isPrimaryThread()
) : Event(async) {

    override fun getHandlers() = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList
    }
}
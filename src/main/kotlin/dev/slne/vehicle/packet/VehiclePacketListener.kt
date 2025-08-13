package dev.slne.vehicle.packet

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.vehicle.packet.listeners.VehicleClickListener
import dev.slne.vehicle.packet.listeners.VehicleInputListener

object VehiclePacketListener : PacketListener {

    private val listeners = mutableObjectListOf(
        VehicleClickListener,
        VehicleInputListener
    )

    fun register() {
        PacketEvents.getAPI().eventManager.registerListener(
            this,
            PacketListenerPriority.NORMAL
        )
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        listeners.forEach {
            it.handle(event)
        }
    }
}
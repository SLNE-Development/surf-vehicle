package dev.slne.vehicle.packet

import com.github.retrooper.packetevents.event.PacketReceiveEvent

fun interface VehicleListener {
    fun handle(event: PacketReceiveEvent)
}
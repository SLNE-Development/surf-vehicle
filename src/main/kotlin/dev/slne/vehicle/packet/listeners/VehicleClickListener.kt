package dev.slne.vehicle.packet.listeners

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.vehicle.VehicleManager
import dev.slne.vehicle.packet.VehicleListener
import dev.slne.vehicle.plugin

object VehicleClickListener : VehicleListener {
    override fun handle(event: PacketReceiveEvent) {
        if (event.packetType == PacketType.Play.Client.INTERACT_ENTITY) {
            val packet = WrapperPlayClientInteractEntity(event)
            val entityId = packet.entityId

            val vehicle = VehicleManager.getVehicleById(entityId)
                ?: VehicleManager.getVehicleBySeat(entityId)
                ?: return

            plugin.launch {
                vehicle.occupyFirstSeat(event.getPlayer())
            }
        }
    }
}
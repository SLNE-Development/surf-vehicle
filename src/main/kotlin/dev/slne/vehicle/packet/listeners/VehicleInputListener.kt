package dev.slne.vehicle.packet.listeners

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.vehicle.VehicleManager
import dev.slne.vehicle.packet.VehicleListener
import dev.slne.vehicle.plugin
import org.bukkit.entity.Player

object VehicleInputListener : VehicleListener {
    override fun handle(event: PacketReceiveEvent) {
        if (event.packetType == PacketType.Play.Client.PLAYER_INPUT) {
            val player = event.getPlayer<Player>()
            val packet = WrapperPlayClientPlayerInput(event)
            val vehicle = VehicleManager.getVehicleByPlayer(player) ?: return

            plugin.launch {
                vehicle.receiveInput(player, packet)
            }
        }
    }
}
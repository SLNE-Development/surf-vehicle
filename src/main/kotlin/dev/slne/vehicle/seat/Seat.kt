package dev.slne.vehicle.seat

import dev.slne.vehicle.packet.sendArmorStandMetadataPacket
import dev.slne.vehicle.packet.sendChangePositionPacket
import dev.slne.vehicle.packet.sendSetPassengersPacket
import dev.slne.vehicle.utils.EntityHolder
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

typealias SeatList = ObjectList<Seat>

fun SeatList.despawnAll() {
    forEach { it.despawn() }
}

fun SeatList.spawnAll() {
    forEach { it.spawn() }
}

open class Seat(
    spawnLocation: Location,
    val seatType: SeatType,
    val offset: Vector,
) {

    private val entityHolder = EntityHolder(spawnLocation)
    val entityId get() = entityHolder.entityId

    var occupant: Player? = null
        private set

    var currentLocation: Location
        get() = entityHolder.currentLocation
        set(value) {
            entityHolder.currentLocation = getSeatLocation(value)
        }

    private fun getSeatLocation(location: Location): Location {
        val yawRad = Math.toRadians(location.yaw.toDouble())
        val cos = cos(yawRad)
        val sin = sin(yawRad)

        val rotatedX = offset.x * cos - offset.z * sin
        val rotatedZ = offset.x * sin + offset.z * cos

        return location.clone().add(rotatedX, offset.y, rotatedZ).apply {
            yaw = location.yaw
            pitch = location.pitch
        }
    }

    fun setOccupant(player: Player?) {
        occupant = player

        sendSetPassengersPacket(entityId, player)
    }

    fun removeOccupant() {
        occupant = null

        sendSetPassengersPacket(entityId, null)
    }

    fun vehicleLocationUpdate(location: Location) {
        currentLocation = location

        sendChangePositionPacket(entityId, currentLocation)
        occupant?.teleport(currentLocation)
    }

    fun spawn() {
        entityHolder.spawn {
            sendArmorStandMetadataPacket(entityId)
        }
    }

    fun despawn() {
        entityHolder.despawn()
    }

}
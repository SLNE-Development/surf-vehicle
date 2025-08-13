package dev.slne.vehicle.seat

import dev.slne.vehicle.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import kotlin.properties.Delegates

open class Seat(
    val seatType: SeatType,
    vehicleLocation: Location,
    val offset: Vector,
) {

    var occupant: Player? = null
        private set

    var entityId by Delegates.notNull<Int>()
        private set

    private lateinit var uuid: UUID

    var currentLocation: Location = getSeatLocation(vehicleLocation)
        set(value) {
            field = getSeatLocation(value)
        }

    private fun getSeatLocation(location: Location) =
        location.clone().add(offset).apply {
            yaw = location.yaw
            pitch = location.pitch
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
        val (entityId, uuid) = sendSpawnArmorStandPacket(currentLocation)

        this.entityId = entityId
        this.uuid = uuid

        sendArmorStandMetadataPacket(entityId)
    }

    fun despawn() {
        sendEntityDespawnPacket(entityId)
    }

}
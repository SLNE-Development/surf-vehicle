package dev.slne.vehicle.utils

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3f
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.packet.sendChangePositionPacket
import dev.slne.vehicle.packet.sendTextDisplayMetadataPacket
import dev.slne.vehicle.packet.utils.TextDisplayMetaData
import org.bukkit.Location
import org.bukkit.util.Vector

data class LicensePlate(
    val spawnLocation: Location,
    val plate: SurfComponentBuilder.() -> Unit,
    val offset: Vector,
    val scale: Vector3f = Vector3f(0.5f, 0.5f, 0.5f),
) {
    private val entityHolder = EntityHolder(
        spawnLocation,
        EntityTypes.TEXT_DISPLAY
    )

    private val entityId get() = entityHolder.entityId

    var currentLocation: Location
        get() = entityHolder.currentLocation
        set(value) {
            entityHolder.currentLocation = getLicensePlateLocation(value)
        }

    private fun getLicensePlateLocation(location: Location) =
        location.clone().add(offset).apply {
            yaw = -location.yaw
            pitch = 0f
        }

    private val metadata
        get() = TextDisplayMetaData(
            plate,
            scale = scale,
        )

    fun spawn() {
        entityHolder.spawn {
            sendTextDisplayMetadataPacket(entityId, metadata)
        }
    }

    fun resendMetadata() {
        sendTextDisplayMetadataPacket(entityId, metadata)
    }

    fun despawn() {
        entityHolder.despawn()
    }

    fun vehicleLocationUpdate(location: Location) {
        currentLocation = location

        sendChangePositionPacket(entityId, currentLocation)
    }
}

fun Vehicle.updateLicensePlatePosition() {
    licensePlate.vehicleLocationUpdate(currentLocation)
}
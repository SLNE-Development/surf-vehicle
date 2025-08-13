package dev.slne.vehicle

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.*
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.random
import glm_.or
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

private val api get() = PacketEvents.getAPI()
private val playerManager get() = api.playerManager

fun sendEntityDespawnPacket(entityId: Int) {
    val packet = WrapperPlayServerDestroyEntities(entityId)

    forEachPlayer {
        playerManager.sendPacket(it, packet)
    }
}

fun sendChangePositionPacket(entityId: Int, location: Location) {
    val packet = WrapperPlayServerEntityTeleport(
        entityId,
        SpigotConversionUtil.fromBukkitLocation(location),
        false
    )

    forEachPlayer {
        playerManager.sendPacket(it, packet)
    }
}

fun sendSpawnArmorStandPacket(location: Location): Pair<Int, UUID> {
    val entityId = random.nextInt(10000, Int.MAX_VALUE)
    val uuid = UUID.randomUUID()

    val packet = WrapperPlayServerSpawnEntity(
        entityId,
        uuid,
        EntityTypes.ARMOR_STAND,
        SpigotConversionUtil.fromBukkitLocation(location),
        location.yaw,
        0,
        Vector3d(0.0, 0.0, 0.0)
    )

    forEachPlayer {
        playerManager.sendPacket(it, packet)
    }

    return entityId to uuid
}

fun sendArmorStandMetadataPacket(entityId: Int) {
    val metaData = mutableObjectListOf<EntityData<*>>()

    var flags: Byte = 0
    flags = flags or 0x40 // Glowing

    var armorStandFlags: Byte = 0
    armorStandFlags = armorStandFlags or 0x08 // No base plate

    metaData.add(EntityData(0, EntityDataTypes.BYTE, flags)) // Flags
    metaData.add(EntityData(5, EntityDataTypes.BOOLEAN, true)) // Gravity
    metaData.add(EntityData(15, EntityDataTypes.BYTE, armorStandFlags)) // Armorstand Flags

    val packet = WrapperPlayServerEntityMetadata(
        entityId,
        metaData
    )

    forEachPlayer {
        playerManager.sendPacket(it, packet)
    }
}

//fun sendRotationPacket(entityId: Int, yaw: Float, pitch: Float) {
//    val packet = WrapperPlayServerEntityRotation(
//        entityId,
//        yaw,
//        pitch,
//        false
//    )
//
//    forEachPlayer {
//        playerManager.sendPacket(it, packet)
//    }
//}

fun sendSetPassengersPacket(entityId: Int, passenger: Player?) {
    val passengers = IntArray(1)

    if (passenger != null) {
        passengers[0] = passenger.entityId
    }

    val packet = WrapperPlayServerSetPassengers(
        entityId,
        passengers
    )

    forEachPlayer {
        playerManager.sendPacket(it, packet)
    }
}
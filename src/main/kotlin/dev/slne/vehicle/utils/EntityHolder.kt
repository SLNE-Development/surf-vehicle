package dev.slne.vehicle.utils

import com.github.retrooper.packetevents.protocol.entity.type.EntityType
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import dev.slne.vehicle.packet.sendEntityDespawnPacket
import dev.slne.vehicle.packet.sendSpawnEntityPacket
import org.bukkit.Location
import java.util.*
import kotlin.properties.Delegates

class EntityHolder(
    spawnLocation: Location,
    private val entityType: EntityType = EntityTypes.ARMOR_STAND
) {

    var entityId by Delegates.notNull<Int>()
        private set

    private lateinit var uuid: UUID

    var currentLocation: Location = spawnLocation.clone()

    fun spawn(afterAction: () -> Unit) {
        val (entityId, uuid) = sendSpawnEntityPacket(currentLocation, entityType)

        this.entityId = entityId
        this.uuid = uuid

        afterAction()
    }

    fun despawn() {
        sendEntityDespawnPacket(entityId)
    }
}
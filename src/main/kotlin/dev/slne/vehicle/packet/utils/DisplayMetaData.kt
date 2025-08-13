package dev.slne.vehicle.packet.utils

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.util.Quaternion4f
import com.github.retrooper.packetevents.util.Vector3f
import dev.slne.vehicle.packet.EntityDataList
import dev.slne.vehicle.packet.mutableEntityDataList

open class DisplayMetaData(
    open val interpolationDelay: Int = 0, // 8
    open val transformationInterpolationDuration: Int = 0, // 9
    open val positionRotationInterpolationDuration: Int = 0, // 10
    open val translation: Vector3f = Vector3f(0.0f, 0.0f, 0.0f), // 11
    open val scale: Vector3f = Vector3f(1f, 1f, 1f), // 12
    open val rotationLeft: Quaternion4f = Quaternion4f(0.0f, 0.0f, 0.0f, 1.0f), // 13
    open val rotationRight: Quaternion4f = Quaternion4f(0.0f, 0.0f, 0.0f, 1.0f), // 14
    open val billboardConstraints: DisplayBillboardConstraints = DisplayBillboardConstraints.FIXED, // 15
    open val brightnessOverride: Int = -1, // 16
    open val viewRange: Float = 1f,
    open val shadowRadius: Float = 0.0f, // 17
    open val shadowStrength: Float = 1.0f, // 18
    open val width: Float = 0.0f, // 19
    open val height: Float = 0.0f, // 20
    open val glowColorOverride: Int = -1 // 21
) {
    open fun toEntityData(): EntityDataList {
        val metadata = mutableEntityDataList()

        metadata.add(EntityData(8, EntityDataTypes.INT, interpolationDelay))
        metadata.add(EntityData(9, EntityDataTypes.INT, transformationInterpolationDuration))
        metadata.add(EntityData(10, EntityDataTypes.INT, positionRotationInterpolationDuration))
        metadata.add(EntityData(11, EntityDataTypes.VECTOR3F, translation))
        metadata.add(EntityData(12, EntityDataTypes.VECTOR3F, scale))
        metadata.add(EntityData(13, EntityDataTypes.QUATERNION, rotationLeft))
        metadata.add(EntityData(14, EntityDataTypes.QUATERNION, rotationRight))
        metadata.add(EntityData(15, EntityDataTypes.BYTE, billboardConstraints.value))
        metadata.add(EntityData(16, EntityDataTypes.INT, brightnessOverride))
        metadata.add(EntityData(17, EntityDataTypes.FLOAT, viewRange))
        metadata.add(EntityData(18, EntityDataTypes.FLOAT, shadowRadius))
        metadata.add(EntityData(19, EntityDataTypes.FLOAT, shadowStrength))
        metadata.add(EntityData(20, EntityDataTypes.FLOAT, width))
        metadata.add(EntityData(21, EntityDataTypes.FLOAT, height))
        metadata.add(EntityData(22, EntityDataTypes.INT, glowColorOverride))

        return metadata
    }

    enum class DisplayBillboardConstraints(val value: Byte) {
        FIXED(0),
        VERTICAL(1),
        HORIZONTAL(2),
        CENTER(3),
    }
}
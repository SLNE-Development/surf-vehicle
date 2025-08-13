package dev.slne.vehicle.packet.utils

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.util.Quaternion4f
import com.github.retrooper.packetevents.util.Vector3f
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import glm_.or
import it.unimi.dsi.fastutil.objects.ObjectList

open class TextDisplayMetaData(
    val text: SurfComponentBuilder.() -> Unit, // 23
    val lineWidth: Int = 200, // 24
    val backgroundColor: Int = 0x40000000, // 25
    val textOpacity: Byte = -1, // 26
    val hasShadow: Boolean = false,
    val isSeeThrough: Boolean = false,
    val useDefaultBackgroundColor: Boolean = false,
    val alignment: TextDisplayAlignment = TextDisplayAlignment.CENTER,

    override val interpolationDelay: Int = 0,
    override val transformationInterpolationDuration: Int = 0,
    override val positionRotationInterpolationDuration: Int = 0,
    override val translation: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
    override val scale: Vector3f = Vector3f(1f, 1f, 1f),
    override val rotationLeft: Quaternion4f = Quaternion4f(0.0f, 0.0f, 0.0f, 1.0f),
    override val rotationRight: Quaternion4f = Quaternion4f(0.0f, 0.0f, 0.0f, 1.0f),
    override val billboardConstraints: DisplayBillboardConstraints = DisplayBillboardConstraints.FIXED,
    override val brightnessOverride: Int = -1,
    override val viewRange: Float = 1.0f,
    override val shadowRadius: Float = 0.0f,
    override val shadowStrength: Float = 0.0f,
    override val width: Float = 0.0f,
    override val height: Float = 0.0f,
    override val glowColorOverride: Int = -1
) : DisplayMetaData() {

    override fun toEntityData(): ObjectList<EntityData<*>> {
        val metadata = super.toEntityData()

        var flags: Byte = 0
        if (hasShadow) flags = flags or 0x01
        if (isSeeThrough) flags = flags or 0x02
        if (useDefaultBackgroundColor) flags = flags or 0x04

        flags = flags or ((alignment.value and 0x03) shl 3)

        metadata.add(
            EntityData(
                23,
                EntityDataTypes.ADV_COMPONENT,
                SurfComponentBuilder.builder().apply(text).build()
            )
        )
        metadata.add(EntityData(24, EntityDataTypes.INT, lineWidth))
        metadata.add(EntityData(25, EntityDataTypes.INT, backgroundColor))
        metadata.add(EntityData(26, EntityDataTypes.BYTE, textOpacity))
        metadata.add(EntityData(27, EntityDataTypes.BYTE, flags))

        return metadata
    }

    enum class TextDisplayAlignment(val value: Int) {
        CENTER(0),
        LEFT(1),
        RIGHT(2)
    }
}
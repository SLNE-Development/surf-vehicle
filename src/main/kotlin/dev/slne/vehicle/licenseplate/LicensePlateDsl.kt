package dev.slne.vehicle.licenseplate

import com.github.retrooper.packetevents.util.Vector3f
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.vehicle.VehicleDslMarker
import org.bukkit.Location
import org.bukkit.util.Vector

@VehicleDslMarker
class LicensePlateConfig(
    private val spawnLocation: Location
) {
    var offset: Vector = Vector(0.0, 0.0, 0.0)
    var plate: SurfComponentBuilder.() -> Unit = { text("DUMMY") }
    var scale: Vector3f = Vector3f(0.5f, 0.5f, 0.5f)

    fun build() = LicensePlate(
        spawnLocation = spawnLocation,
        plate = plate,
        offset = offset,
        scale = scale
    )
}
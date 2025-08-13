package dev.slne.vehicle.utils

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.plugin
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.BoundingBox

fun Vehicle.visualizeBoundingBox(
    pointColor: Color = Color.ORANGE,
    pointSize: Float = 0.25f,

    cornerColor: Color = Color.RED,
    cornerPointSize: Float = pointSize * 3,

    stepSize: Double = 0.1
) = VehicleBoundingBoxVisualizer.visualizeBoundingBox(
    currentLocation,
    getCurrentBoundingBox(),
    pointColor,
    pointSize,
    cornerColor,
    cornerPointSize,
    stepSize
)

object VehicleBoundingBoxVisualizer {

    fun visualizeBoundingBox(
        currentLocation: Location,
        boundingBox: BoundingBox,

        pointColor: Color = Color.ORANGE,
        pointSize: Float = 0.25f,

        cornerColor: Color = Color.RED,
        cornerPointSize: Float = pointSize * 3,

        stepSize: Double = 0.1
    ) {
        val min = boundingBox.min
        val max = boundingBox.max

        val w = currentLocation.world

        val corners = listOf(
            Location(w, min.x, min.y, min.z), // 0
            Location(w, min.x, min.y, max.z), // 1
            Location(w, min.x, max.y, min.z), // 2
            Location(w, min.x, max.y, max.z), // 3
            Location(w, max.x, min.y, min.z), // 4
            Location(w, max.x, min.y, max.z), // 5
            Location(w, max.x, max.y, min.z), // 6
            Location(w, max.x, max.y, max.z)  // 7
        )

        val edges = listOf(
            0 to 1,
            0 to 2,
            0 to 4,
            1 to 3,
            1 to 5,
            2 to 3,
            2 to 6,
            3 to 7,
            4 to 5,
            4 to 6,
            5 to 7,
            6 to 7
        )

        plugin.launch {
            for ((i1, i2) in edges) {
                drawLine(corners[i1], corners[i2], stepSize, pointColor, pointSize)
            }

            corners.forEach { drawPoint(it, cornerPointSize, cornerColor) }
        }
    }

    fun drawLine(
        start: Location,
        end: Location,
        stepSize: Double,
        color: Color,
        size: Float
    ) {
        val direction = end.clone().subtract(start).toVector().normalize()
        val distance = start.distance(end)
        val steps = (distance / stepSize).toInt()

        for (i in 0..steps) {
            val point = start.clone().add(direction.clone().multiply(i * stepSize))

            drawPoint(point, size, color)
        }
    }

    fun drawPoint(
        location: Location,
        size: Float,
        color: Color
    ) = plugin.launch(plugin.regionDispatcher(location)) {
        location.world.spawnParticle(
            Particle.DUST,
            location,
            1,
            0.0, 0.0, 0.0, 0.0,
            Particle.DustOptions(color, size),
            false
        )
    }
}
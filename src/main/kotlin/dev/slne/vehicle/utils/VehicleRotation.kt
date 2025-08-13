package dev.slne.vehicle.utils

import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.plugin
import dev.slne.vehicle.speedmodifier.applySpeedModifiers
import kotlinx.coroutines.withContext

open class VehicleRotation(
    val vehicle: Vehicle
) {

    private val lastInput get() = vehicle.lastInput
    private val driver get() = vehicle.driver
    private val currentLocation get() = vehicle.currentLocation
    private val currentSpeed get() = vehicle.currentSpeed

    suspend fun updateRotation() = withContext(plugin.regionDispatcher(currentLocation)) {
        var rotationSpeed = vehicle.rotationSpeed
        val locationBelow = currentLocation.clone().add(0.0, -0.2, 0.0)
        val blockBelow = locationBelow.block

        rotationSpeed = vehicle.applySpeedModifiers(
            blockBelow.type,
            rotationSpeed.toDouble()
        ).toFloat()

        val driver = driver
        if (vehicle.shouldRotateToPlayer && driver != null) {
            rotateVehicle(driver.location.yaw)
        } else {
            rotationSpeed = if (currentSpeed < 0.1) rotationSpeed / 3 else rotationSpeed

            val newYaw = if (lastInput.isLeft) {
                currentLocation.yaw - rotationSpeed
            } else if (lastInput.isRight) {
                currentLocation.yaw + rotationSpeed
            } else {
                currentLocation.yaw
            }
            val clampedYaw = ((newYaw + 180) % 360 + 360) % 360 - 180

            rotateVehicle(clampedYaw)
        }
    }

    private fun rotateVehicle(yaw: Float) {
        currentLocation.yaw = yaw
    }
}
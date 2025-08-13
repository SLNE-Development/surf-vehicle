package dev.slne.vehicle.utils

import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.packet.sendChangePositionPacket
import dev.slne.vehicle.plugin
import dev.slne.vehicle.seat.forEachSeat
import dev.slne.vehicle.speedmodifier.applySpeedModifiers
import kotlinx.coroutines.withContext

class VehiclePosition(
    val vehicle: Vehicle
) {

    private var currentSpeed by vehicle::currentSpeed

    private val currentLocation get() = vehicle.currentLocation
    private val lastInput get() = vehicle.lastInput
    private val maxSpeedBackwards get() = vehicle.maxSpeedBackwards
    private val breakingSpeed get() = vehicle.breakingSpeed
    private val friction get() = vehicle.friction
    private val accelerationSpeed get() = vehicle.accelerationSpeed

    suspend fun updatePosition() = withContext(plugin.regionDispatcher(currentLocation)) {
        val isForward = lastInput.isForward
        val isBackward = lastInput.isBackward
        val noInput = !(isForward && isBackward)

        if (noInput) {
            applyFriction()
        }

        if (isForward) {
            if (currentSpeed < 0) {
                currentSpeed += vehicle.breakingSpeed(vehicle)
            } else {
                val accelerationSpeed = vehicle.accelerationSpeed

                if (!(currentSpeed > (vehicle.maxSpeed - accelerationSpeed))) {
                    currentSpeed = (currentSpeed + accelerationSpeed)
                    vehicle.applyFuelUsage()
                }
            }
        }

        if (isBackward) {
            if (currentSpeed > 0) {
                currentSpeed -= breakingSpeed(vehicle)
            } else {
                if (!(currentSpeed < -maxSpeedBackwards)) {
                    currentSpeed = (currentSpeed - accelerationSpeed)
                    vehicle.applyFuelUsage()
                }
            }
        }

        moveVehicle()
    }

    private fun applyFriction() {
        if (currentSpeed == 0.0) return

        var frictionValue = friction
        val locationBelow = currentLocation.clone().add(0.0, -0.2, 0.0)
        val blockBelow = locationBelow.block

        frictionValue = vehicle.applySpeedModifiers(blockBelow.type, frictionValue)

        currentSpeed -= frictionValue

        if (currentSpeed < 0) {
            currentSpeed = 0.0
        }
    }

    private fun moveVehicle() {
        val direction = currentLocation.clone().direction.setY(0).normalize()
        currentLocation.add(direction.multiply(currentSpeed))

        sendChangePositionPacket(vehicle.entityHolder.entityId, currentLocation)

        vehicle.forEachSeat { seat ->
            seat.vehicleLocationUpdate(currentLocation)
        }

        vehicle.updateLicensePlatePosition()
    }

    private fun pushVehicleUp(amount: Double) {
        currentLocation.add(0.0, amount, 0.0)
    }

    private fun pushVehicleDown(amount: Double) = pushVehicleUp(-amount)
}
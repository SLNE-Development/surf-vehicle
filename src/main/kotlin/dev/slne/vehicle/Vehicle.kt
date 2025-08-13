package dev.slne.vehicle

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import dev.slne.vehicle.seat.Seat
import dev.slne.vehicle.speedmodifier.VehicleSpeedModifier
import dev.slne.vehicle.speedmodifier.modifiers.IceSpeedSpeedModifier
import dev.slne.vehicle.speedmodifier.modifiers.SlimySpeedSpeedModifier
import dev.slne.vehicle.utils.VehicleInput
import dev.slne.vehicle.utils.visualizeBoundingBox
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

abstract class Vehicle(
    val licensePlate: String,
    spawnLocation: Location,
    val seats: ObjectList<Seat>,

    val maxSpeed: Double = 3.0,
    val accelerationSpeed: Double = 0.05,
    val breakingSpeed: Double = 0.05,
    val maxSpeedBackwards: Double = maxSpeed / 2,
    val friction: Double = 0.05,

    val rotationSpeed: Float = 8f,
    val speedModifiers: ObjectSet<VehicleSpeedModifier> = objectSetOf(
        IceSpeedSpeedModifier,
        SlimySpeedSpeedModifier
    ),
    val shouldRotateToPlayer: Boolean = false,

    val usesFuel: Boolean = true,
    val fuelUsage: Double = 0.1,
) {
    var lastInput: VehicleInput = VehicleInput.zero()
        private set

    var currentFuel: Double = 100.0
        private set
    var health = 100.0
        private set
    var currentSpeed: Double = 0.0
        private set

    val driver: Player? get() = seats.firstOrNull { it.seatType.isDriver }?.occupant

    var entityId by Delegates.notNull<Int>()
        private set

    private lateinit var uuid: UUID

    var currentLocation: Location = spawnLocation.clone()
        private set

    open val size = 2.0
    fun getCurrentBoundingBox() = BoundingBox(
        currentLocation.x - size / 2,
        currentLocation.y,
        currentLocation.z - size / 2,
        currentLocation.x + size / 2,
        currentLocation.y + size,
        currentLocation.z + size / 2
    )

    fun receiveInput(player: Player, packet: WrapperPlayClientPlayerInput) {
        val vehicleInput = VehicleInput(
            forward = packet.isForward,
            backward = packet.isBackward,
            left = packet.isLeft,
            right = packet.isRight,
            jump = packet.isJump,
            sneak = packet.isShift,
            sprint = packet.isSprint
        )

        if (vehicleInput.isSneak) {
            removeOccupant(player)
            return
        }

        if (player != driver) return

        lastInput = vehicleInput
    }

    suspend fun tick() {
        updateRotation()
        updatePosition()

        visualizeBoundingBox()
    }

    private suspend fun updateRotation() = withContext(plugin.regionDispatcher(currentLocation)) {
        var rotationSpeed = rotationSpeed
        val locationBelow = currentLocation.clone().add(0.0, -0.2, 0.0)
        val blockBelow = locationBelow.block

        rotationSpeed = applySpeedModifiers(blockBelow.type, rotationSpeed.toDouble()).toFloat()

        val driver = driver
        if (shouldRotateToPlayer && driver != null) {
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

    private suspend fun updatePosition() = withContext(plugin.regionDispatcher(currentLocation)) {
        val isForward = lastInput.isForward
        val isBackward = lastInput.isBackward
        val noInput = !(isForward && isBackward)

        if (noInput) {
            applyFriction()
        }

        if (isForward) {
            if (currentSpeed < 0) {
                currentSpeed += breakingSpeed
            } else {
                if (!(currentSpeed > (maxSpeed - accelerationSpeed))) {
                    currentSpeed = (currentSpeed + accelerationSpeed)
                    applyFuelUsage()
                }
            }
        }

        if (isBackward) {
            if (currentSpeed > 0) {
                currentSpeed -= breakingSpeed
            } else {
                if (!(currentSpeed < -maxSpeedBackwards)) {
                    currentSpeed = (currentSpeed - accelerationSpeed)
                    applyFuelUsage()
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

        frictionValue = applySpeedModifiers(blockBelow.type, frictionValue)

        currentSpeed -= frictionValue

        if (currentSpeed < 0) {
            currentSpeed = 0.0
        }
    }

    private fun applyFuelUsage() {
        if (!usesFuel) return

        currentFuel -= fuelUsage
    }

    private fun moveVehicle() {
        val direction = currentLocation.clone().direction.setY(0).normalize()
        currentLocation.add(direction.multiply(currentSpeed))

        sendChangePositionPacket(entityId, currentLocation)

        forEachSeat { seat ->
            seat.vehicleLocationUpdate(currentLocation)
        }
    }

    private fun pushVehicleUp(amount: Double) {
        currentLocation.add(0.0, amount, 0.0)
    }

    private fun pushVehicleDown(amount: Double) = pushVehicleUp(-amount)

    private fun getLocationOfBlockAhead(): Location {
        val xOffset = 0.7
        val yOffset = 0.4
        val zOffset = 0.7

        val ahead = currentLocation.clone()
            .add(currentLocation.direction.setY(0).normalize().multiply(xOffset))

        val zAhead = ahead.z + zOffset * sin(Math.toRadians(ahead.yaw.toDouble()))
        val xAhead = ahead.x + zOffset * cos(Math.toRadians(ahead.yaw.toDouble()))

        return Location(
            currentLocation.world,
            xAhead,
            currentLocation.y + yOffset,
            zAhead,
            ahead.yaw,
            ahead.pitch
        )
    }

    private fun applySpeedModifiers(material: Material, speed: Double): Double {
        var speed = speed

        speedModifiers.forEach { modifier ->
            if (modifier.applicableMaterials.contains(material)) {
                speed = modifier.modify(speed)
            }
        }

        return speed
    }

    fun occupyFirstSeat(player: Player) {
        val seat = seats.sortedBy { it.seatType.order }
            .firstOrNull { it.occupant == null } ?: run {

            player.sendText {
                appendPrefix()

                error("Alle Sitze sind belegt!")
            }

            return
        }

        seat.setOccupant(player)
    }

    fun removeOccupant(player: Player) {
        val seat = seats.firstOrNull { it.occupant == player } ?: return

        seat.removeOccupant()
    }

    fun spawn() {
        val (entityId, uuid) = sendSpawnArmorStandPacket(currentLocation)

        this.entityId = entityId
        this.uuid = uuid

        sendArmorStandMetadataPacket(entityId)

        seats.forEach { seat ->
            seat.spawn()
        }
    }

    fun despawn() {
        seats.forEach { seat ->
            seat.despawn()
        }

        sendEntityDespawnPacket(entityId)
    }

    private inline fun forEachSeat(action: (Seat) -> Unit) {
        seats.forEach { seat ->
            action(seat)
        }
    }
}

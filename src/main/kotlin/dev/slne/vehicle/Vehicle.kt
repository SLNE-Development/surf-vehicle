package dev.slne.vehicle

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import dev.slne.vehicle.fuel.HasFuel
import dev.slne.vehicle.packet.sendArmorStandMetadataPacket
import dev.slne.vehicle.seat.HasSeats
import dev.slne.vehicle.seat.SeatList
import dev.slne.vehicle.seat.despawnAll
import dev.slne.vehicle.seat.spawnAll
import dev.slne.vehicle.speedmodifier.VehicleSpeedModifier
import dev.slne.vehicle.speedmodifier.modifiers.IceSpeedSpeedModifier
import dev.slne.vehicle.speedmodifier.modifiers.SlimySpeedSpeedModifier
import dev.slne.vehicle.utils.*
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Vehicle(
    val licensePlate: LicensePlate,
    spawnLocation: Location,
    override val seats: SeatList,

    val maxSpeed: Double = 8.0,
    val accelerationSpeed: Double = 0.1,
    val breakingSpeed: (Vehicle) -> Double = { _ -> maxSpeed * 0.1 },
    val maxSpeedBackwards: Double = maxSpeed / 2,
    val friction: Double = 0.05,

    val rotationSpeed: Float = 8f,
    val speedModifiers: ObjectSet<VehicleSpeedModifier> = objectSetOf(
        IceSpeedSpeedModifier,
        SlimySpeedSpeedModifier
    ),
    val shouldRotateToPlayer: Boolean = false,

    override val usesFuel: Boolean = true,
    override val maxFuel: Double = 100.0,
    override var currentFuel: Double = maxFuel,
    override val fuelUsage: () -> Double = { 0.1 },

    override val maxHealth: Double = 100.0,
    override var health: Double = maxHealth,
) : HasFuel, HasHealth, HasSeats {

    val entityHolder: EntityHolder = EntityHolder(spawnLocation.apply { pitch = 0f })
    val entityId get() = entityHolder.entityId

    private val vehiclePosition = VehiclePosition(this)
    private val vehicleRotation = VehicleRotation(this)

    var lastInput: VehicleInput = VehicleInput.zero()
        private set

    var currentSpeed: Double = 0.0
    var currentLocation by entityHolder::currentLocation

    val driver: Player? get() = seats.firstOrNull { it.seatType.isDriver }?.occupant

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
        vehicleRotation.updateRotation()
        vehiclePosition.updatePosition()
    }

    fun spawn() {
        entityHolder.spawn {
            sendArmorStandMetadataPacket(entityHolder.entityId)
        }
        seats.spawnAll()
        licensePlate.spawn()
    }

    fun despawn() {
        licensePlate.despawn()
        seats.despawnAll()
        entityHolder.despawn()
    }
}

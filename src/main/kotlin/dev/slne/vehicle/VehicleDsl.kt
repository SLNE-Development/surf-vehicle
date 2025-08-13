package dev.slne.vehicle

import dev.slne.surf.surfapi.core.api.util.objectSetOf
import dev.slne.vehicle.licenseplate.LicensePlateConfig
import dev.slne.vehicle.seat.SeatsBuilder
import dev.slne.vehicle.speedmodifier.VehicleSpeedModifier
import dev.slne.vehicle.speedmodifier.modifiers.SlipperySpeedModifier
import dev.slne.vehicle.speedmodifier.modifiers.StickySpeedModifier
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location

fun vehicle(spawnLocation: Location, block: VehicleBuilder.() -> Unit) =
    VehicleBuilder(spawnLocation).apply(block).build().spawn()

@VehicleDslMarker
class VehicleBuilder(private val spawnLocation: Location) {
    var maxSpeed: Double = 0.0
    var accelerationSpeed: Double = 0.0
    var friction: Double = 0.0
    var rotationSpeed: Float = 0.0f

    var maxSpeedBackwards: Double = maxSpeed / 2
    var shouldRotateToPlayer: Boolean = false
    var breakingSpeed: (Vehicle) -> Double = { _ -> maxSpeed * 0.1 }
    var speedModifiers: ObjectSet<VehicleSpeedModifier> = objectSetOf(
        SlipperySpeedModifier,
        StickySpeedModifier
    )

    // Fuel
    var usesFuel: Boolean = false
    var maxFuel: Double = 100.0
    var currentFuel: Double = maxFuel
    var fuelUsage: () -> Double = { 0.1 }

    // Health
    var usesHealth: Boolean = false
    var maxHealth: Double = 100.0
    var health: Double = maxHealth

    // Seats
    private val seatsBuilder = SeatsBuilder(spawnLocation)

    fun seats(block: SeatsBuilder.() -> Unit) {
        seatsBuilder.apply(block)
    }

    // LicensePlate
    private var licensePlateConfig: LicensePlateConfig? = null
    fun licensePlate(block: LicensePlateConfig.() -> Unit) {
        licensePlateConfig = LicensePlateConfig(spawnLocation).apply(block)
    }

    fun build(): Vehicle {
        val licensePlate = licensePlateConfig?.build()
            ?: throw IllegalArgumentException("LicensePlate must be configured!")

        return object : Vehicle(
            licensePlate = licensePlate,
            spawnLocation = spawnLocation,
            seats = seatsBuilder.seats
        ) {
            override val maxSpeed = this@VehicleBuilder.maxSpeed
            override val accelerationSpeed = this@VehicleBuilder.accelerationSpeed
            override val maxSpeedBackwards = this@VehicleBuilder.maxSpeedBackwards
            override val friction = this@VehicleBuilder.friction
            override val rotationSpeed = this@VehicleBuilder.rotationSpeed
            override val shouldRotateToPlayer = this@VehicleBuilder.shouldRotateToPlayer
            override val usesFuel = this@VehicleBuilder.usesFuel
            override val maxFuel = this@VehicleBuilder.maxFuel
            override var currentFuel = this@VehicleBuilder.currentFuel
            override val fuelUsage = this@VehicleBuilder.fuelUsage
            override val maxHealth = this@VehicleBuilder.maxHealth
            override var health = this@VehicleBuilder.health
            override val breakingSpeed = this@VehicleBuilder.breakingSpeed
            override val usesHealth = this@VehicleBuilder.usesHealth
            override val speedModifiers = this@VehicleBuilder.speedModifiers
        }
    }
}
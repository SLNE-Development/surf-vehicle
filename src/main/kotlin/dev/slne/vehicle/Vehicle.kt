package dev.slne.vehicle

import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput
import dev.slne.surf.surfapi.bukkit.api.builder.ItemStack
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import dev.slne.vehicle.fuel.HasFuel
import dev.slne.vehicle.packet.sendArmorStandMetadataPacket
import dev.slne.vehicle.packet.sendEquipmentPacket
import dev.slne.vehicle.seat.HasSeats
import dev.slne.vehicle.seat.SeatList
import dev.slne.vehicle.seat.despawnAll
import dev.slne.vehicle.seat.spawnAll
import dev.slne.vehicle.speedmodifier.VehicleSpeedModifier
import dev.slne.vehicle.speedmodifier.modifiers.IceSpeedSpeedModifier
import dev.slne.vehicle.speedmodifier.modifiers.SlimySpeedSpeedModifier
import dev.slne.vehicle.utils.*
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.inventory.ItemStack as BukkitItemStack

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

    private fun getEquipment(): ObjectList<Equipment> {
        val equipment = mutableObjectListOf<Equipment>()

        equipment(EquipmentSlot.HELMET, ItemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                owningPlayer = server.getOfflinePlayer("NotAmmo")
            }
        }).also { equipment.add(it) }

        equipment(
            EquipmentSlot.CHEST_PLATE,
            coloredLeatherArmor(Material.LEATHER_CHESTPLATE)
        ).also { equipment.add(it) }

        equipment(
            EquipmentSlot.LEGGINGS,
            coloredLeatherArmor(Material.LEATHER_LEGGINGS, Color.BLUE)
        ).also { equipment.add(it) }

        equipment(
            EquipmentSlot.BOOTS,
            coloredLeatherArmor(Material.LEATHER_BOOTS, Color.GREEN)
        ).also { equipment.add(it) }

        return equipment
    }

    private fun coloredLeatherArmor(
        material: Material,
        color: Color = Color.RED
    ) = ItemStack(material) {
        meta<LeatherArmorMeta> {
            setColor(color)
        }
    }

    private fun equipment(slot: EquipmentSlot, itemStack: BukkitItemStack) =
        Equipment(slot, packetItemStack(itemStack))

    private fun packetItemStack(itemStack: BukkitItemStack) =
        SpigotConversionUtil.fromBukkitItemStack(itemStack)

    fun spawn() {
        entityHolder.spawn {
            sendArmorStandMetadataPacket(entityHolder.entityId)
            sendEquipmentPacket(entityHolder.entityId, getEquipment())
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

package dev.slne.vehicle

import com.github.retrooper.packetevents.event.PacketListener
import com.github.shynixn.mccoroutine.folia.ticks
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import kotlinx.coroutines.*
import org.bukkit.entity.Player

object VehicleManager : PacketListener {

    private val vehicleMap = mutableObject2ObjectMapOf<Vehicle, CoroutineScope>()
    private val vehicles get() = vehicleMap.keys.freeze()

    fun getVehicleById(entityId: Int) = vehicles.firstOrNull { it.entityId == entityId }
    fun getVehicleByPlayer(player: Player) =
        vehicles.firstOrNull { it.seats.any { seat -> seat.occupant == player } }

    fun getVehicleBySeat(entityId: Int) =
        vehicles.firstOrNull { it.seats.any { seat -> seat.entityId == entityId } }

    fun killAll() {
        vehicles.forEach { it.despawn() }
        vehicles.clear()
    }

    private val log = logger()
    private val mainScope = buildScope("VehicleTicker")

    fun register(vehicle: Vehicle) {
        vehicleMap[vehicle] = buildScope("Vehicle-${vehicle.entityId}", mainScope)
    }

    fun unregister(vehicle: Vehicle) {
        vehicleMap[vehicle]?.cancel()
        vehicleMap.remove(vehicle)
    }

    private fun buildScope(name: String, parentScope: CoroutineScope? = null): CoroutineScope {
        val supervisor = SupervisorJob()
        val name = CoroutineName(name)
        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            log.atSevere().withCause(throwable)
                .log("An error occurred in the vehicle manager on vehicle $name: ${context[CoroutineName]?.name}")
        }

        return if (parentScope != null) {
            CoroutineScope(
                parentScope.coroutineContext + supervisor + name + exceptionHandler
            )
        } else {
            CoroutineScope(
                supervisor + name + exceptionHandler
            )
        }
    }

    suspend fun tick() {
        vehicleMap.toMap().forEach { (vehicle, coroutineScope) ->
            withContext(coroutineScope.coroutineContext) {
                vehicle.tick()
            }
        }
    }

    fun start() = mainScope.launch {
        while (isActive) {
            tick()

            delay(1.ticks)
        }
    }

}
package dev.slne.vehicle

import com.github.retrooper.packetevents.event.PacketListener
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import kotlinx.coroutines.*
import org.bukkit.entity.Player

object VehicleManager : PacketListener {

    val vehicles = mutableObjectSetOf<Vehicle>()

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
    val scope = CoroutineScope(
        SupervisorJob() +
                CoroutineName("VehicleTicker") +
                CoroutineExceptionHandler { context, throwable ->
                    log.atSevere().withCause(throwable)
                        .log("An error occurred in the vehicle ticker: ${context[CoroutineName]?.name}")
                }
    )

    suspend fun tick() {
        vehicles.toObjectSet().forEach { it.tick() }
    }

    fun start() = plugin.launch {
        while (isActive) {
            tick()
            delay(1.ticks)
        }
    }

}
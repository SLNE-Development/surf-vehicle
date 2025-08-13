package dev.slne.vehicle

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.vehicle.packet.VehiclePacketListener
import dev.slne.vehicle.vehicles.TestVehicle
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(SurfVehicle::class.java)

class SurfVehicle : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        VehiclePacketListener.register()
        VehicleManager.start()

        commandAPICommand("vehicle") {
            subcommand("spawn") {
                playerExecutor { player, _ ->
                    plugin.launch {
                        val vehicle = TestVehicle(player.location)
                        vehicle.spawn()

                        VehicleManager.vehicles.add(vehicle)
                    }
                }
            }

            subcommand("killall") {
                playerExecutor { player, args ->
                    plugin.launch {
                        VehicleManager.killAll()
                    }
                }
            }
        }
    }

}
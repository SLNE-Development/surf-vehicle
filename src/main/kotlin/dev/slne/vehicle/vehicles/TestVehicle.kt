package dev.slne.vehicle.vehicles

import dev.slne.surf.surfapi.core.api.util.objectListOf
import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.seat.Seat
import dev.slne.vehicle.seat.SeatType
import org.bukkit.Location
import org.bukkit.util.Vector

class TestVehicle(
    spawnLocation: Location
) : Vehicle(
    licensePlate = "TEST123",
    spawnLocation,
    seats = objectListOf(
        // Front Row
        Seat(SeatType.DRIVER, spawnLocation, Vector(0.5, -1.0, -0.5)),
//        Seat(SeatType.PASSENGER, spawnLocation, Vector(0.5, -1.0, 0.5)),
//
//        // Back Row
//        Seat(SeatType.PASSENGER, spawnLocation, Vector(-0.5, -1.0, 0.5)),
//        Seat(SeatType.PASSENGER, spawnLocation, Vector(-0.5, -1.0, 0.0)),
//        Seat(SeatType.PASSENGER, spawnLocation, Vector(-0.5, -1.0, -0.5)),
    )
)
package dev.slne.vehicle.vehicles

import dev.slne.surf.surfapi.core.api.util.objectListOf
import dev.slne.vehicle.Vehicle
import dev.slne.vehicle.seat.Seat
import dev.slne.vehicle.seat.SeatType
import dev.slne.vehicle.utils.LicensePlate
import org.bukkit.Location
import org.bukkit.util.Vector

class TestVehicle(
    spawnLocation: Location
) : Vehicle(
    licensePlate = LicensePlate(
        spawnLocation,
        plate = { primary("TEST1234") },
        offset = Vector(-0.75, -0.25, 0.0)
    ),
    spawnLocation,
    seats = objectListOf(
        // Front Row
        Seat(spawnLocation, SeatType.DRIVER, Vector(0.5, -1.0, -0.5)),
        Seat(spawnLocation, SeatType.PASSENGER, Vector(0.5, -1.0, 0.5)),

        // Back Row
        Seat(spawnLocation, SeatType.PASSENGER, Vector(-0.5, -1.0, 0.5)),
        Seat(spawnLocation, SeatType.PASSENGER, Vector(-0.5, -1.0, 0.0)),
        Seat(spawnLocation, SeatType.PASSENGER, Vector(-0.5, -1.0, -0.5)),
    )
)
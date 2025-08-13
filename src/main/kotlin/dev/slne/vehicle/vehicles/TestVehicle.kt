package dev.slne.vehicle.vehicles

import dev.slne.vehicle.vehicle
import org.bukkit.Location
import org.bukkit.util.Vector

fun createTestVehicle(spawnLocation: Location) = vehicle(spawnLocation) {
    maxSpeed = 10.0
    accelerationSpeed = 0.5
    friction = 0.05
    rotationSpeed = 10.0f

    licensePlate {
        offset = Vector(0.0, -0.25, -0.75)
        plate = {
            primary("TEST1234")
        }
    }

    seats {
        driverSeat { offset = Vector(0.5, -1.0, 0.5) }

        passengerSeats {
            seat { offset = Vector(-0.5, -1.0, 0.5) } // Front Right Passenger

            seat { offset = Vector(-0.5, -1.0, -0.5) } // Back Right Passenger
            seat { offset = Vector(0.0, -1.0, -0.5) } // Back Middle Passenger
            seat { offset = Vector(0.5, -1.0, -0.5) } // Back Left Passenger
        }
    }
}
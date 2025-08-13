package dev.slne.vehicle.seat

enum class SeatType(val order: Int) {
    DRIVER(0),
    PASSENGER(1);

    val isDriver get() = this == DRIVER
    val isPassenger get() = this == PASSENGER
}
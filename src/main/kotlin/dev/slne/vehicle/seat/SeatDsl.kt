package dev.slne.vehicle.seat

import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.vehicle.VehicleDslMarker
import org.bukkit.Location
import org.bukkit.util.Vector


@VehicleDslMarker
class SeatConfig(
    private val spawnLocation: Location,
    private val seatType: SeatType = SeatType.PASSENGER,
) {
    var offset: Vector = Vector(0.0, 0.0, 0.0)

    fun build() = Seat(
        spawnLocation = spawnLocation,
        seatType = seatType,
        offset = offset
    )
}

@VehicleDslMarker
class SeatsBuilder(
    private val spawnLocation: Location
) {
    private val _seats = mutableObjectListOf<Seat>()
    val seats get() = _seats.freeze()

    fun driverSeat(block: SeatConfig.() -> Unit) {
        require(_seats.firstOrNull { it.seatType.isDriver } == null) {
            "Tried to add more than one driver seat!"
        }

        _seats.add(SeatConfig(spawnLocation, SeatType.DRIVER).apply(block).build())
    }

    fun passengerSeat(block: SeatConfig.() -> Unit) {
        _seats.add(SeatConfig(spawnLocation, SeatType.PASSENGER).apply(block).build())
    }

    fun passengerSeats(block: PassengerSeatsBuilder.() -> Unit) {
        _seats.addAll(PassengerSeatsBuilder(spawnLocation).apply(block).seats)
    }
}

@VehicleDslMarker
class PassengerSeatsBuilder(
    private val spawnLocation: Location,
) {
    private val _seats = mutableListOf<Seat>()
    val seats: List<Seat> get() = _seats

    fun seat(offset: Vector = Vector(0.0, 0.0, 0.0), block: SeatConfig.() -> Unit) {
        _seats.add(
            SeatConfig(spawnLocation, SeatType.PASSENGER).apply {
                this.offset = offset
                block()
            }.build()
        )
    }

    fun seat(block: SeatConfig.() -> Unit) {
        _seats.add(SeatConfig(spawnLocation, SeatType.PASSENGER).apply(block).build())
    }
}
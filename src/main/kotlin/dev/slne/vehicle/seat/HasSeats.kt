package dev.slne.vehicle.seat

import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

interface HasSeats {
    val seats: SeatList

    fun occupyFirstSeat(player: Player) {
        val seat = seats.sortedBy { it.seatType.order }
            .firstOrNull { it.occupant == null } ?: run {

            player.sendText {
                appendPrefix()

                error("Alle Sitze sind belegt!")
            }

            return
        }

        seat.setOccupant(player)
    }

    fun removeOccupant(player: Player) {
        val seat = seats.firstOrNull { it.occupant == player } ?: return

        seat.removeOccupant()
    }

}

inline fun HasSeats.forEachSeat(action: (Seat) -> Unit) {
    seats.forEach { seat ->
        action(seat)
    }
}
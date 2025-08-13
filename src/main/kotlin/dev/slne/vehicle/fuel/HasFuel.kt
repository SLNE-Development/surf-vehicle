package dev.slne.vehicle.fuel

interface HasFuel {

    val usesFuel: Boolean

    var currentFuel: Double

    val maxFuel: Double

    val fuelUsage: () -> Double

    fun hasFuel() = usesFuel && currentFuel > 0

    fun addFuel(amount: Double) {
        if (usesFuel) {
            currentFuel += amount

            if (currentFuel > maxFuel) {
                currentFuel = maxFuel
            }
        }
    }

    fun removeFuel(amount: Double) {
        if (usesFuel) {
            currentFuel -= amount

            if (currentFuel < 0) {
                currentFuel = 0.0
            }
        }
    }

    fun resetFuel() {
        currentFuel = maxFuel
    }

    fun applyFuelUsage() {
        if (usesFuel) {
            removeFuel(fuelUsage())
        }
    }

}
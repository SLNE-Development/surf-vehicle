package dev.slne.vehicle.utils

interface HasHealth {

    val usesHealth: Boolean
    var health: Double
    val maxHealth: Double

    val isAlive get() = health > 0

    fun heal(amount: Double) {
        if (!usesHealth) return

        health = (health + amount).coerceAtMost(maxHealth)
    }

    fun damage(amount: Double) {
        if (!usesHealth) return

        health = (health - amount).coerceAtLeast(0.0)
    }

    fun resetHealth() {
        if (!usesHealth) return
        
        health = maxHealth
    }

}
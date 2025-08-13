package dev.slne.vehicle.utils

interface HasHealth {

    var health: Double
    val maxHealth: Double

    val isAlive get() = health > 0

    fun heal(amount: Double) {
        health = (health + amount).coerceAtMost(maxHealth)
    }

    fun damage(amount: Double) {
        health = (health - amount).coerceAtLeast(0.0)
    }

    fun resetHealth() {
        health = maxHealth
    }

}
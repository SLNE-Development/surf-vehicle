package dev.slne.vehicle.utils

import org.bukkit.Input

data class VehicleInput(
    val forward: Boolean,
    val backward: Boolean,
    val left: Boolean,
    val right: Boolean,
    val jump: Boolean,
    val sneak: Boolean,
    val sprint: Boolean,
) : Input {
    override fun isForward() = forward
    override fun isBackward() = backward
    override fun isLeft() = left
    override fun isRight() = right
    override fun isJump() = jump
    override fun isSneak() = sneak
    override fun isSprint() = sprint

    override fun toString(): String {
        return "VehicleInput(forward=$forward, backward=$backward, left=$left, right=$right, jump=$jump, sneak=$sneak, sprint=$sprint)"
    }

    companion object {
        fun zero() = VehicleInput(
            forward = false,
            backward = false,
            left = false,
            right = false,
            jump = false,
            sneak = false,
            sprint = false
        )
    }
}
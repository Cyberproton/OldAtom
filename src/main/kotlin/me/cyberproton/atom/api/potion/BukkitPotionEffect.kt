package me.cyberproton.atom.api.potion

import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffectType

class BukkitPotionEffect internal constructor(
    override val type: PotionEffectType,
    override val duration: Int,
    override val amplifier: Int,
    override val isAmbient: Boolean,
    override val hasParticles: Boolean,
    override val hasIcon: Boolean
) : IPotionEffect {
    private val startTimestamp = Bukkit.getCurrentTick()
    private val endTimestamp = startTimestamp + duration
    val isInfinity: Boolean = duration == Integer.MAX_VALUE

    override val durationLeft: Int
        get() = maxOf(endTimestamp - Bukkit.getCurrentTick(), 0)

    override fun toString(): String {
        return "BukkitPotionEffect(type=$type, duration=$duration, amplifier=$amplifier, isAmbient=$isAmbient, hasParticles=$hasParticles, hasIcon=$hasIcon, durationLeft=$durationLeft)"
    }
}
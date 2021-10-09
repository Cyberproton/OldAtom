package me.cyberproton.atom.api.potion

import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffectType

class AtomPotionEffect(
    override val type: PotionEffectType,
    override val duration: Int,
    override val amplifier: Int = 0,
    override val isAmbient: Boolean = true,
    override val hasParticles: Boolean = true,
    override val hasIcon: Boolean = true,
) : IPotionEffect {
    private val startTimestamp = Bukkit.getCurrentTick()
    private val endTimestamp = startTimestamp + duration
    val isInfinity: Boolean = duration == Integer.MAX_VALUE

    override val durationLeft: Int
        get() = maxOf(endTimestamp - Bukkit.getCurrentTick(), 0)

    override fun toString(): String {
        return "AtomPotionEffect(type=$type, duration=$duration, amplifier=$amplifier, isAmbient=$isAmbient, hasParticles=$hasParticles, hasIcon=$hasIcon, durationLeft=$durationLeft)"
    }
}
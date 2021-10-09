package me.cyberproton.atom.api.potion

import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

interface IPotionEffect {
    val type: PotionEffectType

    val duration: Int

    val durationLeft: Int

    val amplifier: Int

    val isAmbient: Boolean

    val hasParticles: Boolean

    val hasIcon: Boolean

    companion object {
        @JvmStatic
        fun fromBukkit(bukkitPotionEffect: PotionEffect): BukkitPotionEffect = BukkitPotionEffect(
            bukkitPotionEffect.type,
            bukkitPotionEffect.duration,
            bukkitPotionEffect.amplifier,
            bukkitPotionEffect.isAmbient,
            bukkitPotionEffect.hasParticles(),
            bukkitPotionEffect.hasIcon(),
        )

        @JvmStatic
        fun toBukkit(potionEffect: IPotionEffect): PotionEffect = PotionEffect(
            potionEffect.type,
            potionEffect.duration,
            potionEffect.amplifier,
            potionEffect.isAmbient,
            potionEffect.hasParticles,
            potionEffect.hasIcon,
        )
    }
}
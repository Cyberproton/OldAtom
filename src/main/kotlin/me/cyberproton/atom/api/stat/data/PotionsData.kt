package me.cyberproton.atom.api.stat.data

import org.bukkit.potion.PotionEffectType

data class PotionsData(val potions: List<PotionData> = arrayListOf())

data class PotionData(
    val type: PotionEffectType,
    val duration: Int,
    val amplifier: Int,
    val isAmbient: Boolean = false,
    val hasParticles: Boolean = true,
    val hasIcon: Boolean = true,
)
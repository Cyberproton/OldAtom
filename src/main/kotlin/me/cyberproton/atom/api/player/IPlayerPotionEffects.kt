package me.cyberproton.atom.api.player

import me.cyberproton.atom.api.potion.IPotionEffect
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

interface IPlayerPotionEffects {
    fun addPotionEffect(potionEffect: IPotionEffect)

    fun addPotionEffect(potionEffect: PotionEffect)

    fun removePotionEffect(type: PotionEffectType)

    fun removePotionEffect(potionEffect: IPotionEffect)

    fun removePotionEffect(potionEffect: PotionEffect)

    fun clearPotionEffects()
}
package me.cyberproton.atom.api.stat

import org.bukkit.attribute.Attribute

object Stats {
    @JvmStatic
    val ATTACK_DAMAGE = VanillaStat("ATTACK_DAMAGE", Attribute.GENERIC_ATTACK_DAMAGE)
    @JvmStatic
    val ATTACK_SPEED = VanillaStat("ATTACK_SPEED", Attribute.GENERIC_ATTACK_SPEED)
    @JvmStatic
    val MAX_HEALTH = VanillaStat("MAX_HEALTH", Attribute.GENERIC_MAX_HEALTH)
    @JvmStatic
    val MOVEMENT_SPEED = VanillaStat("MOVEMENT_SPEED", Attribute.GENERIC_MOVEMENT_SPEED)
    @JvmStatic
    val KNOCKBACK_RESISTANCE = VanillaStat("KNOCKBACK_RESISTANCE", Attribute.GENERIC_KNOCKBACK_RESISTANCE)
    @JvmStatic
    val ARMOR = VanillaStat("ARMOR", Attribute.GENERIC_ARMOR)
    @JvmStatic
    val ARMOR_TOUGHNESS = VanillaStat("ARMOR_TOUGHNESS", Attribute.GENERIC_ARMOR_TOUGHNESS)
    @JvmStatic
    val LUCK = VanillaStat("LUCK", Attribute.GENERIC_LUCK)
    @JvmStatic
    val FLYING_SPEED = VanillaStat("FLYING_SPEED", Attribute.GENERIC_FLYING_SPEED)
    @JvmStatic
    val CRITICAL_STRIKE_CHANCE = DoubleStat("CRITICAL_STRIKE_CHANCE")
    @JvmStatic
    val CRITICAL_STRIKE_POWER = DoubleStat("CRITICAL_STRIKE_POWER")
    @JvmStatic
    val ARROW_PHYSICAL_DAMAGE = DoubleStat("ARROW_PHYSICAL_DAMAGE")
    @JvmStatic
    val ARROW_CRITICAL_STRIKE_CHANCE = DoubleStat("ARROW_CRITICAL_STRIKE_CHANCE")
    @JvmStatic
    val ARROW_CRITICAL_STRIKE_POWER = DoubleStat("ARROW_CRITICAL_STRIKE_POWER")
    @JvmStatic
    val ATTACK_DAMAGE_REDUCTION_SWORD = DoubleStat("ATTACK_DAMAGE_REDUCTION_SWORD")
    @JvmStatic
    val ATTACK_DAMAGE_REDUCTION_AXE = DoubleStat("ATTACK_DAMAGE_REDUCTION_AXE")
    @JvmStatic
    val FAST_DIGGING = FastDiggingStat("FAST_DIGGING")
    @JvmStatic
    val PERMISSIONS = PermissionsStat("PERMISSION")
    @JvmStatic
    val POTIONS = PotionsStat("POTIONS")
}
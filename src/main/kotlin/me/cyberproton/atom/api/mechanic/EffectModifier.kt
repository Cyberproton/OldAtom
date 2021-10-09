package me.cyberproton.atom.api.mechanic

import me.cyberproton.atom.api.stat.container.StatModifier

open class EffectModifier(val type: Type, val value: Double, val op: StatModifier.Operation = StatModifier.Operation.ADD) {
    enum class Type {
        PHYSICAL_DAMAGE,
        MAGICAL_DAMAGE,
        ARMOR_PENETRATION,
        MAGIC_RESISTANCE_PENETRATION,
        PHYSICAL_CRITICAL_POWER,
        MAGICAL_CRITICAL_POWER,
        TRUE_DAMAGE,
        ARMOR_DAMAGE,
        PHYSICAL_DAMAGE_REDUCTION,
        MAGICAL_DAMAGE_REDUCTION,
        PHYSICAL_CRITICAL_REDUCTION,
        MAGICAL_CRITICAL_REDUCTION,
        LIFE_STEAL,
        ATTACK_DAMAGE,
        ATTACK_SPEED,
        MOVEMENT_SPEED,
        ARMOR,
        ARMOR_TOUGHNESS,
        HEAL,
        ARMOR_REGENERATION
    }
}
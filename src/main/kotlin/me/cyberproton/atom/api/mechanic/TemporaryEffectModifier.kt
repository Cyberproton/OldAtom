package me.cyberproton.atom.api.mechanic

import me.cyberproton.atom.api.stat.container.StatModifier

class TemporaryEffectModifier(type: EffectModifier.Type, value: Double, val seconds: Double, op: StatModifier.Operation = StatModifier.Operation.ADD) : EffectModifier(type, value, op) {
}
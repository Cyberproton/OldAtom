package me.cyberproton.atom.api.stat.container

import org.bukkit.attribute.AttributeModifier

open class StatModifier(val value: Double, val operation: Operation = Operation.ADD) {
    enum class Operation {
        ADD, MULTIPLY_BASE, MULTIPLY_TOTAL;

        fun toBukkit(): AttributeModifier.Operation {
            return when (this) {
                ADD -> AttributeModifier.Operation.ADD_NUMBER
                MULTIPLY_BASE -> AttributeModifier.Operation.ADD_SCALAR
                MULTIPLY_TOTAL -> AttributeModifier.Operation.MULTIPLY_SCALAR_1
            }
        }

        companion object {
            fun fromBukkit(bukkitOp: AttributeModifier.Operation): Operation {
                return when (bukkitOp) {
                    AttributeModifier.Operation.ADD_NUMBER -> ADD
                    AttributeModifier.Operation.ADD_SCALAR -> MULTIPLY_BASE
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1 -> MULTIPLY_TOTAL
                    else -> throw IllegalArgumentException("Invalid Bukkit Op: " + bukkitOp.name)
                }
            }
        }
    }
}
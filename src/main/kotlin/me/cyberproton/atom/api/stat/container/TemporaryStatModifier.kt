package me.cyberproton.atom.api.stat.container

class TemporaryStatModifier(value: Double, val second: Double, operation: Operation = Operation.ADD) : StatModifier(value, operation) {
}
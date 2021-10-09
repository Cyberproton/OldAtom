package me.cyberproton.atom.api.stat.container

interface StatContainer {
    val id: String

    fun getValue(): Double

    fun getValue(operation: StatModifier.Operation): Double

    fun getModifiers(): Collection<StatModifier>

    fun addModifier(key: String, modifier: StatModifier)

    fun addModifier(key: String, value: Double)

    fun addTemporaryModifier(key: String, modifier: TemporaryStatModifier)

    fun addTemporaryModifier(key: String, modifier: StatModifier, seconds: Double)

    fun addTemporaryModifier(key: String, value: Double, seconds: Double)

    fun removeModifier(key: String)

    fun removeAllModifiers()
}
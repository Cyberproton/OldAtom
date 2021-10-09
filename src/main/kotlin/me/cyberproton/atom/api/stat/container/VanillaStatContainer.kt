package me.cyberproton.atom.api.stat.container

import me.cyberproton.atom.Atom
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class VanillaStatContainer(val player: Player, override val id: String, val attribute: Attribute): StatContainer {
    private val modifiers: MutableMap<String, AttributeModifier> = HashMap()
    private val tasks: MutableMap<String, BukkitTask> = HashMap()
    private var value: Double = 0.0
    private var reset = true
    private val lock: Lock = ReentrantLock()

    override fun addModifier(key: String, modifier: StatModifier) {
        lock.lock()
        try {
            val mod = when (modifier.operation) {
                StatModifier.Operation.ADD -> AttributeModifier(
                    key,
                    modifier.value,
                    AttributeModifier.Operation.ADD_NUMBER
                )
                StatModifier.Operation.MULTIPLY_BASE -> AttributeModifier(
                    key,
                    modifier.value,
                    AttributeModifier.Operation.ADD_SCALAR
                )
                StatModifier.Operation.MULTIPLY_TOTAL -> AttributeModifier(
                    key,
                    modifier.value,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                )
            }
            val instance: AttributeInstance = player.getAttribute(attribute)!!
            removeModifier(key)
            instance.addModifier(mod)
            modifiers[key] = mod
            reset = true
        } finally {
            lock.unlock()
        }
    }

    override fun addModifier(key: String, value: Double) {
        addModifier(key, StatModifier(value, StatModifier.Operation.ADD))
    }

    override fun addTemporaryModifier(key: String, modifier: TemporaryStatModifier) {
        lock.lock()
        try {
            val key2 = "temporary-$key"
            addModifier(key2, modifier)
            val task = object : BukkitRunnable() {
                override fun run() {
                    removeModifier(key2)
                }
            }.runTaskLater(Atom.instance, (modifier.second * 20).toLong())
            tasks[key2] = task
        } finally {
            lock.unlock()
        }
    }

    override fun addTemporaryModifier(key: String, modifier: StatModifier, seconds: Double) {
        addTemporaryModifier(key, TemporaryStatModifier(modifier.value, seconds, modifier.operation))
    }

    override fun addTemporaryModifier(key: String, value: Double, seconds: Double) {
        addTemporaryModifier(key, StatModifier(value, StatModifier.Operation.ADD), seconds)
    }

    override fun removeModifier(key: String) {
        lock.lock()
        try {
            val instance: AttributeInstance = player.getAttribute(attribute)!!
            val modifier = modifiers[key]
            if (modifier != null) {
                instance.removeModifier(modifier)
            }
            val task = tasks[key]
            if (task != null) {
                if (!task.isCancelled) {
                    task.cancel()
                }
                tasks.remove(key)
            }
            modifiers.remove(key)
            reset = true
        } finally {
            lock.unlock()
        }
    }

    override fun removeAllModifiers() {
        lock.lock()
        try {
            modifiers.clear()
            reset = true
        } finally {
            lock.unlock()
        }
    }

    override fun getValue(): Double {
        lock.lock()
        try {
            if (reset) {
                val x = getValue(StatModifier.Operation.ADD)
                var y = x
                y += x * getValue(StatModifier.Operation.MULTIPLY_BASE)
                y *= (1 + getValue(StatModifier.Operation.MULTIPLY_TOTAL))
                reset = false
                value = y
                return y
            }
            return value
        } finally {
            lock.unlock()
        }
    }

    fun getVanillaValue(): Double {
        return player.getAttribute(attribute)?.value ?: 0.0
    }

    override fun getValue(operation: StatModifier.Operation): Double {
        lock.lock()
        try {
            val bukkitOp = operation.toBukkit()
            var d = 0.0
            player.getAttribute(attribute)?.modifiers?.forEach { modifier ->
                if (modifier.operation != bukkitOp) {
                    return@forEach
                }
                d += modifier.amount
            }
            return d
        } finally {
            lock.unlock()
        }
    }

    override fun getModifiers(): Collection<StatModifier> {
        return player
            .getAttribute(attribute)
            ?.modifiers
            ?.map { b ->
                StatModifier(
                    b.amount,
                    StatModifier.Operation.fromBukkit(b.operation)
                )
            }
            ?: listOf()
    }
}
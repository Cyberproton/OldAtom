package me.cyberproton.atom.api.stat.container

import me.cyberproton.atom.Atom
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class AtomStatContainer(override val id: String) : StatContainer {
    private val modifiers: MutableMap<String, StatModifier> = hashMapOf()
    private val tasks: MutableMap<String, BukkitTask> = hashMapOf()
    private var value: Double = 0.0
    private val valueByOperation: MutableMap<StatModifier.Operation, Double> = EnumMap(StatModifier.Operation::class.java)
    private var reset = true
    private val lock = ReentrantLock()

    init {
        for (operation in StatModifier.Operation.values()) {
            valueByOperation[operation] = 0.0
        }
    }

    override fun getValue(): Double {
        lock.lock()
        try {
            if (reset) {
                value = 0.0
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

    override fun getValue(operation: StatModifier.Operation): Double {
        lock.lock()
        try {
            if (reset) {
                valueByOperation.clear()
                var v0 = 0.0
                var v1 = 0.0
                var v2 = 0.0
                for (modifier in modifiers.values) {
                    when (modifier.operation) {
                        StatModifier.Operation.ADD -> v0 += modifier.value
                        StatModifier.Operation.MULTIPLY_BASE -> v1 += modifier.value
                        StatModifier.Operation.MULTIPLY_TOTAL -> v2 += modifier.value
                    }
                }
                for (op in StatModifier.Operation.values()) {
                    when (op) {
                        StatModifier.Operation.ADD -> valueByOperation[op] = v0
                        StatModifier.Operation.MULTIPLY_BASE -> valueByOperation[op] = v1
                        StatModifier.Operation.MULTIPLY_TOTAL -> valueByOperation[op] = v2
                    }
                }
            }
            return valueByOperation[operation]!!
        } finally {
            lock.unlock()
        }
    }

    override fun getModifiers(): Collection<StatModifier> {
        lock.lock()
        try {
            return ArrayList(modifiers.values)
        } finally {
            lock.unlock()
        }
    }

    override fun addModifier(key: String, modifier: StatModifier) {
        lock.lock()
        try {
            removeModifier(key)
            modifiers[key] = modifier
            reset = true
        } finally {
            lock.unlock()
        }
    }

    override fun addModifier(key: String, value: Double) {
        addModifier(key, StatModifier(value))
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
        addTemporaryModifier(key, TemporaryStatModifier(value, seconds))
    }

    override fun removeModifier(key: String) {
        lock.lock()
        try {
            val task = tasks[key]
            if (task != null && !task.isCancelled) {
                task.cancel()
            }
            tasks.remove(key)
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
}
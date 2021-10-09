package me.cyberproton.atom.api.stat

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import me.cyberproton.atom.api.log.Log
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.ConfigurationSection

abstract class Stat(val id: String) {
    val configPath = id.lowercase().replace('_', '-')

    open fun read(section: ConfigurationSection): Any? = null

    open fun merge(data: Any, other: Any): Any? = null
}

data class StatInstance(val stat: Stat, val value: Any)

data class CompositeStat(val stats: MutableMap<String, StatInstance> = hashMapOf()) {
    fun getStats() = stats.values

    fun getStat(id: String) = stats[id.uppercase()]

    fun getStat(stat: Stat) = stats[stat.id]

    fun putStat(statInstance: StatInstance) {
        stats[statInstance.stat.id] = statInstance
    }

    fun clone(): CompositeStat {
        val cs = CompositeStat()
        cs.stats.putAll(stats)
        return cs
    }
}

data class DuplicableCompositeStat(val stats: Multimap<String, StatInstance> = ArrayListMultimap.create()) {
    fun getStats() = stats.values()

    fun getStat(id: String) = stats[id]

    fun getStat(stat: Stat) = stats[stat.id]

    fun putStat(statInstance: StatInstance) {
        stats.put(statInstance.stat.id, statInstance)
    }
}

open class DoubleStat(id: String): Stat(id) {
    override fun read(section: ConfigurationSection): Double? {
        val s = section.getString(configPath)
        Log.i(javaClass.simpleName, "Reading double stat: $configPath : $s")
        if (s.isNullOrBlank()) {
            return null
        }
        if (s.length > 1 && s.last() == '%') {
            val d = s.substring(0, s.length - 1)
            return d.toDoubleOrNull()?.div(100.0)
        }
        return s.toDoubleOrNull()
    }
}

open class IntegerStat(id: String): Stat(id) {
    override fun read(section: ConfigurationSection): Int? {
        if (!section.isInt(configPath)) {
            return null
        }
        return section.getInt(configPath)
    }
}

open class BooleanStat(id: String): Stat(id) {
    override fun read(section: ConfigurationSection): Boolean? {
        if (!section.isBoolean(configPath)) {
            return null
        }
        return section.getBoolean(configPath)
    }
}

open class StringStat(id: String): Stat(id) {
    override fun read(section: ConfigurationSection): String? {
        return section.getString(configPath)
    }
}

class VanillaStat(id: String, val attribute: Attribute): DoubleStat(id)

open class StringListStat(id: String): Stat(id) {
    override fun read(section: ConfigurationSection): List<String>? {
        if (!section.isList(configPath)) {
            return null
        }
        return section.getStringList(configPath)
    }
}
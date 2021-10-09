package me.cyberproton.atom.api.stat

import org.bukkit.configuration.ConfigurationSection

interface IStatManager {
    fun getStats(): Collection<Stat>

    fun getStat(id: String): Stat?

    fun getCompositeStat(section: ConfigurationSection?): CompositeStat {
        section ?: return CompositeStat()
        val stats = hashMapOf<String, StatInstance>()
        section.getKeys(false)
            .mapNotNull { getStat(it) }
            .mapNotNull { it.read(section)?.let { d -> return@mapNotNull Pair(it.id, StatInstance(it, d)) }?:return@mapNotNull null }
            .toMap(stats)
        return CompositeStat(stats)
    }

    fun getDoubleStats(): Collection<DoubleStat> = getStats().filterIsInstance(DoubleStat::class.java)

    fun getIntegerStats(): Collection<IntegerStat> = getStats().filterIsInstance(IntegerStat::class.java)

    fun getBooleanStats(): Collection<BooleanStat> = getStats().filterIsInstance(BooleanStat::class.java)

    fun getStringStats(): Collection<StringStat> = getStats().filterIsInstance(StringStat::class.java)

    fun registerStat(stat: Stat)
}
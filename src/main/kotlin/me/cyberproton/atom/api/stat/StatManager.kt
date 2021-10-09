package me.cyberproton.atom.api.stat

import me.cyberproton.atom.api.log.Log

class StatManager : IStatManager {
    private val stats: MutableMap<String, Stat> = hashMapOf()

    init {
        Log.i(javaClass.simpleName, "Loading default stats")
        for (field in Stats.javaClass.declaredFields) {
            Log.i(javaClass.simpleName, field.name)
            field.isAccessible = true
            try {
                val s = field.get(null)
                if (s !is Stat) {
                    continue
                }
                stats[s.id] = s
            } catch (ex: IllegalAccessException) {
                ex.printStackTrace()
            }
        }
    }

    override fun getStats(): Collection<Stat> = stats.values

    override fun getStat(id: String): Stat? {
        return stats[id.uppercase().replace('-', '_')]
    }

    override fun registerStat(stat: Stat) {
        stats[stat.id] = stat
    }
}
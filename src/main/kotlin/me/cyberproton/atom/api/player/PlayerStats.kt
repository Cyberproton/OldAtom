package me.cyberproton.atom.api.player

import me.cyberproton.atom.api.stat.DoubleStat
import me.cyberproton.atom.api.stat.Stat
import me.cyberproton.atom.api.stat.VanillaStat
import me.cyberproton.atom.api.stat.container.AtomStatContainer
import me.cyberproton.atom.api.stat.container.StatContainer
import me.cyberproton.atom.api.stat.container.VanillaStatContainer
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PlayerStats(override val id: UUID, override val player: IPlayer, override val priority: Int): IPlayerStats, PlayerModule {
    private val containers: MutableMap<String, StatContainer> = ConcurrentHashMap()

    override fun onUpdate() {
        for (i in containers.values) {
            i.removeAllModifiers()
        }

        for (attribute in Attribute.values()) {
            val instance: AttributeInstance = player.bukkitPlayer.getAttribute(attribute) ?: continue
            for (modifier in instance.modifiers) {
                if (modifier.name.startsWith("atombase")) {
                    instance.removeModifier(modifier)
                }
            }
        }
    }

    override fun getValue(stat: Stat): Double {
        val instance: StatContainer = containers[stat.id] ?: return 0.0
        return instance.getValue()
    }

    override fun getInstance(stat: DoubleStat): StatContainer {
        val instance: StatContainer
        return if (stat is VanillaStat) {
            val a = stat.attribute
            if (!containers.containsKey(a.name)) {
                instance = VanillaStatContainer(player.bukkitPlayer, a.name, a)
                containers[a.name] = instance
            }
            containers[a.name]!!
        } else {
            if (!containers.containsKey(stat.id)) {
                instance = AtomStatContainer(stat.id)
                containers[stat.id] = instance
            }
            containers[stat.id]!!
        }
    }
}
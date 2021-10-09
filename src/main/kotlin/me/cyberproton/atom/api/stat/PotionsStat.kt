package me.cyberproton.atom.api.stat

import me.cyberproton.atom.api.extension.getStringListOrString
import me.cyberproton.atom.api.stat.data.PotionData
import me.cyberproton.atom.api.stat.data.PotionsData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.potion.PotionEffectType

open class PotionsStat(id: String) : Stat(id) {
    override fun read(section: ConfigurationSection): PotionsData {
        if (section.isConfigurationSection(configPath)) {
            val config = section.getConfigurationSection(configPath)!!
            val potions = section
                .getConfigurationSection(configPath)!!
                .getKeys(false)
                .mapNotNull {
                    section.getConfigurationSection(it)
                }
                .mapNotNull potionDataMap@ {
                    val typeString = config.getString("type") ?: return@potionDataMap null
                    val type = PotionEffectType.getByName(typeString) ?: return@potionDataMap null
                    val amplifier = config.getInt("amplifier", 0)
                    val duration = config.getInt("duration", Int.MAX_VALUE)
                    val isAmbient = config.getBoolean("is-ambient")
                    val hasParticles = config.getBoolean("has-particles", true)
                    val hasIcon = config.getBoolean("has-icon", true)
                    PotionData(type, duration, amplifier, isAmbient, hasParticles, hasIcon)
                }
            return PotionsData(potions)
        } else {
            val potions = arrayListOf<PotionData>()
            for (potionString in section.getStringListOrString(configPath)) {
                val parts = potionString.split(';')
                if (parts.isEmpty()) {
                    continue
                }
                val type = PotionEffectType.getByName(parts[0]) ?: continue
                val amplifier = if (parts.size > 1) parts[1].toIntOrNull() ?: continue else 0
                val duration = if (parts.size > 2) parts[2].toIntOrNull() ?: continue else Int.MAX_VALUE
                val isAmbient = if (parts.size > 3) parts[3].toBooleanStrictOrNull() ?: continue else false
                val hasParticles = if (parts.size > 4) parts[4].toBooleanStrictOrNull() ?: continue else true
                val hasIcon = if (parts.size > 5) parts[5].toBooleanStrictOrNull() ?: continue else true
                potions.add(PotionData(type, duration, amplifier, isAmbient, hasParticles, hasIcon))
            }
            return PotionsData(potions)
        }
    }
}
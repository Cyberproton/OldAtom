package me.cyberproton.atom.api.stat

import me.cyberproton.atom.api.stat.data.PotionData
import me.cyberproton.atom.api.stat.data.PotionsData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.potion.PotionEffectType

class FastDiggingStat(id: String) : PotionsStat(id) {
    override fun read(section: ConfigurationSection): PotionsData {
        val amp = section.getInt(configPath)
        if (amp < 0) {
            return PotionsData()
        }
        return PotionsData(listOf(PotionData(PotionEffectType.FAST_DIGGING, Int.MAX_VALUE, amp, false, false, false)))
    }
}
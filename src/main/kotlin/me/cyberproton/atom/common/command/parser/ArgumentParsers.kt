package me.cyberproton.atom.common.command.parser

import me.cyberproton.atom.api.command.ArgumentParser
import org.bukkit.potion.PotionEffectType

class PotionEffectTypeParser : ArgumentParser("potion-effect-type") {
    override fun parse(arg: String): PotionEffectType? {
        return PotionEffectType.getByName(arg.uppercase())
    }
}
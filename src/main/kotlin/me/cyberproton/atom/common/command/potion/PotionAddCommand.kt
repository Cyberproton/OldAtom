package me.cyberproton.atom.common.command.potion

import me.cyberproton.atom.api.command.Command
import me.cyberproton.atom.api.command.CommandArgument
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

class PotionAddCommand : Command("potion add <potion@potion-effect-type> (duration@double) (amplifier@int)", playerOnly = true) {
    override fun execute(
        sender: CommandSender,
        args: List<CommandArgument>,
        stringArgs: Array<out String>,
        numberOfArgs: Int
    ) {
        val player = sender as Player
        val type = args[2].parseOrThrow(PotionEffectType::class.java)
        val duration = if (args.size > 3) (args[3].parseOrThrow(Double::class) * 20).toInt() else 100
        val amplifier = if (args.size > 4) args[4].parseOrThrow(Int::class) else 0
        player.addPotionEffect(type.createEffect(duration, amplifier))
    }
}
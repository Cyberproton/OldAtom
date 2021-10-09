package me.cyberproton.atom.common.command.permission

import me.cyberproton.atom.api.command.Command
import me.cyberproton.atom.api.command.CommandArgument
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PermissionSetCommand : Command(
    "permission set <player-name@player> <permission@string> (value@bool)",
    "atom.permission.set"
)
{
    override fun execute(sender: CommandSender, args: List<CommandArgument>, stringArgs: Array<out String>, numberOfArgs: Int) {
        val player = args[2].parseOrThrow(Player::class.java)
        val perm = args[3].arg
        val value = if (args.size > 4) args[4].parseOrThrow(Boolean::class.java) else true

        sender.sendMessage("Permission set: $perm")
    }
}
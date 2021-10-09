package me.cyberproton.atom.common.command.permission

import me.cyberproton.atom.api.command.Command
import me.cyberproton.atom.api.command.CommandArgument
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PermissionCheckCommand : Command(
    "permission check <player-name@player> <permission@string>",
    "atom.permission.check"
)
{
    override fun execute(sender: CommandSender, args: List<CommandArgument>, stringArgs: Array<out String>, numberOfArgs: Int) {
        val player = args[2].parse(Player::class.java) ?: return
        if (player.hasPermission(args[3].arg)) {
            sender.sendMessage("Has permission ${args[3].arg}")
        } else {
            sender.sendMessage("No permission ${args[3].arg}")
        }
    }
}
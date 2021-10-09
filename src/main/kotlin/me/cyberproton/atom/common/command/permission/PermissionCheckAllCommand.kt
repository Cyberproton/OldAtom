package me.cyberproton.atom.common.command.permission

import me.cyberproton.atom.api.command.Command
import me.cyberproton.atom.api.command.CommandArgument
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PermissionCheckAllCommand : Command(
    "permission checkall <player-name@player>",
    "atom.permission.checkall"
)
{
    override fun execute(sender: CommandSender, args: List<CommandArgument>, stringArgs: Array<out String>, numberOfArgs: Int) {
        val player = args[2].parseOrThrow(Player::class.java)
        sender.sendMessage("Permissions: ")
        if (player.effectivePermissions.isEmpty()) {
            sender.sendMessage("- This player doesnt have any permissions")
        } else {
            for (pai in player.effectivePermissions) {
                sender.sendMessage("- plugin=${pai.attachment?.plugin?.name} perm=${pai.permission} value=${pai.value}")
            }
        }
    }
}
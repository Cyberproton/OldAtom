package me.cyberproton.atom.common.command.permission

import me.cyberproton.atom.Atom
import me.cyberproton.atom.api.command.Command
import me.cyberproton.atom.api.command.CommandArgument
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PermissionRemoveCommand : Command(
    "permission remove <player-name@player> <permission@string>",
    "atom.permission.remove"
)
{
    override fun execute(sender: CommandSender, args: List<CommandArgument>, stringArgs: Array<out String>, numberOfArgs: Int) {
        val player = args[2].parseOrThrow(Player::class.java)
        val perm = args[3].arg
        for (pai in player.effectivePermissions) {
            if (pai.attachment == null || pai.attachment!!.plugin.name != Atom.instance.name) {
                continue
            }
            pai.attachment!!.unsetPermission(perm)
            sender.sendMessage("Permission unset: $perm")
            return
        }
        sender.sendMessage("No permission: $perm")
    }
}
package me.cyberproton.atom.common.command.reload

import me.cyberproton.atom.Atom
import me.cyberproton.atom.api.command.Command
import me.cyberproton.atom.api.command.CommandArgument
import org.bukkit.command.CommandSender

class ReloadCommand : Command("reload") {
    override fun execute(
        sender: CommandSender,
        args: List<CommandArgument>,
        stringArgs: Array<out String>,
        numberOfArgs: Int
    ) {
        Atom.instance.onReload()
    }
}
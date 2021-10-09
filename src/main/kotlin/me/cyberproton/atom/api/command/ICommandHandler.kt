package me.cyberproton.atom.api.command

import org.bukkit.command.TabExecutor

interface ICommandHandler : TabExecutor {
    fun registerCommand(command: Command)

    fun registerParser(parser: ArgumentParser)
}
package me.cyberproton.atom.api.command

import me.cyberproton.atom.api.exception.AtomArgumentParserException
import me.cyberproton.atom.api.exception.AtomCommandException
import me.cyberproton.atom.api.extension.colored
import me.cyberproton.atom.api.log.Log
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

open class CommandHandler(private val plugin: JavaPlugin, private val commandMain: String) : ICommandHandler {
    private val commandMainColored: String = "&b$commandMain".colored()
    private val commands: MutableMap<String, MutableList<Command>> = linkedMapOf()
    val argumentParserRegistry: IArgumentParserRegistry = ArgumentParserRegistry()

    init {
        plugin.getCommand(commandMain)?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
        val base = if (args.isNotEmpty()) args[0] else ""
        if (base.isEmpty() || base.equals("help", true)) {
            showHelp(sender)
            return true
        }
        val commands = commands[base]
        if (commands == null || commands.isEmpty()) {
            showHelp(sender)
            return true
        }

        val matchedCommand = arrayListOf<Command>()
        for (cmd in commands) {
            Log.i("Checking ${cmd.syntax}, min = ${cmd.minArguments}, max = ${cmd.maxArguments}, mask = ${cmd.mask}")
            if (args.size < cmd.minArguments || args.size > cmd.maxArguments) {
                continue
            }
            Log.i("Pass 1")
            var passed = true
            for (i in cmd.mask.indices) {
                if (!passed) {
                    break
                }
                if (cmd.mask[i] == '0' && cmd.parameters[i].id != args[i]) {
                    passed = false
                    break
                }
            }
            if (!passed) {
                continue
            }
            Log.i("Pass 2")
            matchedCommand.add(cmd)
        }

        if (matchedCommand.isEmpty()) {
            showHelp(sender)
            return true
        }
        if (matchedCommand.size > 1) {
            showHelp(sender)
            return true
        }

        val cmd = matchedCommand[0]

        if (cmd.playerOnly && sender !is Player) {
            sender.sendMessage("Not Player")
            return true
        }
        if (cmd.permission != null && !sender.hasPermission(cmd.permission)) {
            sender.sendMessage("No Permission")
            return true
        }

        val commandArgs = arrayListOf<CommandArgument>()
        args.forEachIndexed { index, arg ->
            val p = cmd.parameters[index]
            if (p.parserId == null) {
                commandArgs.add(CommandArgument(p.id, arg, p.index, null))
                return@forEachIndexed
            }
            val parser = argumentParserRegistry.getParser(p.parserId)
            commandArgs.add(CommandArgument(p.id, arg, p.index, parser))
        }

        try {
            cmd.execute(sender, commandArgs, args, args.size)
        } catch (ex: AtomArgumentParserException) {
            sender.sendMessage(ex.message!!)
        } catch (ex: AtomCommandException) { }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: org.bukkit.command.Command, alias: String, args: Array<out String>): MutableList<String> {
        return arrayListOf()
    }

    override fun registerCommand(command: Command) {
        try {
            val list = commands[command.base.id] ?: arrayListOf()
            list.add(command)
            commands[command.base.id] = list
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun registerParser(parser: ArgumentParser) {
        argumentParserRegistry.addParser(parser)
    }

    fun showHelp(sender: CommandSender) {
        for (cmds in commands.values) {
            for (cmd in cmds) {
                showUsage(cmd, sender)
            }
        }
    }

    fun showUsage(commands: Collection<Command>, sender: CommandSender) {
        commands.forEach { showUsage(it, sender) }
    }

    fun showUsage(command: Command, sender: CommandSender) {
        if (command.permission != null && !sender.hasPermission(command.permission)) {
            return
        }
        sender.sendMessage("/$commandMainColored ${command.usage}")
    }
}
package me.cyberproton.atom.api.command

import me.cyberproton.atom.api.exception.AtomCommandException
import me.cyberproton.atom.api.extension.colored
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class Command(
    val syntax: String,
    val permission: String? = null,
    usage: String? = null,
    val playerOnly: Boolean = false,
    val requireExactPermissions: Boolean = false,
)
{
    private val permissions: MutableSet<String>
    val usage: String
    val base: CommandParameter
    val parameters: MutableList<CommandParameter>
    val minArguments: Int
    val maxArguments: Int
    val mask: String

    init {
        val parts = syntax.trim().split("\\s+".toRegex())
        if (parts.isEmpty()) {
            throw AtomCommandException("Syntax for command is empty")
        }
        base = readParameter(parts[0], 0)
        if (base.type != CommandParameter.Type.BASE) {
            throw AtomCommandException("Command base must be base parameter: ${parts[0]}")
        }
        parameters = arrayListOf(base)

        var optionalStartIndex = -1
        if (parts.size > 1) {
            for (i in 1 until parts.size) {
                val param = readParameter(parts[i], i)
                if (optionalStartIndex == -1 && param.type == CommandParameter.Type.OPTIONAL) optionalStartIndex = i
                if (param.type != CommandParameter.Type.OPTIONAL && optionalStartIndex != -1 && optionalStartIndex < i)
                    throw AtomCommandException("Optional parameter must be placed after base and required parameter: $syntax")
                parameters.add(param)
            }
        }

        maxArguments = parts.size
        minArguments = if (optionalStartIndex == -1) maxArguments else optionalStartIndex

        if (usage == null) {
            var u = ""
            for (parameter in parameters) {
                u += when (parameter.type) {
                    CommandParameter.Type.REQUIRED -> " &6<${parameter.id}>&f"
                    CommandParameter.Type.OPTIONAL -> " &e(${parameter.id})&f"
                    else -> " ${parameter.id}"
                }
            }
            this.usage = u.trim().colored()
        } else {
            this.usage = usage.colored()
        }

        mask = parameters.fold("") { acc, param ->
            acc + when (param.type) {
                CommandParameter.Type.REQUIRED -> "1"
                CommandParameter.Type.OPTIONAL -> "2"
                else -> "0"
            }
        }

        permissions = hashSetOf()
        if (permission != null) {
            val permsArray = permission.split("|")
            permissions.addAll(permsArray)
        }
    }

    abstract fun execute(sender: CommandSender, args: List<CommandArgument>, stringArgs: Array<out String>, numberOfArgs: Int)

    fun validatePermission(sender: CommandSender): Boolean = permissions.any { sender.hasPermission(it) }

    protected fun throwCommandException(exception: AtomCommandException) {

    }

    private fun readParameter(param: String, index: Int): CommandParameter {
        if (param.isEmpty()) {
            throw AtomCommandException("Command syntax parameter is empty")
        }
        if (param[0] == '<') {
            if (param.length < 3 || param.last() != '>') {
                throw AtomCommandException("Expected required parameter to follow the form <w+(@w+)*>, found $param")
            }
            val comps = param.substring(1, param.length - 1).split('@')
            if (comps.size > 2) {
                throwRequiredParameterException(param)
            }
            return if (comps.size == 2) CommandParameter(CommandParameter.Type.REQUIRED, comps[0], index, comps[1]) else CommandParameter(
                CommandParameter.Type.REQUIRED, comps[0], index)
        } else if (param[0] == '(') {
            if (param.length < 3 || param.last() != ')') {
                throw AtomCommandException("Expected optional parameter to follow the form (w+(@w+)*), found $param")
            }
            val comps = param.substring(1, param.length - 1).split('@')
            if (comps.size > 2) {
                throwOptionalParameterException(param)
            }
            return if (comps.size == 2) CommandParameter(CommandParameter.Type.OPTIONAL, comps[0], index, comps[1]) else CommandParameter(
                CommandParameter.Type.OPTIONAL, comps[0], index)
        }
        return CommandParameter(CommandParameter.Type.BASE, param, index)
    }

    private fun throwRequiredParameterException(found: String) {
        throw AtomCommandException("Expected required parameter to follow the form <w+(@w+)*>, found $found")
    }

    private fun throwOptionalParameterException(found: String) {
        throw AtomCommandException("Expected optional parameter to follow the form (w+(@w+)*), found $found")
    }
}
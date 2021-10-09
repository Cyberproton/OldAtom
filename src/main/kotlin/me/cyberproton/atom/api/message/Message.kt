package me.cyberproton.atom.api.message

import me.cyberproton.atom.api.extension.colored
import org.bukkit.command.CommandSender

class Message(
    val id: String,
    val defaultMessage: String,
    val code: Int? = null,
    val errorMessage: String = "Some error has happened, please contact admin for more information. Code: {code}",
)
{
    internal var message: String = defaultMessage
    val configPath = id.lowercase().replace('_', '-')

    fun send(receiver: CommandSender, vararg args: Arg) {
        receiver.sendMessage(formatColored(*args))
    }

    fun error(receiver: CommandSender) {
        receiver.sendMessage(errorMessage.replace("{code}", code?.toString() ?: "?"))
    }

    fun format(vararg args: Arg): String {
        var m = message
        for (arg in args) {
            m = m.replace("{${arg.id}}", arg.value)
        }
        return m
    }

    fun formatColored(vararg args: Arg): String {
        var m = message
        for (arg in args) {
            m = m.replace("{${arg.id}}", arg.value)
        }
        return m.colored()
    }

    fun register(messageManager: MessageManager) {
        messageManager.registerMessage(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Message) return false

        if (id != other.id) return false
        if (defaultMessage != other.defaultMessage) return false
        if (code != other.code) return false
        if (configPath != other.configPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + defaultMessage.hashCode()
        result = 31 * result + (code ?: 0)
        result = 31 * result + configPath.hashCode()
        return result
    }

    class Arg(val id: String, val value: String)
}
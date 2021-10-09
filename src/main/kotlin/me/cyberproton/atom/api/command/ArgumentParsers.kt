package me.cyberproton.atom.api.command

import me.cyberproton.atom.common.message.Messages
import org.bukkit.Bukkit

object ArgumentParsers {
    object PlayerArgumentParser : ArgumentParser("player", Messages.PLAYER_NOT_FOUND) {
        override fun parse(arg: String): Any? {
            return Bukkit.getPlayer(arg)
        }
    }

    object StringArgumentParser : ArgumentParser("string") {
        override fun parse(arg: String): Any = arg
    }

    object DoubleArgumentParser : ArgumentParser("double", Messages.COMMAND_INPUT_IS_NOT_A_DOUBLE) {
        override fun parse(arg: String): Any? = arg.toDoubleOrNull()
    }

    object IntegerArgumentParser : ArgumentParser("int", Messages.COMMAND_INPUT_IS_NOT_A_INTEGER) {
        override fun parse(arg: String): Any? = arg.toIntOrNull()
    }

    object BoolArgumentParser : ArgumentParser("bool", Messages.COMMAND_INPUT_IS_NOT_A_BOOLEAN) {
        override fun parse(arg: String): Any? = arg.toBooleanStrictOrNull()
    }

    object LongArgumentParser : ArgumentParser("long", Messages.COMMAND_INPUT_IS_NOT_A_LONG) {
        override fun parse(arg: String): Any? = arg.toLongOrNull()
    }
}
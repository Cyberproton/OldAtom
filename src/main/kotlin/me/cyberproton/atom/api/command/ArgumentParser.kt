package me.cyberproton.atom.api.command

import me.cyberproton.atom.api.exception.AtomArgumentParserException
import me.cyberproton.atom.api.message.Message
import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class ArgumentParser(val id: String, val errorMessage: Message = Message("ARGUMENT_PARSER_PARSE_ERROR", "Syntax Error")) {
    abstract fun parse(arg: String): Any?

    open fun parseOrThrow(arg: String, vararg formats: Message.Arg) = parse(arg) ?: throw AtomArgumentParserException(errorMessage.formatColored(*formats))

    open fun parse(arg: CommandArgument) = parse(arg.arg)

    open fun parseOrThrow(arg: CommandArgument, vararg formats: Message.Arg) = parseOrThrow(arg.arg, *formats)

    open fun <T> parse(arg: String, clazz: Class<T>): T? {
        val r = parse(arg) ?: return null
        return try {
            clazz.cast(r)
        } catch (ex: ClassCastException) {
            null
        }
    }

    open fun <T : Any> parse(arg: String, clazz: KClass<T>): T? {
        val r = parse(arg) ?: return null
        return try {
            clazz.cast(r)
        } catch (ex: ClassCastException) {
            null
        }
    }

    open fun <T> parse(arg: CommandArgument, clazz: Class<T>): T? {
        return parse(arg.arg, clazz)
    }

    open fun <T : Any> parse(arg: CommandArgument, clazz: KClass<T>): T? {
        return parse(arg.arg, clazz)
    }

    open fun <T> parseOrThrow(arg: String, clazz: Class<T>, vararg formats: Message.Arg): T = parse(arg, clazz) ?: throw AtomArgumentParserException(errorMessage.formatColored(*formats))

    open fun <T : Any> parseOrThrow(arg: String, clazz: KClass<T>, vararg formats: Message.Arg): T = parse(arg, clazz) ?: throw AtomArgumentParserException(errorMessage.formatColored(*formats))

    open fun <T> parseOrThrow(arg: CommandArgument, clazz: Class<T>, vararg formats: Message.Arg): T = parse(arg.arg, clazz) ?: throw AtomArgumentParserException(errorMessage.formatColored(*formats))

    open fun <T : Any> parseOrThrow(arg: CommandArgument, clazz: KClass<T>, vararg formats: Message.Arg): T = parse(arg.arg, clazz) ?: throw AtomArgumentParserException(errorMessage.formatColored(*formats))
}


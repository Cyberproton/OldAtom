package me.cyberproton.atom.api.command

import me.cyberproton.atom.api.exception.NoArgumentParserFound
import kotlin.reflect.KClass

class CommandArgument(
    val id: String,
    val arg: String,
    val index: Int,
    val parser: ArgumentParser?,
) {
    fun <T> parse(clazz: Class<T>): T? = if (parser == null) throw NoArgumentParserFound("No argument parser is found") else parser.parse(arg, clazz)

    fun <T: Any> parse(clazz: KClass<T>): T? = if (parser == null) throw NoArgumentParserFound("No argument parser is found") else parser.parse(arg, clazz)

    fun <T> parseOrThrow(clazz: Class<T>): T = if (parser == null) throw NoArgumentParserFound("No argument parser is found") else parser.parseOrThrow(arg, clazz)

    fun <T : Any> parseOrThrow(clazz: KClass<T>): T = if (parser == null) throw NoArgumentParserFound("No argument parser is found") else parser.parseOrThrow(arg, clazz)
}
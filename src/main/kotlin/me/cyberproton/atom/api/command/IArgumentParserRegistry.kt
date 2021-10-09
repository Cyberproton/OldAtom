package me.cyberproton.atom.api.command

interface IArgumentParserRegistry {
    fun getParser(id: String): ArgumentParser?

    fun addParser(argumentParser: ArgumentParser)

    fun removeParser(argumentParser: ArgumentParser)

    fun removeParser(id: String)
}
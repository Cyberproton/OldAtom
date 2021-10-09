package me.cyberproton.atom.api.command

class ArgumentParserRegistry : IArgumentParserRegistry {
    private val parsers: MutableMap<String, ArgumentParser> = hashMapOf()

    init {
        addParser(ArgumentParsers.StringArgumentParser)
        addParser(ArgumentParsers.DoubleArgumentParser)
        addParser(ArgumentParsers.IntegerArgumentParser)
        addParser(ArgumentParsers.BoolArgumentParser)
        addParser(ArgumentParsers.LongArgumentParser)
        addParser(ArgumentParsers.PlayerArgumentParser)
    }

    override fun getParser(id: String): ArgumentParser? = parsers[id]

    override fun addParser(argumentParser: ArgumentParser) {
        parsers[argumentParser.id] = argumentParser
    }

    override fun removeParser(id: String) {
        parsers.remove(id)
    }

    override fun removeParser(argumentParser: ArgumentParser) {
        removeParser(argumentParser.id)
    }
}
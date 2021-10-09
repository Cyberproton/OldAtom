package me.cyberproton.atom.api.exception

open class AtomException(message: String) : RuntimeException(message)

class AtomPlayerNotFoundException(message: String): AtomException(message)

open class AtomCommandException(message: String): AtomException(message)

class AtomArgumentParserException(message: String): AtomCommandException(message)

class NoArgumentParserFound(message: String): AtomCommandException(message)

class HookNotFoundException(message: String): AtomException(message)
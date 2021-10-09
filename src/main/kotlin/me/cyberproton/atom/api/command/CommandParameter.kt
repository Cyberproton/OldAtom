package me.cyberproton.atom.api.command

class CommandParameter(
    val type: Type,
    val id: String,
    val index: Int,
    val parserId: String? = null,
) {
    enum class Type {
        BASE,
        REQUIRED,
        OPTIONAL,
    }
}
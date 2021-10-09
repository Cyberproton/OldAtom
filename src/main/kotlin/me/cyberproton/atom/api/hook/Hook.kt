package me.cyberproton.atom.api.hook

import me.cyberproton.atom.api.exception.HookNotFoundException

interface Hook {
    fun getName(): String

    fun isEnabled(): Boolean

    fun getHookOrNull(): Any?

    fun getHook(): Any = getHookOrNull() ?: throw HookNotFoundException("Hook for ${getName()} is not found. Please check if plugin is installed")
}
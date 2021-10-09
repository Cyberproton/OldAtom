package me.cyberproton.atom.api

import me.cyberproton.atom.AtomPlugin

interface PluginComponent {
    val atomPlugin: AtomPlugin

    val priority: Int

    fun onEnable() { }

    fun onReload() { }

    fun onDisable() { }
}
package me.cyberproton.atom

import me.cyberproton.atom.api.PluginComponent
import org.bukkit.plugin.java.JavaPlugin

open class AtomPlugin : JavaPlugin() {
    private val pluginComponents: MutableList<PluginComponent> = arrayListOf()

    override fun onLoad() { }

    override fun onEnable() { }

    open fun onReload() { }

    override fun onDisable() { }

    fun registerPluginComponent(pluginComponent: PluginComponent) {
        pluginComponents.add(pluginComponent)
        pluginComponents.sortBy { it.priority }
    }

    fun unregisterPluginComponent(pluginComponent: PluginComponent) {
        pluginComponents.remove(pluginComponent)
        pluginComponents.sortBy { it.priority }
    }

    fun enablePluginComponents() {
        try {
            pluginComponents.forEach { it.onEnable() }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun reloadPluginComponents() {
        try {
            pluginComponents.forEach { it.onReload() }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun disablePluginComponents() {
        try {
            pluginComponents.forEach { it.onDisable() }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
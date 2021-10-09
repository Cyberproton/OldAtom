package me.cyberproton.atom.api

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

object Plugin {
    private var plugin: JavaPlugin? = null
    private var mainThread: Thread? = null

    fun getPlugin(): JavaPlugin {
        // Double check locking
        if (me.cyberproton.atom.api.Plugin.plugin == null) {
            synchronized(this) {
                if (me.cyberproton.atom.api.Plugin.plugin == null) {
                    me.cyberproton.atom.api.Plugin.plugin = JavaPlugin.getProvidingPlugin(me.cyberproton.atom.api.Plugin::class.java)
                }
            }
        }
        return me.cyberproton.atom.api.Plugin.plugin!!
    }

    @Synchronized
    fun getMainThread(): Thread? {
        if (me.cyberproton.atom.api.Plugin.mainThread == null) {
            if (Bukkit.getServer().isPrimaryThread) {
                me.cyberproton.atom.api.Plugin.mainThread = Thread.currentThread()
            }
        }
        return me.cyberproton.atom.api.Plugin.mainThread
    }
}
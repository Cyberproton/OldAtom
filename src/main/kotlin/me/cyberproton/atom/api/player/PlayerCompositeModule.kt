package me.cyberproton.atom.api.player

import java.util.*

interface PlayerCompositeModule {
    fun getPlayerModule(id: UUID): PlayerModule?

    fun getPlayerModules(): Collection<PlayerModule>

    fun addPlayerModule(playerModule: PlayerModule)

    fun removePlayerModule(playerModule: PlayerModule)

    fun onLoad() {
        getPlayerModulesSortedByPriority().forEach {
            it.onLoad()
        }
    }

    fun onUpdate() {
        val modules = getPlayerModulesSortedByPriority()
        for (module in modules) {
            module.onPreUpdate()
        }
        for (module in modules) {
            module.onUpdate()
        }
        for (module in modules) {
            module.onPostUpdate()
        }
    }

    fun onUnload() {
        getPlayerModulesSortedByPriority(true).forEach { it.onUnload() }
    }

    fun getPlayerModulesSortedByPriority(reverse: Boolean = false): Collection<PlayerModule> {
        if (reverse) {
            return getPlayerModules().sortedByDescending { it.priority }
        }
        return getPlayerModules().sortedBy { it.priority }
    }
}
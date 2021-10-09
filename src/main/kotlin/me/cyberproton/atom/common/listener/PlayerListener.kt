package me.cyberproton.atom.common.listener

import me.cyberproton.atom.api.log.Log
import me.cyberproton.atom.api.player.IPlayerRegistry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener(private val playerRegistry: IPlayerRegistry): Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Log.i(javaClass.simpleName, "Atom Player Created")
        playerRegistry.addPlayer(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        playerRegistry.removePlayer(event.player)
    }
}
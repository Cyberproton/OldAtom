package me.cyberproton.atom.api.player

import me.cyberproton.atom.AtomPlugin
import me.cyberproton.atom.api.PluginComponent
import me.cyberproton.atom.api.event.AtomPlayerCreateEvent
import me.cyberproton.atom.api.event.AtomPlayerRemoveEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class PlayerRegistry(override val atomPlugin: AtomPlugin, override val priority: Int) : IPlayerRegistry, PluginComponent {
    private val players: MutableMap<UUID, IPlayer> = hashMapOf()
    private lateinit var task: BukkitTask

    override fun onEnable() {
        task = Bukkit.getScheduler().runTaskTimer(atomPlugin, Runnable { onTask() }, 0L, 1L)
    }

    override fun onDisable() {
        task.cancel()
    }

    override fun getPlayer(player: Player): IPlayer? {
        return players[player.uniqueId]
    }

    override fun addPlayer(player: Player): IPlayer? {
        if (players.containsKey(player.uniqueId)) {
            return players[player.uniqueId]
        }
        if (player.hasMetadata("NPC")) {
            return null
        }
        val handler = AtomPlayer(player)
        players[player.uniqueId] = handler
        Bukkit.getPluginManager().callEvent(AtomPlayerCreateEvent(handler))
        handler.onLoad()
        return handler
    }

    override fun removePlayer(player: Player): IPlayer? {
        if (!players.containsKey(player.uniqueId)) {
            return null
        }
        val handler = players[player.uniqueId]!!
        handler.onUnload()
        Bukkit.getPluginManager().callEvent(AtomPlayerRemoveEvent(handler))
        players.remove(player.uniqueId)
        return handler
    }

    override fun onReload() {
        players.values.forEach { it.onUpdate() }
    }

    fun onTask() {
        players.values.forEach { it.task.run() }
    }
}
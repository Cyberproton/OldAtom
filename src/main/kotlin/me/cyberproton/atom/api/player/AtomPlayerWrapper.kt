package me.cyberproton.atom.api.player

import org.bukkit.entity.Player
import java.util.*

open class AtomPlayerWrapper(private val player: IPlayer) : IPlayer {
    override val bukkitPlayer: Player = player.bukkitPlayer

    override val stats: IPlayerStats = player.stats

    override val permissions: IPlayerPermissions = player.permissions

    override val potionEffects: IPlayerPotionEffects = player.potionEffects

    override val task: PlayerTask = player.task

    override fun addPlayerModule(playerModule: PlayerModule) {
        player.addPlayerModule(playerModule)
    }

    override fun getPlayerModule(id: UUID): PlayerModule? = player.getPlayerModule(id)

    override fun getPlayerModules(): Collection<PlayerModule> = player.getPlayerModules()

    override fun removePlayerModule(playerModule: PlayerModule) {
        player.removePlayerModule(playerModule)
    }
}
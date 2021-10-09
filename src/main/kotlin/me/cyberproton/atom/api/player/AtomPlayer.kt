package me.cyberproton.atom.api.player

import org.bukkit.entity.Player
import java.util.*

class AtomPlayer(override val bukkitPlayer: Player) : IPlayer {
    private val playerModules: MutableMap<UUID, PlayerModule> = hashMapOf()
    override val stats: IPlayerStats = PlayerStats(UUID.fromString("3475bd01-aceb-4c09-b684-4f57876eb8a4"), this, 100)
    override val permissions: IPlayerPermissions = PlayerPermissions(UUID.fromString("02393d3d-1361-40d9-8663-5776ff436add"), this, 200)
    override val potionEffects: IPlayerPotionEffects = PlayerPotionEffects(UUID.fromString("8c7278ee-70f5-464e-8bc9-a8687fb833a6"), this, 300)
    override val task: PlayerTask = PlayerTask(UUID.fromString("0567fd3c-e061-474c-bb99-87aaae2787b4"), this, 400)

    init {
        addPlayerModule(stats as PlayerModule)
        addPlayerModule(permissions as PlayerModule)
        addPlayerModule(potionEffects as PlayerModule)
        addPlayerModule(task)
    }

    override fun getPlayerModule(id: UUID): PlayerModule? = playerModules[id]

    override fun getPlayerModules(): Collection<PlayerModule> = playerModules.values

    override fun addPlayerModule(playerModule: PlayerModule) {
        playerModules[playerModule.id] = (playerModule)
    }

    override fun removePlayerModule(playerModule: PlayerModule) {
        playerModules.remove(playerModule.id)
    }

    override fun getPlayerModulesSortedByPriority(reverse: Boolean): Collection<PlayerModule> {
        if (reverse) {
            return playerModules.values.sortedByDescending { it.priority }
        }
        return playerModules.values.sortedBy { it.priority }
    }
}
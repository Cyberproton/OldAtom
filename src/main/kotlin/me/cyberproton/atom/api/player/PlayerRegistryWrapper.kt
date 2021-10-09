package me.cyberproton.atom.api.player

import org.bukkit.entity.Player

open class PlayerRegistryWrapper(private val registry: IPlayerRegistry) : IPlayerRegistry {
    override fun getPlayer(player: Player): IPlayer? {
        TODO("Not yet implemented")
    }

    override fun addPlayer(player: Player): IPlayer? {
        TODO("Not yet implemented")
    }

    override fun removePlayer(player: Player): IPlayer? {
        TODO("Not yet implemented")
    }
}
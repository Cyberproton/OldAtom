package me.cyberproton.atom.api.player

import org.bukkit.entity.Player

interface IPlayerRegistry {
    fun getPlayer(player: Player): IPlayer?

    fun addPlayer(player: Player): IPlayer?

    fun removePlayer(player: Player): IPlayer?
}
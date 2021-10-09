package me.cyberproton.atom.api.player

import org.bukkit.entity.Player

interface IPlayer : PlayerCompositeModule {
    val bukkitPlayer: Player

    val stats: IPlayerStats

    val permissions: IPlayerPermissions

    val potionEffects: IPlayerPotionEffects

    val task: PlayerTask
}
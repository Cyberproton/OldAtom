package me.cyberproton.atom.api.player

import java.util.*

class PlayerTask(override val id: UUID, override val player: IPlayer, override val priority: Int)
    : PlayerModule, Runnable
{
    override fun run() {
        val potionEffects = player.potionEffects as? PlayerPotionEffects
        potionEffects?.checkUpdateActiveEffects()
    }
}
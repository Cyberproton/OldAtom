package me.cyberproton.atom.api.player

import java.util.*

interface PlayerModule {
    val id: UUID

    val player: IPlayer

    val priority: Int

    fun onLoad() = onUpdate()

    fun onPreUpdate() { }

    fun onUpdate() { }

    fun onPostUpdate() { }

    fun onUnload() { }
}
package me.cyberproton.atom.api.event

import me.cyberproton.atom.api.log.Log
import me.cyberproton.atom.api.player.IPlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AtomPlayerCreateEvent(val player: IPlayer) : Event()
{
    init {
        Log.i(javaClass.simpleName, "Event created")
    }

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        private val handlerList: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
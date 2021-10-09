package me.cyberproton.atom.common.listener

import me.cyberproton.atom.Atom
import me.cyberproton.atom.api.log.Log
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent

class PotionListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPotionEffect(event: EntityPotionEffectEvent) {
        Log.d(javaClass.simpleName, "Action: ${event.action}")
        Log.d(javaClass.simpleName, "Old: ${event.oldEffect}")
        Log.d(javaClass.simpleName, "New: ${event.newEffect}")
        if (processing) {
            return
        }
        Log.d(javaClass.simpleName, "Processing potions")
        if (event.entity !is Player) {
            return
        }
        val player = Atom.instance.playerRegistry.getPlayer(event.entity as Player)!!
        processing = true
        try {
            val oldEffect = event.oldEffect
            val newEffect = event.newEffect
            when (event.action) {
                EntityPotionEffectEvent.Action.ADDED, EntityPotionEffectEvent.Action.CHANGED -> player.potionEffects.addPotionEffect(newEffect!!)
                EntityPotionEffectEvent.Action.REMOVED, EntityPotionEffectEvent.Action.CLEARED -> player.potionEffects.removePotionEffect(oldEffect!!)
            }
        } finally {
            processing = false
        }
    }

    companion object {
        @JvmStatic
        private var processing: Boolean = false
    }
}
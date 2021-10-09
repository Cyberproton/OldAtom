package me.cyberproton.atom.common.listener

import me.cyberproton.atom.Atom
import me.cyberproton.atom.api.mechanic.EntityEffect
import me.cyberproton.atom.api.mechanic.PlayerEffect
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class MechanicListener : Listener {
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.damage == 0.0) {
            return
        }
        if (event.entity.hasMetadata("NPC")) {
            return
        }
        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            if (event.damager is LivingEntity && event.entity is Player || event.damager is Player && event.entity is LivingEntity) {
                val defenderEffect = if (event.entity is Player) {
                    PlayerEffect(Atom.instance.playerRegistry.getPlayer(event.entity as Player)!!, false)
                } else {
                    EntityEffect(event.entity as LivingEntity, false)
                }
                val attackerEffect = if (event.damager is Player) {
                    PlayerEffect(Atom.instance.playerRegistry.getPlayer(event.damager as Player)!!, true)
                } else {
                    EntityEffect(event.damager as LivingEntity, true)
                }

                attackerEffect.apply(event)
                defenderEffect.apply(event)
            }
        }
    }
}
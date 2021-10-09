package me.cyberproton.atom.api.player

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import me.cyberproton.atom.Atom
import me.cyberproton.atom.api.log.Log
import me.cyberproton.atom.api.potion.AtomPotionEffect
import me.cyberproton.atom.api.potion.BukkitPotionEffect
import me.cyberproton.atom.api.potion.IPotionEffect
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class PlayerPotionEffects(override val id: UUID, override val player: IPlayer, override val priority: Int) : IPlayerPotionEffects, PlayerModule {
    private val effects: MutableMap<PotionEffectType, MutableList<IPotionEffect>> = HashMap()
    private val activeEffects: MutableMap<PotionEffectType, IPotionEffect> = HashMap()

    init {
        for (type in PotionEffectType.values()) {
            effects[type] = arrayListOf()
        }
    }

    override fun onPreUpdate() {
        clearAtomPotionEffects()
    }

    override fun onUpdate() {

    }

    override fun onPostUpdate() {
        updateActiveEffects()
    }

    override fun addPotionEffect(potionEffect: IPotionEffect) {
        if (potionEffect.durationLeft <= 0) {
            return
        }
        effects[potionEffect.type]!!.add(potionEffect)
        updateActiveEffects()
    }

    override fun addPotionEffect(potionEffect: PotionEffect) {
        addPotionEffect(IPotionEffect.fromBukkit(potionEffect))
    }

    override fun removePotionEffect(potionEffect: IPotionEffect) {
        val b = effects[potionEffect.type]!!.remove(potionEffect)
        if (b) {
            updateActiveEffects()
        }
    }

    override fun removePotionEffect(potionEffect: PotionEffect) {
        Log.d(javaClass.simpleName, "Removing effect: type=${potionEffect.type} amp=${potionEffect.amplifier}, dur=${potionEffect.duration}, isAmb=${potionEffect.isAmbient}, hasPar=${potionEffect.hasParticles()}, hasIcon=${potionEffect.hasIcon()}")
        Log.d(javaClass.simpleName, "Before removal: ")
        for (effect in effects[potionEffect.type]!!) {
            var b = false
            if (effect.amplifier == potionEffect.amplifier &&
                effect.durationLeft == potionEffect.duration &&
                effect.isAmbient == potionEffect.isAmbient &&
                effect.hasParticles == potionEffect.hasParticles() &&
                effect.hasIcon == potionEffect.hasIcon()) {
                b = true
            }
            Log.d(javaClass.simpleName, "Effect: $effect ${(if (b) " > Removed" else "")}")
        }
        Log.d("", "")
        val b = effects[potionEffect.type]!!.removeIf {
                    it.amplifier == potionEffect.amplifier &&
                    it.durationLeft == potionEffect.duration &&
                    it.isAmbient == potionEffect.isAmbient &&
                    it.hasParticles == potionEffect.hasParticles() &&
                    it.hasIcon == potionEffect.hasIcon()
        }
        Log.d(javaClass.simpleName, "After removal: ")
        for (effect in effects[potionEffect.type]!!) {
            Log.d(javaClass.simpleName, "Effect: $effect")
        }
        Log.d("", "")
        if (b) {
            updateActiveEffects()
        }
        Log.d(javaClass.simpleName, "End of removal.\n")
    }

    override fun removePotionEffect(type: PotionEffectType) {
        val b = effects[type]!!.isNotEmpty()
        effects[type] = arrayListOf()
        if (b) {
            updateActiveEffects()
        }
    }

    fun getPotionEffects(potionEffectType: PotionEffectType): List<IPotionEffect> {
        return ArrayList(effects[potionEffectType]!!)
    }

    override fun clearPotionEffects() {
        for (type in PotionEffectType.values()) {
            effects[type] = arrayListOf()
        }
        updateActiveEffects()
    }

    fun clearBukkitPotionEffects(update: Boolean = false) {
        for (type in PotionEffectType.values()) {
            effects[type]!!.removeIf { it is BukkitPotionEffect }
        }
        if (update) {
            updateActiveEffects()
        }
    }

    fun clearAtomPotionEffects(update: Boolean = false) {
        for (type in PotionEffectType.values()) {
            effects[type]!!.removeIf { it is AtomPotionEffect }
        }
        if (update) {
            updateActiveEffects()
        }
    }

    fun checkUpdateActiveEffects() {
        var b = false
        for (effectList in effects.values) {
            if (effectList.any { it.durationLeft <= 0 }) {
                b = true
                break
            }
        }
        if (b) {
            updateActiveEffects()
        }
    }

    fun updateActiveEffects() {
        Log.d(javaClass.simpleName, "Update active potion effects")
        effects.values.forEach { it.removeIf { it.durationLeft <= 0 } }
        for (effect in activeEffects.values) {
            val packet = PacketContainer(PacketType.Play.Server.REMOVE_ENTITY_EFFECT)
            packet.integers.write(0, player.bukkitPlayer.entityId)
            packet.effectTypes.write(0, effect.type)
            try {
                Atom.instance.protocolManager.sendServerPacket(player.bukkitPlayer, packet)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            //player.bukkitPlayer.removePotionEffect(value.type)
        }
        activeEffects.clear()
        for ((type, effects) in effects) {
            var a = 0
            var d = Integer.MAX_VALUE
            var ia = false
            var hp = false
            var hi = false
            if (effects.isEmpty()) {
                continue
            }
            for (effect in effects) {
                a += effect.amplifier
                d = minOf(effect.durationLeft, d)
                if (effect.isAmbient) ia = true
                if (effect.hasParticles) hp = true
                if (effect.hasIcon) hi = true
                Log.d(javaClass.simpleName, "effect: $effect")
            }
            Log.d(javaClass.simpleName, "final: ${AtomPotionEffect(type, d, a, ia, hp, hi)}")
            activeEffects[type] = AtomPotionEffect(type, d, a, ia, hp, hi)
        }
        for (effect in activeEffects.values) {
            val packet = PacketContainer(PacketType.Play.Server.ENTITY_EFFECT)
            packet.integers.write(0, player.bukkitPlayer.entityId)
            packet.bytes.write(0, effect.type.id.toByte())
            packet.bytes.write(1, effect.amplifier.toByte())
            packet.integers.write(1, effect.durationLeft)
            var flags = 0
            if (effect.isAmbient) flags += 1
            if (effect.hasParticles) flags += 2
            if (effect.hasIcon) flags += 3
            packet.bytes.write(2, flags.toByte())
            Log.d(javaClass.simpleName, "Sending packet of $effect")
            try {
                Atom.instance.protocolManager.sendServerPacket(player.bukkitPlayer, packet)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            //player.bukkitPlayer.addPotionEffect(IPotionEffect.toBukkit(effect))
        }
        Log.d(javaClass.simpleName, "End of update.\n")
    }
}
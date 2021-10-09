package me.cyberproton.atom.api.mechanic

import me.cyberproton.atom.Atom
import me.cyberproton.atom.api.stat.container.StatModifier
import me.cyberproton.atom.api.player.IPlayer
import me.cyberproton.atom.api.stat.DoubleStat
import me.cyberproton.atom.api.stat.Stats
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.concurrent.ThreadLocalRandom

class PlayerEffect(
    private val player: IPlayer,
    isAttacker: Boolean,
    isRangedAttack: Boolean = false
) : EntityEffect(player.bukkitPlayer, isAttacker, isRangedAttack)
{
    override fun apply(event: EntityDamageByEntityEvent): Double {
        var baseAttacker = 0.0
        var baseDefender = 0.0
        if (isAttacker) {
            baseAttacker = event.damage
        } else {
            baseDefender = event.damage
        }
        val physicalDamage = total[EffectModifier.Type.PHYSICAL_DAMAGE]!!
        val physicalCriticalPower = total[EffectModifier.Type.PHYSICAL_CRITICAL_POWER]!!
        var attributePhysicalCriticalPower = 0.0
        var attributePhysicalDamageReductionAxeInPercent = 0.0
        var attributePhysicalDamageReductionSwordInPercent = 0.0
        if (!isAttacker && event.damager is Player) {
            val other = event.damager as Player
            val attributes = Atom.instance.playerRegistry.getPlayer(other)!!.stats
            if (ThreadLocalRandom.current()
                    .nextDouble() <= attributes.getValue(Stats.CRITICAL_STRIKE_CHANCE) / 100.0
            ) {
                attributePhysicalCriticalPower = attributes.getValue(Stats.CRITICAL_STRIKE_POWER)
            }
        }
        var attributeArrowPhysicalDamage = 0.0
        var attributeArrowPhysicalCriticalPower = 0.0
        if (isRangedAttack) {
            if (!isAttacker && (event.damager as Projectile).shooter is Player) {
                val firer = (event.damager as Projectile).shooter as Player
                val attributes = Atom.instance.playerRegistry.getPlayer(firer)!!.stats
                attributeArrowPhysicalDamage += attributes.getValue(Stats.ARROW_PHYSICAL_DAMAGE)
                val match = ThreadLocalRandom.current().nextDouble()
                if (match < attributes.getValue(Stats.ARROW_CRITICAL_STRIKE_CHANCE) / 100) {
                    attributeArrowPhysicalCriticalPower += attributes.getValue(Stats.ARROW_CRITICAL_STRIKE_POWER)
                    if (attributeArrowPhysicalCriticalPower != 0.0) {
                        for (i in 0..15) {
                            val dx = ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
                            val dy = ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
                            val dz = ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
                            target.world.spawnParticle(
                                Particle.CRIT_MAGIC,
                                target.location.add(dx, dy + 0.5, dz),
                                0,
                                dx,
                                dy,
                                dz
                            )
                        }
                    }
                }
                attributeArrowPhysicalDamage += baseDefender * attributeArrowPhysicalCriticalPower
            }
        }
        val trueDamage = total[EffectModifier.Type.TRUE_DAMAGE]!!
        val armorPenetration = total[EffectModifier.Type.ARMOR_PENETRATION]!!
        val armorDamage = total[EffectModifier.Type.ARMOR_DAMAGE]!!
        val armorHeal = total[EffectModifier.Type.ARMOR_REGENERATION]!!
        val lifeSteal = total[EffectModifier.Type.LIFE_STEAL]!!
        val heal = total[EffectModifier.Type.HEAL]!!
        val damagerEquipment = (event.damager as LivingEntity).equipment
        if (!isAttacker && damagerEquipment != null) {
            val mainHandType = damagerEquipment.itemInMainHand.type
            if (mainHandType.name.contains("_SWORD")) attributePhysicalDamageReductionSwordInPercent =
                player.stats.getValue(Stats.ATTACK_DAMAGE_REDUCTION_SWORD)
            if (mainHandType.name.contains("_AXE")) attributePhysicalDamageReductionAxeInPercent =
                this.player.stats.getValue(Stats.ATTACK_DAMAGE_REDUCTION_AXE)
        }
        val physicalDamageReductionInPercent = Math.min(
            total[EffectModifier.Type.PHYSICAL_DAMAGE_REDUCTION]!! + attributePhysicalDamageReductionSwordInPercent + attributePhysicalDamageReductionAxeInPercent,
            100.0
        )
        val armor =
            if (target.getAttribute(Attribute.GENERIC_ARMOR) != null) target.getAttribute(Attribute.GENERIC_ARMOR)!!
                .value - armorPenetration else 0.0
        val armorToughness = if (target.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS) != null) target.getAttribute(
            Attribute.GENERIC_ARMOR_TOUGHNESS
        )!!
            .value - armorPenetration else 0.0
        val physical =
            (physicalDamage + baseDefender * (physicalCriticalPower + attributePhysicalCriticalPower) - heal) * (1.0 - physicalDamageReductionInPercent / 100.0)
        val truePhysical = calculateTruePhysicalDamage(physical, armor, armorToughness, armorPenetration)
        val base = event.damage * (1.0 - physicalDamageReductionInPercent / 100.0)
        val baseReductionWithPen = calculateArmorDamageReduction(base, armor, armorToughness, armorPenetration)
        val baseReduction = calculateArmorDamageReduction(base, armor, armorToughness, 0.0)
        val penetrationPhysical = Math.max(0.0, baseReduction - baseReductionWithPen)
        val healing = heal + baseAttacker * lifeSteal / 100.0
        val trueFinal = trueDamage + penetrationPhysical - healing
        var health = target.health - trueFinal
        health = Math.max(health, 0.1)
        health = Math.min(health, target.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value)
        target.health = health
        val finalArmorDamage = armorDamage - armorHeal
        if (finalArmorDamage != 0.0 &&
            target.equipment != null
        ) {
            val equipment = target.equipment!!
            damageItemStack(equipment.helmet, finalArmorDamage)
            damageItemStack(equipment.chestplate, finalArmorDamage)
            damageItemStack(equipment.leggings, finalArmorDamage)
            damageItemStack(equipment.boots, finalArmorDamage)
        }
        if (temporary.isNotEmpty() && target is Player) {
            val attributes = Atom.instance.playerRegistry.getPlayer(target)!!.stats
            val attributeManager = Atom.instance.statManager
            for ((key, value) in temporary) {
                attributes
                    .getInstance(attributeManager.getStat(value.type.name) as DoubleStat)
                    .addTemporaryModifier(
                        key, StatModifier(value.value, value.op), value.seconds
                    )
            }
        }
        event.damage = base + physicalDamage + attributeArrowPhysicalDamage
        return base + physicalDamage
    }
}
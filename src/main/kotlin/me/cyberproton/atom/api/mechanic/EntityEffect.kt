package me.cyberproton.atom.api.mechanic

import me.cyberproton.atom.Atom
import me.cyberproton.atom.api.stat.container.StatModifier
import me.cyberproton.atom.api.stat.DoubleStat
import me.cyberproton.atom.api.stat.Stats
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.*
import java.util.concurrent.ThreadLocalRandom

open class EntityEffect(
    protected val target: LivingEntity,
    protected val isAttacker: Boolean,
    protected val isRangedAttack: Boolean = false
) {
    protected val modifiers: MutableMap<String, EffectModifier> = hashMapOf()
    protected val temporary: MutableMap<String, TemporaryEffectModifier> = hashMapOf()
    protected val total: MutableMap<EffectModifier.Type, Double> = EnumMap(EffectModifier.Type::class.java)

    init {
        for (type in EffectModifier.Type.values()) {
            total[type] = 0.0
        }
    }

    open fun apply(event: EntityDamageByEntityEvent): Double {
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
        if (!isAttacker && event.damager is Player) {
            val attributes = Atom.instance.playerRegistry.getPlayer(event.damager as Player)!!.stats
            if (ThreadLocalRandom.current()
                    .nextDouble() < attributes.getValue(Stats.CRITICAL_STRIKE_CHANCE) / 100
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
        val physicalDamageReductionAxeInPercent =
            (total[EffectModifier.Type.PHYSICAL_DAMAGE_REDUCTION]!!).coerceAtMost(100.0)
        val physicalDamageReductionInPercent =
            (total[EffectModifier.Type.PHYSICAL_DAMAGE_REDUCTION]!!).coerceAtMost(100.0)
        val armor: Double =
            if (target.getAttribute(Attribute.GENERIC_ARMOR) != null) target.getAttribute(Attribute.GENERIC_ARMOR)!!
                .value - armorPenetration else 0.0
        val armorToughness: Double =
            if (target.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS) != null) target.getAttribute(
                Attribute.GENERIC_ARMOR_TOUGHNESS
            )!!
                .value - armorPenetration else 0.0

        // Physical
        val physical =
            (physicalDamage + baseDefender * (physicalCriticalPower + attributePhysicalCriticalPower) - heal) * (1 - physicalDamageReductionInPercent / 100)
        val physicalReduction = calculateArmorDamageReduction(physical, armor, armorToughness, 0.0)
        val base = event.damage * (1 - physicalDamageReductionInPercent / 100)
        val baseReductionWithPen = calculateArmorDamageReduction(base, armor, armorToughness, armorPenetration)
        val baseReduction = calculateArmorDamageReduction(base, armor, armorToughness, 0.0)
        val penetrationPhysical = Math.max(0.0, baseReduction - baseReductionWithPen)

        /*
        AtomItems.log("Armor Penetration: " + armorPenetration);
        AtomItems.log("Physical: " + physical);
        AtomItems.log("Critical: " + (physicalCriticalPower + attributePhysicalCriticalPower));
        AtomItems.log("Damage reduced: " + ((physicalDamage + baseDefender * physicalCriticalPower - heal - physical) + (baseReductionWithPen)));
        AtomItems.log("Phy red in per: " + physicalDamageReductionInPercent);*/

        // Healing
        val healing = heal + baseAttacker * (lifeSteal / 100)
        /*
        AtomItems.log("Heal: " + healing);
        */

        // Final
        val trueFinal = trueDamage + penetrationPhysical - healing
        //AtomItems.log("True damage: " + trueFinal);
        var health = target.health - trueFinal
        health = Math.max(health, 0.1)
        health = Math.min(health, target.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value)
        target.health = health
        //AtomItems.log("Final: " + health);

        // Armor Damage
        val finalArmorDamage = armorDamage - armorHeal
        if (finalArmorDamage != 0.0) {
            if (target.equipment != null) {
                val equipment = target.equipment!!
                damageItemStack(equipment.helmet, finalArmorDamage)
                damageItemStack(equipment.chestplate, finalArmorDamage)
                damageItemStack(equipment.leggings, finalArmorDamage)
                damageItemStack(equipment.boots, finalArmorDamage)
            }
        }
        if (temporary.isNotEmpty() && target is Player) {
            val attributes = Atom.instance.playerRegistry.getPlayer(target)!!.stats
            val attributeManager = Atom.instance.statManager
            for ((key, value) in temporary) {
                attributes
                    .getInstance(attributeManager.getStat(value.type.name) as DoubleStat)!!
                    .addTemporaryModifier(
                        key, StatModifier(value.value, value.op), value.seconds
                    )
            }
        }

        //event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        event.damage = base + physicalDamage + attributeArrowPhysicalDamage
        return base + physicalDamage
    }

    fun effectModifier(key: String, value: Double, type: EffectModifier.Type) {
        modifiers[key] = EffectModifier(type, value)
        total[type] = total[type]!! + value
    }

    fun temporaryEffectModifier(key: String, value: Double, type: EffectModifier.Type, duration: Double) {
        temporary[key] = TemporaryEffectModifier(type, value, duration)
    }

    fun physicalDamage(key: String, value: Double) {
        effectModifier(key, value, EffectModifier.Type.PHYSICAL_DAMAGE)
    }

    fun physicalCriticalPower(key: String, value: Double) {
        effectModifier(key, value, EffectModifier.Type.PHYSICAL_CRITICAL_POWER)
    }

    fun trueDamage(key: String, value: Double) {
        effectModifier(key, value, EffectModifier.Type.TRUE_DAMAGE)
    }

    fun armorPenetration(key: String, value: Double) {
        effectModifier(key, value, EffectModifier.Type.ARMOR_PENETRATION)
    }

    fun armorDamage(key: String, value: Double) {
        effectModifier(key, value, EffectModifier.Type.ARMOR_DAMAGE)
    }

    fun armorRegeneration(key: String, value: Double) {
        effectModifier(key, value, EffectModifier.Type.ARMOR_REGENERATION)
    }

    fun lifeSteal(key: String, value: Double) {
        effectModifier(key, value, EffectModifier.Type.LIFE_STEAL)
    }

    fun physicalDamageReduction(key: String, percentage: Double) {
        effectModifier(key, percentage, EffectModifier.Type.PHYSICAL_DAMAGE_REDUCTION)
    }

    fun attackDamageBoost(key: String, value: Double, duration: Double, op: StatModifier.Operation) {
        temporary[key] = TemporaryEffectModifier(EffectModifier.Type.ATTACK_DAMAGE, value, duration, op)
    }

    fun attackDamageBoost(key: String, value: Double, duration: Double) {
        temporary[key] = TemporaryEffectModifier(EffectModifier.Type.ATTACK_DAMAGE, value, duration)
    }

    fun attackSpeedBoost(key: String, value: Double, duration: Double) {
        temporary[key] = TemporaryEffectModifier(EffectModifier.Type.ATTACK_SPEED, value, duration)
    }

    fun movementSpeed(key: String, value: Double, duration: Double, op: StatModifier.Operation) {
        temporary[key] = TemporaryEffectModifier(EffectModifier.Type.MOVEMENT_SPEED, value, duration, op)
    }

    fun movementSpeed(key: String, value: Double, duration: Double) {
        temporary[key] = TemporaryEffectModifier(EffectModifier.Type.MOVEMENT_SPEED, value, duration)
    }

    fun movementSpeedIncrease(key: String?, value: Double, duration: Double) {}
    
    fun armor(key: String, value: Double, duration: Double) {
        temporary[key] = TemporaryEffectModifier(EffectModifier.Type.ARMOR, value, duration)
    }

    fun armorToughness(key: String, value: Double, duration: Double) {
        temporary[key] = TemporaryEffectModifier(EffectModifier.Type.ARMOR_TOUGHNESS, value, duration)
    }

    fun heal(key: String, value: Double) {
        effectModifier(key, value, EffectModifier.Type.HEAL)
    }

    fun getValueOfEffectType(type: EffectModifier.Type): Double {
        return total[type]!!
    }

    companion object {
        fun calculateTruePhysicalDamage(
            damage: Double,
            armor: Double,
            armorToughness: Double,
            armorPenetration: Double
        ): Double {
            val nArmor = Math.max(0.0, armor - armorPenetration)
            val nToughness = Math.max(0.0, armorToughness - armorPenetration)
            return damage * (1 - Math.min(20.0, Math.max(nArmor / 5, nArmor - damage / (2 + nToughness / 4))) / 25)
        }

        fun calculateArmorDamageReduction(
            damage: Double,
            armor: Double,
            armorToughness: Double,
            armorPenetration: Double
        ): Double {
            val nArmor = Math.max(0.0, armor - armorPenetration)
            val nToughness = Math.max(0.0, armorToughness - armorPenetration)
            return damage * (Math.min(20.0, Math.max(nArmor / 5, nArmor - damage / (2 + nToughness / 4))) / 25)
        }

        fun damageItemStack(item: ItemStack?, damage: Double) {
            if (item == null) return
            val meta = item.itemMeta
            if (meta is Damageable) {
                val damageable = meta as Damageable
                damageable.damage = damageable.damage + damage.toInt()
                item.setItemMeta(meta)
            }
        }
    }
}
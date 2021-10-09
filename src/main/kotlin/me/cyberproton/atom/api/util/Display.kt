package me.cyberproton.atom.api.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.cyberproton.atom.api.extension.colored
import me.cyberproton.atom.api.extension.getItemFlags
import me.cyberproton.atom.api.extension.getStringListOrString
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.lang.reflect.Field
import java.util.*

class Display {
    val material: String
    val name: String?
    private val lore: List<String>
    val texture: String?
    val amount: Int
    val flags: Set<ItemFlag>
    private var item: ItemStack? = null

    constructor(material: String = "", name: String? = null, lore: List<String> = arrayListOf(), texture: String? = null, amount: Int = 1, flags: Set<ItemFlag> = hashSetOf()) {
        this.material = material
        this.name = name
        this.lore = lore.map { "&7$it" }
        this.texture = texture
        this.amount = amount
        this.flags = flags
    }

    constructor(section: ConfigurationSection) {
        this.material = section.getString("material") ?: ""
        this.name = section.getString("name")
        this.lore = section.getStringListOrString("lore").map { "&7$it" }
        this.texture = section.getString("texture")
        this.amount = section.getInt("amount", 1)
        this.flags = section.getItemFlags("flags")
    }

    fun withMaterial(material: String): Display = Display(material, name, lore, texture, amount, flags)

    fun withMaterial(material: Material): Display = withMaterial(material.name)

    fun getItemStack(defaultMaterial: Material = Material.DIAMOND): ItemStack {
        if (item != null) return item!!.clone()
        val mat = Material.getMaterial(material) ?: defaultMaterial
        val item = ItemStack(mat)
        val meta: ItemMeta = item.itemMeta
        if (texture != null && item.type == Material.PLAYER_HEAD) {
            val profile = GameProfile(UUID.randomUUID(), null)
            profile.properties.put("textures", Property("textures", texture))
            val profileField: Field
            try {
                profileField = meta.javaClass.getDeclaredField("profile")
                profileField.isAccessible = true
                profileField[meta] = profile
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
        if (name != null) {
            meta.setDisplayName(name.colored())
        }
        meta.lore = lore.colored()
        meta.addItemFlags(*flags.toTypedArray())
        item.itemMeta = meta
        this.item = item.clone()
        item.amount = amount
        return item
    }

    fun getLore(): List<String> = lore.toMutableList()
}
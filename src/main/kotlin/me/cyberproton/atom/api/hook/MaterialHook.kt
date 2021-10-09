package me.cyberproton.atom.api.hook

import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MaterialHook {
    val headDatabaseApi: HeadDatabaseAPI? = null

    fun getItemStack(name: String?, default: Material): ItemStack {
        if (name == null) return ItemStack(default)
        if (name.startsWith("hdb")) {
            val parts = name.split('-')
            if (parts.size < 2) {
                return ItemStack(default)
            }
            return ItemStack(default)
        }
        val material = Material.getMaterial(name) ?: return ItemStack(default)
        return ItemStack(material)
    }
}
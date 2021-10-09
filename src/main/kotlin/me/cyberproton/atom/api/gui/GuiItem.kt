package me.cyberproton.atom.api.gui

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Consumer

open class GuiItem(item: ItemStack, var clickHandler: Consumer<InventoryClickEvent>? = null) : Cloneable {
    val item: ItemStack = item.clone()

    constructor(material: Material): this(ItemStack(material))

    fun onClick(event: InventoryClickEvent): Boolean {
        if (clickHandler == null) {
            return false
        }
        clickHandler?.accept(event)
        return true
    }

    public override fun clone(): GuiItem {
        val r = GuiItem(item)
        r.clickHandler = clickHandler
        return r
    }
}

open class InputItem(item: ItemStack, val owner: UUID? = null, clickHandler: Consumer<InventoryClickEvent>? = null) : GuiItem(item, clickHandler) {
    constructor(material: Material, owner: UUID? = null, clickHandler: Consumer<InventoryClickEvent>? = null): this(ItemStack(material), owner, clickHandler)

    override fun clone(): InputItem {
        val r = InputItem(item, owner)
        r.clickHandler = clickHandler
        return r
    }
}
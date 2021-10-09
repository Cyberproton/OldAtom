package me.cyberproton.atom.api.gui

import me.cyberproton.atom.api.log.Log
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import java.util.*

open class Gui(val id: String, val rows: Int, val cols: Int, val title: String = "") : Layered {
    val size = rows * cols
    private val layers: MutableList<Layer> = arrayListOf()
    private var baseLayer: Layer = DefaultLayer("base", rows, cols, 0)
    private var inputOutputLayer: InputOutputLayer = InputOutputLayer("input", rows, cols, 1, hashSetOf(), MaskedLayer.Type.WHITE)
    private val views: MutableList<View> = arrayListOf()
    private val flags: MutableSet<GuiFlag> = hashSetOf()

    init {
        if (rows < 1 || cols < 1) {
            throw IllegalArgumentException("Rows and columns must larger than 0")
        }
        layers.add(inputOutputLayer)
        layers.add(baseLayer)
    }

    open fun onClick(event: InventoryClickEvent, view: View, position: Int) {
        val item = event.currentItem
        val cursor = event.cursor
        // Fast input
        if (event.clickedInventory != event.inventory) {
            Log.d(javaClass.simpleName, "Other Pass")
            if (event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                Log.d(javaClass.simpleName, "Move to other inventory pass")
                if (item == null || item.type.isAir) {
                    return
                }
                Log.d(javaClass.simpleName, "On input pass")
                for (layer in layers) {
                    if (layer !is InputOutputLayer) {
                        continue
                    }
                    val firstEmpty = layer.firstEmpty()
                    val result = layer.onClick(event, position)
                    if (result != Layer.Result.NONE) {
                        Log.d(javaClass.simpleName, "Layer ${layer.id} have consumed the click")
                        updateAllViewsAtPosition(firstEmpty)
                        break
                    }
                }
            }
        } else {
            Log.d(javaClass.simpleName, "Main Pass")
            for (layer in layers) {
                val result = layer.onClick(event, position)
                if (result != Layer.Result.NONE) {
                    Log.d(javaClass.simpleName, "Layer ${layer.id} have consumed the click")
                    if (layer is InputOutputLayer) {
                        updateAllViewsAtPosition(position)
                    }
                    break
                }
            }
        }

        Log.d(javaClass.simpleName, "Cursor: ${cursor?.type?.name}")
    }

    open fun onClose(event: InventoryCloseEvent, view: View) {
        Log.d(javaClass.simpleName, "Closing gui")
        Log.d(javaClass.simpleName, "Viewer size: ")
        if (view.inventory.viewers.size == 1) {
            Log.d(javaClass.simpleName, "Empty. View removed")
            views.remove(view)
        }
        if (!flags.contains(GuiFlag.DONT_RETURN_ITEMS_ON_CLOSED)) {
            returnInputs(event.player)
        }
    }

    open fun onDrag(event: InventoryDragEvent, view: View) {
        event.isCancelled = true
    }

    open fun onPluginDisable() {

    }

    fun updateAllViews() {
        views.forEach { it.update() }
    }

    fun updateAllViewsAtPosition(position: Int) {
        views.forEach { it.updateAtPosition(position) }
    }

    fun closeAllViews() {
        for (view in views) {
            view.close()
        }
        views.clear()
    }

    fun setItem(layerId: String, position: Int, item: GuiItem) {
        if (layerId == "base") {
            baseLayer.setItem(position, item)
        } else if (layerId == "input") {
            inputOutputLayer.setItem(position, item)
        } else {
            val layer = layers.firstOrNull { it.id == layerId } ?: return
            layer.setItem(position, item)
        }
        views.forEach { it.updateAtPosition(position) }
    }

    fun setBaseItem(position: Int, item: ItemStack): GuiItem {
        val guiItem = GuiItem(item)
        baseLayer.setItem(position, guiItem)
        views.forEach { it.updateAtPosition(position) }
        return guiItem
    }

    fun fillBaseItem(item: ItemStack, from: Int = 0, to: Int = size, isVertical: Boolean = false) {
        baseLayer.fill(GuiItem(item), from, to, isVertical)
        baseLayer.getAllItems().forEach { println("${it.key} ${it.value}") }
        if (isVertical) {
            views.forEach { it.updateAtPositions((from..to step baseLayer.columns).toList()) }
        } else {
            views.forEach { it.updateAtPositions((from..to).toList()) }
        }
    }

    fun getBaseItem(position: Int): GuiItem? = baseLayer.getItem(position)

    fun setInputItem(position: Int, item: ItemStack, owner: UUID? = null): InputItem {
        val inputItem = InputItem(item, owner)
        inputOutputLayer.setItem(position, inputItem)
        views.forEach { it.updateAtPosition(position) }
        return inputItem
    }

    fun getInputItem(position: Int): InputItem? = inputOutputLayer.getItem(position) as InputItem?

    fun setInputMask(mask: Set<Int>) {
        inputOutputLayer.setMask(mask)
    }

    fun getInputMask() = inputOutputLayer.getMask()

    fun getItem(slot: Int): GuiItem? {
        for (layer in layers) {
            val item = layer.getItem(slot)
            if (item != null) {
                Log.d(javaClass.simpleName, "Layer ${layer.id} have supplied the item ${item.item.type.name}")
                return item
            }
        }
        return null
    }

    private fun returnInputs() {
        for (layer in layers) {
            if (layer !is InputOutputLayer) {
                continue
            }
            for (item in layer.getAllItems().values) {
                val owner = (item as InputItem).owner ?: continue
                val p = Bukkit.getPlayer(owner) ?: continue
                p.inventory.addItem(item.item)
            }
        }
    }

    private fun returnInputs(player: HumanEntity) {
        for (layer in layers) {
            if (layer !is InputOutputLayer) {
                continue
            }
            for (item in layer.getAllItems().values) {
                if ((item as InputItem).owner != player.uniqueId) {
                    continue
                }
                player.inventory.addItem(item.item)
            }
        }
    }

    fun getBaseLayer(): Layer = baseLayer

    fun getInputLayer(): InputOutputLayer = inputOutputLayer

    override fun getLayer(layerId: String): Layer? = layers.find { it.id == layerId }

    override fun addLayer(layer: Layer) {
        if (layer.id == "base" || layer.id == "input") {
            return
        }
        layers.add(layer)
        layers.sortByDescending { it.priority }
        views.forEach { it.update() }
    }

    override fun removeLayer(layerId: String) {
        if (layerId == "base" || layerId == "input") {
            return
        }
        layers.removeIf { it.id == layerId }
        layers.sortByDescending { it.priority }
        views.forEach { it.update() }
    }

    override fun removeLayer(layer: Layer) {
        if (layer.id == "base" || layer.id == "input") {
            return
        }
        layers.remove(layer)
        layers.sortByDescending { it.priority }
        views.forEach { it.update() }
    }


    fun addFlag(flag: GuiFlag) {
        flags.add(flag)
    }

    fun removeFlag(flag: GuiFlag) {
        flags.remove(flag)
    }

    fun hasFlag(flag: GuiFlag) = flags.contains(flag)

    fun clearFlags(flag: GuiFlag) = flags.clear()

    fun createView(player: Player, rows: Int = minOf(this.rows, 6), cols: Int = minOf(this.cols, 9)): View {
        return View.createView(this, player, rows, cols)
    }

    fun addView(view: View) {
        if (view.gui !== this || views.contains(view)) {
            return
        }
        views.add(view)
    }
}
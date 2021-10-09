package me.cyberproton.atom.api.gui

import me.cyberproton.atom.api.extension.colored
import me.cyberproton.atom.api.log.Log
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

open class View private constructor(val gui: Gui, val rows: Int, horizontalOffset: Int = 0, verticalOffset: Int = 0) : InventoryHolder, Layered {
    private val inventory: Inventory = Bukkit.createInventory(this, rows * 9, gui.title.colored())
    private val layers: MutableList<Layer> = arrayListOf()
    var bounded: Boolean = true
    val size: Int = inventory.size
    private var offsetX = horizontalOffset
    private var offsetY = verticalOffset

    init {
        if (rows < 1 || rows > 6) {
            throw IllegalArgumentException("Rows must higher than 0 and lower than 6")
        }
        update()
    }

    override fun getInventory(): Inventory = this.inventory

    open fun onClick(event: InventoryClickEvent) {
        Log.d(javaClass.simpleName, "View click")
        var b = false
        for (layer in layers) {
            if (layer.onClick(event, event.slot) != Layer.Result.NONE) {
                b = true
                break
            }
        }
        Log.d(javaClass.simpleName, "Layer pass")
        if (!b) {
            val slot = mapGuiSlotToViewSlot(event.slot)
            gui.onClick(event, this, slot)
        }
    }

    open fun onClose(event: InventoryCloseEvent) {
        gui.onClose(event, this)
    }

    open fun onDrag(event: InventoryDragEvent) {
        gui.onDrag(event, this)
    }

    fun addViewer(player: Player) {
        player.openInventory(inventory)
    }

    fun removeViewer(player: Player) {
        for (viewer in inventory.viewers) {
            if (viewer.uniqueId == player.uniqueId) {
                viewer.closeInventory()
            }
        }
    }

    fun getViewerSize() = inventory.viewers.size

    fun update() {
        val offset = offsetY * gui.cols + offsetX
        for (i in 0 until inventory.size) {
            val item = gui.getItem(i + offset)
            inventory.setItem(i, item?.item)
        }
    }

    fun updateAtPositions(positions: Collection<Int>) {
        for (position in positions) {
            val slot = mapGuiSlotToViewSlot(position)
            if (!validateSlot(slot)) {
                return
            }
            val item = gui.getItem(position)
            inventory.setItem(slot, item?.item)
        }
    }

    fun updateAtPosition(position: Int) {
        val slot = mapGuiSlotToViewSlot(position)
        if (!validateSlot(slot)) {
            return
        }
        val item = gui.getItem(position)
        inventory.setItem(slot, item?.item)
    }

    fun move(x: Int, y: Int = 0) {
        offsetX += x
        offsetY += y
        update()
    }

    fun close() {
        for (viewer in inventory.viewers) {
            viewer.closeInventory()
        }
    }

    override fun getLayer(layerId: String): Layer? = layers.find { it.id == layerId }

    override fun addLayer(layer: Layer) {
        layers.add(layer)
        layers.sortByDescending { it.priority }
    }

    override fun removeLayer(layerId: String) {
        layers.removeIf { it.id == layerId }
        layers.sortByDescending { it.priority }
    }

    override fun removeLayer(layer: Layer) {
        layers.remove(layer)
        layers.sortByDescending { it.priority }
    }

    fun mapViewSlotToGuiSlot(slot: Int) = slot + offsetY * gui.cols + offsetX

    fun mapGuiSlotToViewSlot(slot: Int) = slot - offsetY * gui.cols - offsetX

    fun validateSlot(slot: Int): Boolean = slot > -1 && slot < size

    companion object {
        @JvmStatic
        fun createView(gui: Gui, player: Player, rows: Int = minOf(gui.rows, 6), cols: Int = minOf(gui.cols, 9)): View {
            val view = View(gui, rows)
            gui.addView(view)
            view.addViewer(player)
            return view
        }
    }
}
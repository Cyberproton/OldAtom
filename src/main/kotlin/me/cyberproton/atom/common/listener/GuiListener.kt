package me.cyberproton.atom.common.listener

import me.cyberproton.atom.api.gui.View
import me.cyberproton.atom.api.log.Log
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class GuiListener : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onGuiClick(event: InventoryClickEvent) {
        Log.d(javaClass.simpleName, "Gui Click")
        val view = event.inventory.holder
        if (view !is View) {
            return
        }
        Log.d(javaClass.simpleName, "View Pass")
        val item = event.currentItem
        val cursor = event.cursor
        if (event.clickedInventory != event.inventory) {
            Log.d(javaClass.simpleName, "Other Pass")
            if (event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                Log.d(javaClass.simpleName, "Move to other inventory pass")
                if (item == null || item.type.isAir) {
                    return
                }
                Log.d(javaClass.simpleName, "On input pass")
                view.onClick(event)
            }
        } else {
            Log.d(javaClass.simpleName, "Main Pass")
            if (cursor != null && !cursor.type.isAir) {
                Log.d(javaClass.simpleName, "On input pass")
                view.onClick(event)
            } else {
                Log.d(javaClass.simpleName, "On click pass")
                view.onClick(event)
            }
        }

        Log.d(javaClass.simpleName, "Is cancelled: ${event.isCancelled}")
        Log.d(javaClass.simpleName, "Cursor: ${event.whoClicked.itemOnCursor.type.name}")
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onGuiClose(event: InventoryCloseEvent) {
        Log.d(javaClass.simpleName, "Gui Close")
        val view = event.inventory.holder
        if (view !is View) {
            return
        }
        view.onClose(event)
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onGuiDrag(event: InventoryDragEvent) {
        Log.d(javaClass.simpleName, "Gui Drag")
        val holder = event.inventory.holder
        if (holder !is View) {
            return
        }
        if (event.rawSlots.any { it < event.inventory.size }) {
            holder.onDrag(event)
        }
    }
}
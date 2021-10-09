package me.cyberproton.atom.api.gui

import me.cyberproton.atom.api.log.Log
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent

abstract class Layer(val id: String, val rows: Int, val columns: Int, val priority: Int) {
    val size: Int = rows * columns

    protected val items: MutableMap<Int, GuiItem> = hashMapOf()

    abstract fun onClick(event: InventoryClickEvent, position: Int): Result

    open fun hasItem(position: Int): Boolean = items.containsKey(position)

    open fun getItem(position: Int): GuiItem? = items[position]

    open fun getAllItems(): Map<Int, GuiItem> = HashMap(items)

    abstract fun setItem(position: Int, item: GuiItem): Result

    abstract fun acceptItem(position: Int, item: GuiItem): Result

    open fun removeItem(position: Int): GuiItem? = items.remove(position)

    abstract fun fill(item: GuiItem, from: Int = 0, to: Int = size - 1, isVertical: Boolean = false)

    abstract fun fillIfEmpty(item: GuiItem, from: Int = 0, to: Int = size - 1, isVertical: Boolean = false)

    abstract fun firstEmpty(): Int

    fun validatePosition(position: Int): Boolean {
        if (position < 0 || position > size - 1) {
            return false
        }
        return true
    }

    enum class Result {
        NONE,
        ALLOW,
        DENY,
    }
}

class DefaultLayer(id: String, rows: Int, columns: Int, priority: Int) : Layer(id, rows, columns, priority) {
    // Pass to other layer or catch the item
    override fun onClick(event: InventoryClickEvent, position: Int): Result {
        event.isCancelled = true
        val item = items[position] ?: return Result.NONE
        item.onClick(event)
        return Result.ALLOW
    }

    override fun setItem(position: Int, item: GuiItem): Result {
        if (position < 0 || position > size - 1) {
            return Result.NONE
        }
        items[position] = item.clone()
        return Result.ALLOW
    }

    override fun acceptItem(position: Int, item: GuiItem): Result {
        if (position < 0 || position > size - 1) {
            return Result.NONE
        }
        if (items.containsKey(position)) {
            return Result.DENY
        }
        items[position] = item.clone()
        return Result.ALLOW
    }

    override fun fill(item: GuiItem, from: Int, to: Int, isVertical: Boolean) {
        if (isVertical) {
            for (i in from..to step columns) {
                if (!validatePosition(i)) {
                    continue
                }
                items[i] = item.clone()
            }
        } else {
            val f = minOf(maxOf(0, from), size - 1)
            val t = minOf(maxOf(0, to), size - 1)
            for (i in f..t) {
                items[i] = item.clone()
            }
        }
    }

    override fun fillIfEmpty(item: GuiItem, from: Int, to: Int, isVertical: Boolean) {
        if (isVertical) {
            for (i in from..to step columns) {
                if (!validatePosition(i) || items.containsKey(i)) {
                    continue
                }
                items[i] = item.clone()
            }
        } else {
            val f = minOf(maxOf(0, from), size - 1)
            val t = minOf(maxOf(0, to), size - 1)
            for (i in f..t) {
                if (items.containsKey(i)) {
                    continue
                }
                items[i] = item.clone()
            }
        }
    }

    override fun firstEmpty(): Int {
        for (i in 0 until size) {
            if (!items.containsKey(i)) {
                return i
            }
        }
        return -1
    }
}

open class MaskedLayer(id: String, rows: Int, columns: Int, priority: Int, private var mask: Set<Int>, private val type: Type) : Layer(id, rows, columns, priority) {
    var sortedMask = mask.sorted()

    // None: the positions are not in mask (white) or are in mask (black)
    // Allow: the positions are in mask and the position is empty
    // Deny: the positions are in mask and the position is not empty
    override fun onClick(event: InventoryClickEvent, position: Int): Result {
        event.isCancelled = true
        when (type) {
            Type.WHITE -> if (!mask.contains(position)) return Result.NONE
            Type.BLACK -> if (mask.contains(position)) return Result.NONE
        }
        val item = items[position] ?: return Result.DENY
        item.onClick(event)
        return Result.ALLOW
    }

    override fun acceptItem(position: Int, item: GuiItem): Result {
        when (type) {
            Type.BLACK -> if (mask.contains(position)) return Result.NONE
            Type.WHITE -> if (!mask.contains(position)) return Result.NONE
        }
        if (position < 0 || position > size - 1) {
            return Result.NONE
        }
        items[position] = item.clone()
        return Result.ALLOW
    }

    override fun setItem(position: Int, item: GuiItem): Result {
        if (position < 0 || position > size - 1) {
            return Result.NONE
        }
        when (type) {
            Type.BLACK -> if (mask.contains(position)) return Result.NONE
            Type.WHITE -> if (!mask.contains(position)) return Result.NONE
        }
        items[position] = item.clone()
        return Result.ALLOW
    }

    override fun fill(item: GuiItem, from: Int, to: Int, isVertical: Boolean) {
        if (isVertical) {
            for (i in from..to step columns) {
                if (!validatePosition(i) || !validateMask(i)) {
                    continue
                }
                items[i] = item.clone()
            }
        } else {
            val f = minOf(maxOf(0, from), size - 1)
            val t = minOf(maxOf(0, to), size - 1)
            for (i in f..t) {
                if (!validateMask(i)) {
                    continue
                }
                items[i] = item.clone()
            }
        }
    }

    override fun fillIfEmpty(item: GuiItem, from: Int, to: Int, isVertical: Boolean) {
        if (isVertical) {
            for (i in from..to step columns) {
                if (!validatePosition(i) || !validateMask(i) || items.containsKey(i)) {
                    continue
                }
                items[i] = item.clone()
            }
        } else {
            val f = minOf(maxOf(0, from), size - 1)
            val t = minOf(maxOf(0, to), size - 1)
            for (i in f..t) {
                if (!validateMask(i) || items.containsKey(i)) {
                    continue
                }
                items[i] = item.clone()
            }
        }
    }

    override fun firstEmpty(): Int {
        if (type == Type.WHITE) {
            for (i in sortedMask) {
                if (!items.containsKey(i)) {
                    return i
                }
            }
        } else {
            for (i in 0 until size) {
                if (mask.contains(i) || items.containsKey(i)) {
                    continue
                }
                return i
            }
        }
        return -1
    }

    fun getMask() = HashSet(mask)

    fun setMask(mask: Set<Int>): List<GuiItem> {
        // Black mask
        this.mask = mask
        val filtered = arrayListOf<GuiItem>()
        if (type == Type.BLACK) {
            for ((pos, item) in items) {
                if (!mask.contains(pos)) {
                    continue
                }
                filtered.add(item)
            }
        } else {
            for ((pos, item) in items) {
                if (mask.contains(pos)) {
                    continue
                }
                filtered.add(item)
            }
        }
        items.values.removeAll(filtered)
        sortedMask = mask.sorted()
        return filtered
    }

    fun validateMask(position: Int): Boolean {
        return when (type) {
            Type.WHITE -> mask.contains(position)
            Type.BLACK -> !mask.contains(position)
        }
    }

    enum class Type {
        BLACK,
        WHITE
    }
}

class InputOutputLayer(id: String, rows: Int, columns: Int, priority: Int, mask: Set<Int>, type: Type) : MaskedLayer(id, rows, columns, priority, mask, type) {
    override fun onClick(event: InventoryClickEvent, position: Int): Result {
        Log.d(javaClass.simpleName, "Click: $id")
        val player = event.whoClicked
        val item = event.currentItem
        val cursor = event.cursor
        if (event.clickedInventory !== event.inventory) {
            if (event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                // Fast input
                if (item == null || item.type.isAir) {
                    return Result.DENY
                }
                val first = firstEmpty()
                if (first < 0) {
                    return Result.NONE
                }
                val inputItem = InputItem(event.cursor!!, event.whoClicked.uniqueId)
                setItem(first, inputItem)
                event.currentItem = null
                event.isCancelled = true
                return Result.ALLOW
            }
            return Result.NONE
        } else {
            if (!validatePosition(position)) {
                return Result.NONE
            }
            if (!validateMask(position)) {
                return Result.NONE
            }
            Log.d(javaClass.simpleName, "Main Pass")
            event.isCancelled = true
            if (cursor == null || cursor.type.isAir) {
                Log.d(javaClass.simpleName, "Output")
                // Output item
                val output = removeItem(position) ?: return Result.NONE
                Log.d(javaClass.simpleName, "After removal")
                for ((slot, item) in items) {
                    Log.d(javaClass.simpleName, "$slot ${item.item.type.name}")
                }
                Log.d(javaClass.simpleName, "New Cursor: ${output.item.type.name}")
                player.setItemOnCursor(output.item)
                return Result.ALLOW
            } else {
                // Input-output item
                val inputItem = InputItem(event.cursor!!, event.whoClicked.uniqueId)
                val exist = getItem(position)
                // Input-output
                if (exist != null) {
                    Log.d(javaClass.simpleName, "Input-output: exist: ${exist.item.type.name}")
                    removeItem(position)
                    Log.d(javaClass.simpleName, "After removal")
                    for ((slot, item) in items) {
                        Log.d(javaClass.simpleName, "$slot ${item.item.type.name}")
                    }
                    Log.d(javaClass.simpleName, "New Cursor: ${exist.item.type.name}")
                    player.setItemOnCursor(exist.item)
                    return acceptItem(position, inputItem)
                }
                // Input
                Log.d(javaClass.simpleName, "New Cursor: ${exist?.item?.type?.name}")
                player.setItemOnCursor(null)
                return acceptItem(position, inputItem)
            }
        }
    }

    override fun setItem(position: Int, item: GuiItem): Result {
        Log.d(javaClass.simpleName, "Input layer set at $position")
        if (item !is InputItem) {
            throw IllegalArgumentException("Item must be InputItem")
        }
        return super.setItem(position, item)
    }

    override fun acceptItem(position: Int, item: GuiItem): Result {
        if (item !is InputItem) {
            throw IllegalArgumentException("Item must be InputItem")
        }
        return super.acceptItem(position, item)
    }
}
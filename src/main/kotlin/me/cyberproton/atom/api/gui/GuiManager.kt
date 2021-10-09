package me.cyberproton.atom.api.gui

import org.bukkit.entity.Player
import java.util.*

class GuiManager {
    private val guis: MutableMap<String, Class<Gui>> = hashMapOf()
    private val individuals: MutableMap<String, Gui> = hashMapOf()
    private val singletons: MutableMap<String, Gui> = hashMapOf()
    private val viewers: MutableMap<UUID, View> = hashMapOf()
}
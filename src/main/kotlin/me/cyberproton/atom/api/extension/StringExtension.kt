package me.cyberproton.atom.api.extension

import org.bukkit.ChatColor

fun String.colored(): String {
    if (!this.endsWith("&f")) {
        return ChatColor.translateAlternateColorCodes('&', "$this&f")
    }
    return ChatColor.translateAlternateColorCodes('&', this)
}
package me.cyberproton.atom.api.extension

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemFlag

fun ConfigurationSection.getStringListOrString(key: String): MutableList<String> {
    return if (isList(key)) {
        getStringList(key)
    } else {
        getString(key)?.let { arrayListOf(it) } ?: arrayListOf()
    }
}

fun ConfigurationSection.getItemFlags(key: String): MutableSet<ItemFlag> {
    val set = HashSet<ItemFlag>()
    for (s in getStringList(key)) {
        try {
            set.add(ItemFlag.valueOf(s.uppercase()))
        } catch (ex: Exception) { }
    }
    return set
}
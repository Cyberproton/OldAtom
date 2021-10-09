package me.cyberproton.atom.api.hook

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit

class VaultEconomyHook : Hook {
    val economy: Economy? = Bukkit.getServicesManager().getRegistration(Economy::class.java)?.provider

    override fun getName(): String = "Vault Economy"

    override fun isEnabled(): Boolean = economy != null

    override fun getHookOrNull(): Any? = economy
}
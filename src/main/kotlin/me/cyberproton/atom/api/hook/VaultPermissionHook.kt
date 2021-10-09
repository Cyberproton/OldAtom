package me.cyberproton.atom.api.hook

import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit

class VaultPermissionHook : Hook {
    val permission: Permission? = Bukkit.getServicesManager().getRegistration(Permission::class.java)?.provider

    override fun getName(): String = "Vault Permission"

    override fun isEnabled(): Boolean = permission != null

    override fun getHookOrNull(): Any? = permission
}
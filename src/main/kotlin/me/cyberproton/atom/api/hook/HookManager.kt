package me.cyberproton.atom.api.hook

class HookManager {
    val vaultEconomyHook: VaultEconomyHook = VaultEconomyHook()
    val vaultPermissionHook: VaultPermissionHook = VaultPermissionHook()
    val vaultChatHook: VaultChatHook = VaultChatHook()
}
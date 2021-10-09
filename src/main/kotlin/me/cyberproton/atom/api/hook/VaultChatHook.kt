package me.cyberproton.atom.api.hook

import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit

class VaultChatHook : Hook {
    val chat: Chat? = Bukkit.getServicesManager().getRegistration(Chat::class.java)?.provider

    override fun getName(): String = "Vault Chat"

    override fun isEnabled(): Boolean = chat != null

    override fun getHookOrNull(): Any? = chat
}
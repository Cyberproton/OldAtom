package me.cyberproton.atom.api.message

import me.cyberproton.atom.AtomPlugin
import me.cyberproton.atom.api.PluginComponent
import me.cyberproton.atom.api.config.YamlConfig
import java.util.concurrent.ConcurrentHashMap

open class MessageManager(override val atomPlugin: AtomPlugin, override val priority: Int) : PluginComponent {
    private lateinit var config: YamlConfig
    private val messages: MutableMap<Message, String?> = ConcurrentHashMap()

    override fun onEnable() {
        config = YamlConfig(atomPlugin, "/messages", "messages")
        onReload()
    }

    override fun onReload() {
        config.reload()
        val section = config.getConfig()
        for (message in messages.keys) {
            val s = section.getString(message.configPath)
            messages[message] = s
        }
    }

    override fun onDisable() {
        messages.clear()
    }

    fun registerMessage(message: Message) {
        if (messages.containsKey(message)) {
            return
        }
        val m = config.getConfig().getString(message.configPath) ?: message.defaultMessage
        messages[message] = m
        message.message = m
    }

    fun unregisterMessage(message: Message) {
        messages.remove(message)
    }
}
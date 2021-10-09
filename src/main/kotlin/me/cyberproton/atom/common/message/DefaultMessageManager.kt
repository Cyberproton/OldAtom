package me.cyberproton.atom.common.message

import me.cyberproton.atom.AtomPlugin
import me.cyberproton.atom.api.message.Message
import me.cyberproton.atom.api.message.MessageManager

class DefaultMessageManager(override val atomPlugin: AtomPlugin, override val priority: Int) : MessageManager(atomPlugin, priority) {
    override fun onEnable() {
        super.onEnable()
        for (field in Messages.javaClass.declaredFields) {
            field.isAccessible = true
            try {
                val s = field.get(null)
                if (s !is Message) {
                    continue
                }
                registerMessage(s)
            } catch (ex: IllegalAccessException) {
                ex.printStackTrace()
            }
        }
    }
}
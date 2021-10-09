package me.cyberproton.atom.api.log

import me.cyberproton.atom.api.Plugin

object Log {
    fun info(tag: String, message: String) {
        me.cyberproton.atom.api.Plugin.getPlugin().logger.info("[$tag] $message")
    }

    fun i(tag: String, message: String) {
        info(tag, message)
    }

    fun info(message: String) {
        me.cyberproton.atom.api.Plugin.getPlugin().logger.info(message)
    }

    fun i(message: String) {
        info(message)
    }

    fun debug(tag: String, message: String) {
        if (!me.cyberproton.atom.api.Plugin.getPlugin().config.getBoolean("debug")) {
            return
        }
        me.cyberproton.atom.api.Plugin.getPlugin().logger.info("[D][$tag] $message")
    }

    fun d(tag: String, message: String) {
        debug(tag, message)
    }

    fun debug(message: String) {
        if (!me.cyberproton.atom.api.Plugin.getPlugin().config.getBoolean("debug")) {
            return
        }
        me.cyberproton.atom.api.Plugin.getPlugin().logger.info("[D] $message")
    }

    fun warn(tag: String, message: String) {
        me.cyberproton.atom.api.Plugin.getPlugin().logger.warning("[$tag] $message")
    }

    fun w(tag: String, message: String) {
        warn(tag, message)
    }

    fun warn(message: String) {
        me.cyberproton.atom.api.Plugin.getPlugin().logger.warning(message)
    }

    fun w(message: String) {
        warn(message)
    }

    fun severe(tag: String, message: String) {
        me.cyberproton.atom.api.Plugin.getPlugin().logger.severe("[$tag] $message")
    }

    fun s(tag: String, message: String) {
        severe(tag, message)
    }

    fun severe(message: String) {
        me.cyberproton.atom.api.Plugin.getPlugin().logger.severe(message)
    }

    fun s(message: String) {
        severe(message)
    }
}
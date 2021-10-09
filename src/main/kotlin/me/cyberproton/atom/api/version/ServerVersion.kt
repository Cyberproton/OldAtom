package me.cyberproton.atom.api.version

import org.bukkit.Bukkit

object ServerVersion {
    val version: String = Bukkit.getMinecraftVersion()
    val majorVersion: Int = version.split('.')[0].toInt()
    val minorVersion: Int = version.split('.')[1].toInt()

    fun isHigherThan(major: Int, minor: Int): Boolean = majorVersion >= major && minorVersion > minor

    fun isHigherThanOrEqualsTo(major: Int, minor: Int): Boolean = majorVersion >= major && minorVersion >= minor

    fun isLowerThan(major: Int, minor: Int): Boolean = (majorVersion < major) || (majorVersion <= major && minorVersion < minor)

    fun isLowerThanOrEqualsTo(major: Int, minor: Int): Boolean = (majorVersion < major) || (majorVersion <= major && minorVersion <= minor)
}
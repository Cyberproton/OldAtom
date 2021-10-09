package me.cyberproton.atom.api.player

interface IPlayerPermissions {
    fun getPermissions(): Map<String, Boolean>

    fun getPermission(permission: String): Boolean?

    fun setPermission(permission: String, value: Boolean = true)

    fun removePermission(permission: String)

    fun clearPermissions()
}
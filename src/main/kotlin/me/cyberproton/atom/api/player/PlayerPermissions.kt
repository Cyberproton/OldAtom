package me.cyberproton.atom.api.player

import me.cyberproton.atom.Atom
import org.bukkit.permissions.PermissionAttachment
import java.util.*

class PlayerPermissions(override val id: UUID, override val player: IPlayer, override val priority: Int) : IPlayerPermissions, PlayerModule {
    private var permissions: PermissionAttachment = player.bukkitPlayer.addAttachment(Atom.instance)
    private var permissionAttached: Boolean = true

    override fun onUpdate() {
        clearPermissions()
    }

    override fun onUnload() {
        clearPermissions()
    }

    override fun getPermissions(): Map<String, Boolean> {
        setupPermissionAttachment()
        return permissions.permissions
    }

    override fun getPermission(permission: String): Boolean? {
        setupPermissionAttachment()
        return permissions.permissions[permission]
    }

    override fun setPermission(permission: String, value: Boolean) {
        setupPermissionAttachment()
        permissions.setPermission(permission, value)
    }

    override fun removePermission(permission: String) {
        setupPermissionAttachment()
        permissions.unsetPermission(permission)
    }

    override fun clearPermissions() {
        permissions.remove()
        permissionAttached = false
    }

    private fun setupPermissionAttachment() {
        if (permissionAttached) {
            return
        }
        permissions = player.bukkitPlayer.addAttachment(Atom.instance)
        permissionAttached = true
    }
}
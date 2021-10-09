package me.cyberproton.atom.common.listener

import me.cyberproton.atom.api.version.ServerVersion
import org.bukkit.Material
import org.bukkit.block.data.Waterlogged
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class FixListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onBlockPlaceOnWaterBlockFix(event: BlockPlaceEvent) {
        if (ServerVersion.isLowerThan(1, 13)) {
            return
        }
        if (event.blockAgainst.type != Material.LEGACY_STATIONARY_WATER) {
            return
        }
        val data = event.block.blockData as? Waterlogged ?: return
        data.isWaterlogged = false
    }
}
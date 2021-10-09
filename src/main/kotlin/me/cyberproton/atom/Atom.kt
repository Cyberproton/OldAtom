package me.cyberproton.atom

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import me.cyberproton.atom.api.PluginComponent
import me.cyberproton.atom.api.log.Log
import me.cyberproton.atom.api.player.IPlayerRegistry
import me.cyberproton.atom.api.player.PlayerRegistry
import me.cyberproton.atom.api.stat.IStatManager
import me.cyberproton.atom.api.stat.StatManager
import me.cyberproton.atom.api.version.ServerVersion
import me.cyberproton.atom.common.command.CommandHandler
import me.cyberproton.atom.common.listener.*
import org.bukkit.Bukkit

class Atom: AtomPlugin() {
    lateinit var statManager: IStatManager
        private set
    lateinit var playerRegistry: IPlayerRegistry
        private set
    lateinit var protocolManager: ProtocolManager
        private set
    lateinit var commandHandler: CommandHandler
        private set

    override fun onLoad() {
        if (ServerVersion.isLowerThan(1, 13)) {
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }
        instance = this
        saveDefaultConfig()
    }

    override fun onEnable() {
        statManager = StatManager()
        playerRegistry = PlayerRegistry(this, 1000)
        registerPluginComponent(playerRegistry as PluginComponent)
        protocolManager = ProtocolLibrary.getProtocolManager()
        Bukkit.getPluginManager().registerEvents(PlayerListener(playerRegistry), this)
        Bukkit.getPluginManager().registerEvents(MechanicListener(), this)
        Bukkit.getPluginManager().registerEvents(GuiListener(), this)
        Bukkit.getPluginManager().registerEvents(FixListener(), this)
        Bukkit.getPluginManager().registerEvents(PotionListener(), this)
        commandHandler = CommandHandler(this)
        enablePluginComponents()
    }

    override fun onReload() {
        Log.i("[Atom]", "Reloading Atom")
        reloadConfig()
        reloadPluginComponents()
    }

    override fun onDisable() {
        disablePluginComponents()
    }

    companion object Instance {
        @JvmStatic
        lateinit var instance: Atom
            private set
    }
}
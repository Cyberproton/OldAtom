package me.cyberproton.atom.common.command

import me.cyberproton.atom.api.command.CommandHandler
import me.cyberproton.atom.common.command.gui.GuiTestCommand
import me.cyberproton.atom.common.command.parser.PotionEffectTypeParser
import me.cyberproton.atom.common.command.permission.PermissionCheckAllCommand
import me.cyberproton.atom.common.command.permission.PermissionCheckCommand
import me.cyberproton.atom.common.command.permission.PermissionRemoveCommand
import me.cyberproton.atom.common.command.permission.PermissionSetCommand
import me.cyberproton.atom.common.command.potion.PotionAddCommand
import me.cyberproton.atom.common.command.reload.ReloadCommand
import org.bukkit.plugin.java.JavaPlugin

class CommandHandler(plugin: JavaPlugin) : CommandHandler(plugin, "atom") {
    init {
        registerCommand(ReloadCommand())
        registerCommand(PermissionSetCommand())
        registerCommand(PermissionRemoveCommand())
        registerCommand(PermissionCheckAllCommand())
        registerCommand(PermissionCheckCommand())
        registerCommand(GuiTestCommand())
        registerCommand(PotionAddCommand())
        argumentParserRegistry.addParser(PotionEffectTypeParser())
    }
}
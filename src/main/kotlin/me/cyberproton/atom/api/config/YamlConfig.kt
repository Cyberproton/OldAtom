package me.cyberproton.atom.api.config

import me.cyberproton.atom.api.log.Log
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class YamlConfig {
    val plugin: Plugin?
    val path: String
    val name: String
    private var config: FileConfiguration
    private val resolvedPath: String

    constructor(plugin: Plugin? = null, path: String = "", name: String) {
        this.plugin = plugin
        this.path = if (path.isNotEmpty() && path.first() == File.separatorChar) path.substring(1) else path
        this.name = name
        this.resolvedPath = if (plugin == null) path else plugin.dataFolder.toString() + File.separator + path
        setup()
        this.config = YamlConfiguration.loadConfiguration(
            File(
                resolvedPath,
                "$name.yml"
            )
        )
    }

    constructor(file: File) {
        this.plugin = null
        this.path = file.absolutePath
        this.name = file.nameWithoutExtension
        this.resolvedPath = file.absolutePath
        this.config = YamlConfiguration.loadConfiguration(file)
    }

    fun getConfig(): FileConfiguration {
        return config
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(File(resolvedPath, "$name.yml"))
    }

    fun save() {
        try {
            config.save(File(resolvedPath, "$name.yml"))
        } catch (iOException: IOException) {
            Log.w(javaClass.simpleName, "Could not save $name.yml")
        }
    }

    private fun setup() {
        try {
            if (!File(resolvedPath).exists()) File(resolvedPath).mkdirs()
            if (!File(resolvedPath, "$name.yml").exists())
                File(resolvedPath, "$name.yml").createNewFile()
        } catch (iOException: IOException) {
            Log.w(javaClass.simpleName, "Could not generate $name.yml")
        }
    }

    companion object {
        @JvmStatic
        fun getAllConfigsFromFolder(plugin: Plugin, path: String): List<YamlConfig> {
            var p = path.replace("/", File.separator).trim()
            if (p.isNotEmpty()) {
                if (p.firstOrNull() == File.separatorChar) {
                    p = p.substring(1)
                }
                if (p.lastOrNull() == File.separatorChar) {
                    p = p.substring(0, p.length - 1)
                }
            }
            val folder = File(plugin.dataFolder.absolutePath + File.separatorChar + p)
            if (!folder.exists() || !folder.isDirectory) {
                return arrayListOf()
            }
            return getAllConfigsFromFolder(folder)
        }

        @JvmStatic
        fun getAllConfigsFromFolder(absolutePath: String): List<YamlConfig> {
            val p = absolutePath.replace("/", File.separator)
            val folder = File(p)
            if (!folder.exists() || !folder.isDirectory) {
                return arrayListOf()
            }
            return getAllConfigsFromFolder(folder)
        }

        @JvmStatic
        fun getAllConfigsFromFolder(folder: File): List<YamlConfig> {
            return folder
                .listFiles()
                ?.filter { it.extension.equals("yml", ignoreCase = true) }
                ?.map { YamlConfig(it) }
                ?: arrayListOf()
        }
    }
}
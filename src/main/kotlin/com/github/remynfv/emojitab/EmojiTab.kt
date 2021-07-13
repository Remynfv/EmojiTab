package com.github.remynfv.emojitab

import com.comphenix.protocol.ProtocolManager
import com.github.remynfv.emojitab.commands.TestCommand
import com.github.remynfv.emojitab.utils.Messager
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException


class EmojiTab : JavaPlugin()
{
    private lateinit var protocolManager: ProtocolManager
    private lateinit var emojisConfig: FileConfiguration

    override fun onEnable()
    {
        // Plugin startup logic
        Messager.send("Loaded!")

        getCommand("test")!!.setExecutor(TestCommand(this))

        createEmojiListConfig()
    }

    override fun onDisable()
    {
        // Plugin shutdown logic
    }

    fun getEmojisConfig(): FileConfiguration
    {
        return this.emojisConfig
    }

    private fun createEmojiListConfig()
    {
        val emojisConfigFile = File(getDataFolder(), "emojis.yml")
        if (!emojisConfigFile.exists())
        {
            emojisConfigFile.getParentFile().mkdirs()
            saveResource("emojis.yml", false)
        }

        emojisConfig = YamlConfiguration()
        try
        {
            emojisConfig.load(emojisConfigFile)
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
        catch (e: InvalidConfigurationException)
        {
            e.printStackTrace()
        }

    }
}
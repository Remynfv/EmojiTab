package com.github.remynfv.emojitab

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
    private lateinit var emojisConfig: FileConfiguration

    lateinit var emojifier: Emojifier

    override fun onEnable()
    {
        // Plugin startup logic
        Messager.send("Loaded!")

        //Save Configs
        saveDefaultConfig()
        createEmojiListConfig()

        //Initialize emoji list
        emojifier = Emojifier(this)
        emojifier.loadEmojisFromConfig()

        //Register commands
        getCommand("test")!!.setExecutor(TestCommand(this))

        //Register events
        server.pluginManager.registerEvents(Events(this), this)
    }

    override fun onDisable()
    {
        // Plugin shutdown logic
    }

    //Get the FileConfiguration for the emoji list
    fun getEmojisConfig(): FileConfiguration
    {
        return this.emojisConfig
    }

    //Load emojis.yml into emojisConfig
    private fun createEmojiListConfig()
    {
        val emojisConfigFile = File(dataFolder, "emojis.yml")

        //Create emojis.yml if it doesn't exist yet.
        if (!emojisConfigFile.exists())
        {
            emojisConfigFile.parentFile.mkdirs()
            saveResource("emojis.yml", false)
        }

        //Load 'em in and hope it doesn't break
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
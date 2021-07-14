package com.github.remynfv.emojitab

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo
import com.comphenix.protocol.wrappers.*
import com.github.remynfv.emojitab.commands.TestCommand
import com.github.remynfv.emojitab.utils.Messager
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.*


class EmojiTab : JavaPlugin()
{
    //From config.yml
    var usePermissions: Boolean = true
    var verbose: Boolean = false
    var wrappingCharacter: String = ""
    var uuid: UUID = UUID.randomUUID()

    //This is emojis.yml
    private lateinit var emojisConfig: FileConfiguration

    //The Great Emojifier class, where most of the work gets done
    lateinit var emojifier: Emojifier

    //The packets that will be sent out to load the tab completion of emojis
    private lateinit var removeEmojisPacket: WrapperPlayServerPlayerInfo
    private lateinit var addEmojisPacket: WrapperPlayServerPlayerInfo


    /*
    Features list:
    TODO Individual permissions
    TODO Main config.yml
     */
    override fun onEnable()
    {
        // Plugin startup logic
        Messager.send("Loaded!")

        //Save Configs
        saveDefaultConfig()
        createEmojiListConfig()

        //Initialize emoji list
        emojifier = Emojifier(this)

        //(Re)load configs for the first time
        reloadConfigs()
        generateEmojiPackets()

        //Register commands
        getCommand("test")!!.setExecutor(TestCommand(this))
        //TODO /emoji
        //TODO /emoji reload
        //TODO /emoji toggle
        //TODO Custom skin uuid in config

        //Register events
        server.pluginManager.registerEvents(Events(this), this)

        //Load emojis for any players who are online already
        for (player in Bukkit.getOnlinePlayers())
            sendEmojiPackets(player)
    }

    private fun generateEmojiPackets()
    {


        //Create a list of players of size = emojiMap.keys.size
        addEmojisPacket = WrapperPlayServerPlayerInfo()
        addEmojisPacket.action = EnumWrappers.PlayerInfoAction.ADD_PLAYER

        val info = ArrayList<PlayerInfoData>()
        for (shortcode in emojifier.emojiMap.keys)
        {


            val shortcode2 = shortcode.take(16)
            val randomUUID = UUID.randomUUID()
            val gameProfile = WrappedGameProfile(randomUUID, shortcode2)
            val signature = WrappedSignedProperty("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyNjI4NTI5NjcyMywKICAicHJvZmlsZUlkIiA6ICJjZWM0M2ZiYTZkZTQ0NmQ0OTZkZjdjNmI4NGQyMGU1YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJMb2dib2ciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZmYzQ2MDQ5NDEzMDYzMzcyOWQ3NmU3ODkxYWUyODQwYjhhM2FmNGUxODMwZDVhYzYyMzc5NzQ1OGIxYTBkYyIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdkZmVhMTZkYzgzYzk3ZGYwMWExMmZhYmJkMTIxNjM1OWMwY2QwZWE0MmY5OTk5YjZlOTdjNTg0OTYzZTk4MCIKICAgIH0KICB9Cn0=", "tnigNGwKGoFXQdPyFjZaIebt65N0h32efVJCY/m1WFE4EmDYRB6pw3gyFB9sVUn5MlS9q4QrdEe7uNF9uSyvMtEfkAXl7lQENmGCgNv8A3igyOaMIhPyHQcJ67Tnw3UF5cm8xnJhJ14NSpNrdsL6aiKSIeuwtL59IiUQmUBYKKoNhW9Qpp6JWV9HBGEBdE/OuWqcMwliTY4L7L2WxaMoCHHfy6rApFvR0ePWXfF1Oi8ycqrXvb/5XzIYBLyO/vYhNuABDQCihCpjVgkZtPTmNB10QuG4bEqQ+Rd3C4PeGyuMeUcwTCD+pyEmh43ygkgzzHneltk8Q/oX92vBfc8z50+d2CMbxEmcidxgAN7zu2fWoRsj2zVLoECwRoEDJt50rEV3UG6hyVvw94Jy2d0fx45lhoRbpXuV6NAqvUL95KmzRBAKoC4JAYTuymFpOhoMKftlJTZm7HY32MKHk8UPDbfbSKhXPy5mKbfvVcCwDJjjS486D8MtvGUv3neFPd1s8VJHtbgvHynAoonojT5zp5pDfx/pYIEe+VP3KVSmeinMRf2Kuv1bXqFW392hc6fpHrGgz2d6ggcS8LrE4kInb0BmaCuzeZ2YDx9jXh9ACrM/J51JHvCDOHfxF8rGdN4+3I6sflRqJii/gjTaHl9F8MxEjeB0UzHo60O9DJA3kgI=")
            gameProfile.properties.put("textures", signature)
            info.add(PlayerInfoData(gameProfile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText("")))

        }
        addEmojisPacket.data = info

        //Configure an identical packet to do the reverse.
        removeEmojisPacket = WrapperPlayServerPlayerInfo()
        removeEmojisPacket.data = addEmojisPacket.data
        removeEmojisPacket.action = EnumWrappers.PlayerInfoAction.REMOVE_PLAYER     //Does this work?
    }

    fun sendEmojiPackets(player: Player)
    {
        addEmojisPacket.sendPacket(player)
    }

    fun sendRemoveEmojiPackets(player: Player)
    {
        removeEmojisPacket.sendPacket(player)
    }

    override fun onDisable()
    {
        // Plugin shutdown logic
        for (player in Bukkit.getOnlinePlayers())
            sendRemoveEmojiPackets(player)  //Remove autocorrect bois for all players, to avoid clogging up the tab menu if unwanted
    }

    fun reloadConfigs()
    {
        //Load config.yml settings in variables
        config.getBoolean(Configs.VERBOSE_BOOT).let { verbose = it }
        config.getBoolean(Configs.USE_PERMISSIONS).let { usePermissions = it }
        config.getString(Configs.WRAPPING_CHARACTER)?.let { wrappingCharacter = it }

        //Load emojis into hashmap
        emojifier.loadEmojisFromConfig()

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
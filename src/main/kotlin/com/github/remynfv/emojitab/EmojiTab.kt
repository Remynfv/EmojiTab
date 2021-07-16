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

private const val displayName: String = "" //Any character that is invisible

class EmojiTab : JavaPlugin()
{

    //From config.yml
    var usePermissions: Boolean = true
    var verbose: Boolean = false
    var wrappingCharacter: String = ""
    //Dim gray, courtesy of someone off mineskin.org (https://mineskin.org/14b3cfc390dc440282195d8a74b742f4)
    var texture: String = "ewogICJ0aW1lc3RhbXAiIDogMTYyMTQxMTE5MDkyMywKICAicHJvZmlsZUlkIiA6ICJmZDQ3Y2I4YjgzNjQ0YmY3YWIyYmUxODZkYjI1ZmMwZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDEyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ4NDYxNmVhNDI0OTk1NzI4OGE5Y2Y4ZTNhM2E0ZjVjZDU0NDYxNjk1ZTczMmM5ZWViOTA4NDBmZDRkYzg3YjQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
    var signature: String = "vAk/+xJkgEYANJq2FxjfX4xT5Lo+z1+YNnvWPUgLnpwgj3Vq1nqKZ24y0mHbsLROE3JCnOW1vJObFyNRBktInFXX5RhAv8yis/TSyFFhR3rjnC8ZEMSlM0gyy2K9nJxjY+jDSVBNBaBmWs1JbhPWl2zN/eaMEMivAwZmBLqhTLIV/o4IAUAIPDkxdEw5MGtp81wEot1YSMc1PkGYANx7VTGUy2eCe4AhjDgUrWLkGPkSWeCowU1xQzT5DeWw5V6sylRWXR7DTkzonteRA5jO4gXrXXt5CdytGbz8SOT9V2xnhUPbnRZOgeRKwwHphAJ4N+g2+C5BGxrfSlnmj8YZKAlM17YEK2ej1eClxmmxIW/2bjZnCJR0U7f750evnXb6ZcjIQ+P400RpSCUo79L9cbvz3rHU36IcHKl3GmGG9uyr15C6DVa5WGj5A19fmzIMyRG5e5GTH6NPVC+yK5R0M36in88iP1HQFY9CdPn9NixrdRcCcXPcOcKFsNXE6la+UMhSlsXX+FS5zGtMvTedn5fPglP0DWur9Iz4Z/Bk5ZoZ93NdpF/h63rLZG9xYBs+gf8UEESPRykZSB2wIRO4039s3TC4g8i/lUBn4Zt6IpUiXip9rK7ihKdy3bVX8YywxmCL9oqhfQK0jnFk1dPDBCs/QDMCYnP4fLkLEqPZRrI="

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
            val signature = WrappedSignedProperty("textures", texture,signature)
            gameProfile.properties.put("textures", signature)
            info.add(PlayerInfoData(gameProfile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(displayName)))

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

        //Load custom skins
        val texture = config.getString(Configs.TEXTURE)
        val signature = config.getString(Configs.SIGNATURE)
        if (!texture.isNullOrBlank() && !signature.isNullOrBlank())
        {
            this.texture = texture
            this.signature = signature
        }

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
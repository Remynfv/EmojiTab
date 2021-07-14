package com.github.remynfv.emojitab

import com.comphenix.packetwrapper.AbstractPacket
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn
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
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class EmojiTab : JavaPlugin()
{
    //From config.yml
    var usePermissions: Boolean = true
    var verbose: Boolean = false
    var wrappingCharacter: String = ""
    var uuid: UUID = UUID.randomUUID()

    //This is emojis.yml
    private lateinit var emojisConfig: FileConfiguration

    //The Great Emojifire class, where most of the work gets done
    lateinit var emojifier: Emojifier

    //The packets that will be sent out to load the tab completion of emojis
    lateinit var removeEmojisPacket: WrapperPlayServerPlayerInfo
    lateinit var addEmojisPacket: WrapperPlayServerPlayerInfo


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
            val signature = WrappedSignedProperty("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyNjI0MDc1NTQzNCwKICAicHJvZmlsZUlkIiA6ICJjZWM0M2ZiYTZkZTQ0NmQ0OTZkZjdjNmI4NGQyMGU1YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJMb2dib2ciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZmYzQ2MDQ5NDEzMDYzMzcyOWQ3NmU3ODkxYWUyODQwYjhhM2FmNGUxODMwZDVhYzYyMzc5NzQ1OGIxYTBkYyIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdkZmVhMTZkYzgzYzk3ZGYwMWExMmZhYmJkMTIxNjM1OWMwY2QwZWE0MmY5OTk5YjZlOTdjNTg0OTYzZTk4MCIKICAgIH0KICB9Cn0", "qtulmBYSr7MTdXqh8CSlaDPjePKCgh/es+ayO2u6HoBQ44DBGKVuywZhX1vqGFmRMKDTC+OhYMCOqN5c5aer6prgm2eVMIwn1ep0W1WOsUSV+a9kUlAd1txDQYR4xyKB3J6kTWi/pX2D/1LlldfYBnF2xWqzU+g4cDy4XCXRn+X20nHpTan4cnoBNVpwNYNutgBG9hisnXn1ZPGIV8fZgqDW5ob8JZnP115J4Y0qRj1Ihi3DBTV8V86VMLm9xdKzgYQ3rgp8GO+pXZy3J6smDjWamCi5lgsw2yGxwU40wtAKk+Z+eAy3zAds9LKTXJ4QSSbc4cBhh+WsAOl2bEEu3xDLlx4vuyghW9kyH8Ui1YH0jsdlyJ/Kar4mVITMWppg9wXMgP5lvgGQQ+VyTVO7Sa+UNF+EJmKomOVhTZRWk2wXRD6KEdhUEO6Gk7tJ9TT5jwvvGaarfqZErKGeHxMu8/kGJj0VcxislPN0wcl1ShS5yKOX55n2HA7Ntqst/PF6kmKBNjYKfwxT2fS5nW4XKOj2gNE9Mx32q//uwJ24NiPZcyYl2TmvVySxAf20hx3JrZs5cwDlZdRQk9DjZtQBar9on/Ekh6oeKErp+bB1Awoe2apoVz4nKv4NZoqRfd+HqjJPUrZAjO7kR6XsKazFrXvEtUmIKUCUFZQLVHm6t/E=")

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
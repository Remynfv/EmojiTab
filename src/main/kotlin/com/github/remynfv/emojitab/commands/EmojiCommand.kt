package com.github.remynfv.emojitab.commands

import com.github.remynfv.emojitab.EmojiTab
import com.github.remynfv.emojitab.utils.Messager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.metadata.MetadataValueAdapter

class EmojiCommand(val plugin: EmojiTab) : CommandExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        //Generic error
        fun showUsage() = Messager.warn("Invalid command. Usage: " + command.usage, sender)

        //emoji reload
        fun reload()
        {
            plugin.reloadConfigs()
            for (player in Bukkit.getOnlinePlayers())
            {
                plugin.sendRemoveEmojiPackets(player)
                plugin.sendEmojiPackets(player)
            }

            Messager.send("Config and emojis reloaded!", sender)
        }

        //emoji toggle <player>
        fun toggle()
        {
            val player: Player?
            player = if (args.size <= 1 && sender is Player)
                sender
            else
                Bukkit.getPlayer(args[1])

            if (player == null)
            {
                showUsage()
                return
            }

            //Get metadata to find out if emojis are already toggled
            var emojisOff: Boolean? = null
            for (meta in player.getMetadata("emoji_off"))
            {
                if (meta.asBoolean())
                {
                    emojisOff = meta.asBoolean()
                    break
                }
            }
            if (emojisOff == null)
                emojisOff = false

            emojisOff = !emojisOff //Toggle

            val value = FixedMetadataValue(plugin, emojisOff)
            player.setMetadata("emoji_off", value) //Set the metadata

            if (emojisOff)
            {
                Messager.send("Emojis turned OFF for ${player.name}", sender)
                plugin.sendRemoveEmojiPackets(player)
            }
            else
            {
                Messager.send("Emojis turned ON for ${player.name}", sender)
                plugin.sendEmojiPackets(player)

            }
        }

        if (args.size < 1)
            showUsage()
        else
        {
            //Map all them args
            when (args[0].lowercase())
            {
                "reload" -> reload()
                "toggle" -> toggle()
                else ->
                {
                    showUsage()
                }
            }
        }
        return true
    }

    private fun reload()
    {


    }


}
package com.github.remynfv.emojitab.commands

import com.github.remynfv.emojitab.EmojiTab
import com.github.remynfv.emojitab.utils.Messager
import com.github.remynfv.emojitab.utils.Settings
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

val subcommands = listOf("reload", "toggle").toMutableList()

class EmojiCommand(private val plugin: EmojiTab) : TabExecutor
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
                plugin.generateEmojiPackets()
                plugin.sendEmojiPackets(player)
            }

            Messager.send("Config and emojis reloaded!", sender)
        }

        //emoji toggle <player>
        fun toggle()
        {
            val player: Player? = if (args.size <= 1 && sender is Player)
                sender
            else
                Bukkit.getPlayer(args[1])

            if (player == null)
            {
                showUsage()
                return
            }




            val emojisOff = !Settings.getEmojiDisabled(player) //Toggle

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

        if (args.isEmpty())
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

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>?
    {
        when (args.size)
        {
            1 -> {
                return subcommands
            }
            2 -> {
                when (args[0].lowercase())
                {
                    "toggle" -> {
                        val players = mutableListOf<String>()
                        for (player in Bukkit.getOnlinePlayers())
                            if ((sender as Player).canSee(player))
                                players.add(player.name)
                        return players
                    }
                    //Room for more subcommands
                }
            }
        }
        return null
    }
}
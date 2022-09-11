package com.github.remynfv.emojitab.commands

import com.github.remynfv.emojitab.EmojiTab
import com.github.remynfv.emojitab.utils.Messager
import com.github.remynfv.emojitab.utils.Permissions
import com.github.remynfv.emojitab.utils.Settings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

enum class EmojiSubCommand(val permission: String)
{
    reload(Permissions.RELOAD),
    toggle(Permissions.TOGGLE_SELF),
    list(Permissions.LIST)
}

class EmojiCommand(private val plugin: EmojiTab) : TabExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        //Generic error
        fun showUsage() = Messager.warn("Invalid command. Usage: " + command.usage, sender)

        //emoji reload
        fun reload()
        {
            if (!sender.hasPermission(Permissions.RELOAD))
            {
                Messager.missingPermissions(Permissions.RELOAD, sender)
                return
            }
            plugin.reloadConfigs()
            for (player in Bukkit.getOnlinePlayers())
            {
                plugin.sendEmojiPackets(player)
            }

            Messager.send("Config and emojis reloaded!", sender)
        }

        //emoji toggle <player>
        fun toggle()
        {
            val player: Player?

            if (args.size <= 1 && sender is Player)
            {
                if (sender.hasPermission(Permissions.TOGGLE_SELF))
                    player = sender
                else
                {
                    Messager.missingPermissions(Permissions.TOGGLE_SELF, sender)
                    return
                }
            }
            else
            {
                if (sender.hasPermission(Permissions.TOGGLE_OTHERS))
                    player = Bukkit.getPlayer(args[1])
                else
                {
                    Messager.missingPermissions(Permissions.TOGGLE_OTHERS, sender)
                    return
                }
            }

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
            showList(sender)
        else
        {
            //Map all them args
            when (args[0].lowercase())
            {
                "reload" -> reload()
                "toggle" -> toggle()
                "list" -> showList(sender)
                else ->
                {
                    showUsage()
                }
            }
        }
        return true
    }

    private fun showList(sender: CommandSender)
    {
        //Check permissions
        if (!sender.hasPermission(Permissions.LIST) && plugin.usePermissions)
        {
            Messager.missingPermissions(Permissions.LIST, sender)
            return
        }

        //Create empty message
        var emojiList = Component.empty()

        //Iterate over list of emojis
        for (entry: MutableMap.MutableEntry<String, String> in plugin.emojifier.emojiMap)
        {
            //Create a slick hoverevent
            val hover = HoverEvent.showText(
                Component.text(entry.value).append(
                Component.text(" " + entry.key).color(NamedTextColor.AQUA)).append(
                Component.text(" /emoji").color(NamedTextColor.LIGHT_PURPLE)))

            //Create the clickevent
            val clickEvent = ClickEvent.suggestCommand(entry.key)

            //Create the whole component
            val currentEmoji = Component.text(entry.value).hoverEvent(hover).clickEvent(clickEvent)

            //Add the current emoji component to the list
            emojiList = emojiList.append(Component.text(" ")).append(currentEmoji)
        }

        //Send the list
        sender.sendMessage(emojiList)
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>?
    {
        when (args.size)
        {
            1 -> {
                val output = mutableListOf<String>()
                for (cmd in EmojiSubCommand.values())
                    if (sender.hasPermission(cmd.permission)) output.add(cmd.name)
                return output
            }
            2 -> {
                if (!sender.hasPermission(Permissions.TOGGLE_OTHERS))
                    return null

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
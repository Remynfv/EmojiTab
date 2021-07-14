package com.github.remynfv.emojitab.commands

import com.github.remynfv.emojitab.EmojiTab
import com.github.remynfv.emojitab.utils.Messager
import com.mojang.authlib.properties.Property
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player


class TestCommand(val plugin: EmojiTab) : CommandExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        if (sender !is Player)
            return true


        val player: Player = sender


        val playerNMS = (player as CraftPlayer).handle
        val profile = playerNMS.profile

        val property: Property = profile.properties["textures"].iterator().next()

        val texture = property.value
        val signature = property.signature

        Messager.send(property.toString())
        Messager.send(texture)
        Messager.send(signature)

        return true
    }
}
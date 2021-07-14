package com.github.remynfv.emojitab.commands

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.github.remynfv.emojitab.EmojiTab
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*


class TestCommand(val plugin: EmojiTab) : CommandExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        if (sender !is Player)
            return true


        val player: Player = sender

        return true
    }
}
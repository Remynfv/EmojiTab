package com.github.remynfv.emojitab.commands

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.github.remynfv.emojitab.EmojiTab
import com.github.remynfv.emojitab.utils.Messager
import org.bukkit.Bukkit
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

        val addPlayerPacket = WrapperPlayServerPlayerInfo()
        addPlayerPacket.action = EnumWrappers.PlayerInfoAction.ADD_PLAYER
        val info = PlayerInfoData(WrappedGameProfile(UUID.randomUUID(), "NPC"), 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(""))
        addPlayerPacket.data = List(1) { info }
        addPlayerPacket.sendPacket(player)

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
            {
                Messager.broadcast("This message is shown after 1 seconds")
                val hiderPacket = WrapperPlayServerPlayerInfo()
                hiderPacket.action = EnumWrappers.PlayerInfoAction.REMOVE_PLAYER
                hiderPacket.data = List<PlayerInfoData>(1) { info }
                hiderPacket.sendPacket(player)
            },
            20L) //20 Tick (1 Second) delay before run() is called



        return true
    }
}
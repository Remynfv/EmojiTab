package com.github.remynfv.emojitab

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent


class Events(private val plugin: EmojiTab) : Listener
{
    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent)
    {
        //Add emojis to any player chat
        val newMessage = plugin.emojifier.emojifyMessage(event.message())

        //Replace the event.message with the emojified version
        event.message(newMessage)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent)
    {
        val player = event.player

        plugin.sendEmojiPackets(player)
    }
}
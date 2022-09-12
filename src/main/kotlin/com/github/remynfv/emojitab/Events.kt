package com.github.remynfv.emojitab

import com.github.remynfv.emojitab.utils.Permissions
import io.papermc.paper.event.player.AsyncChatDecorateEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener


class Events(private val plugin: EmojiTab) : Listener
{
    @Suppress("UnstableApiUsage")
    @EventHandler(priority = EventPriority.LOW)
    fun onChatDecorate(event: AsyncChatDecorateEvent)
    {
        val player = event.player()

        //If player lacks permission to use emojis altogether, return
        if (player != null && !player.hasPermission(Permissions.USE) && plugin.usePermissions)
            return

        //Add emojis to any player chat
        val newMessage = plugin.emojifier.emojifyMessage(event.result(), player)

        //Replace the event.message with the emojified version
        event.result(newMessage)
    }
}
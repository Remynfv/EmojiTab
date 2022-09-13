package com.github.remynfv.emojitab

import com.github.remynfv.emojitab.utils.Permissions
import io.papermc.paper.event.player.AsyncChatDecorateEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent
import org.bukkit.event.player.PlayerJoinEvent


class Events(private val plugin: EmojiTab) : Listener
{
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent)
    {
        plugin.trySendEmojiPacket(e.player)
    }
}

class PaperEvents(private val plugin: EmojiTab) : Listener
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

/**
 * Chat modifying events using the Bukkit API instead of the Paper API.
 */
class BukkitEvents(private val plugin: EmojiTab) : Listener
{
    @Suppress("DEPRECATION")
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerChat(event: AsyncPlayerChatEvent)
    {
        val player = event.player

        //If player lacks permission to use emojis altogether, return
        if (!player.hasPermission(Permissions.USE) && plugin.usePermissions)
            return

        // Basic replacement of a string.
        event.message = plugin.emojifier.emojifyString(event.message)
    }

    @Suppress("DEPRECATION")
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerPreviewChat(event: AsyncPlayerChatPreviewEvent)
    {
        val player = event.player

        //If player lacks permission to use emojis altogether, return
        if (!player.hasPermission(Permissions.USE) && plugin.usePermissions)
            return

        // Basic replacement of a string.
        event.message = plugin.emojifier.emojifyString(event.message)
    }
}
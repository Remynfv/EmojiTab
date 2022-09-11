package com.github.remynfv.emojitab

import com.github.remynfv.emojitab.utils.Permissions
import io.papermc.paper.event.player.AsyncChatDecorateEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable


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

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent)
    {
        val player = event.player

        //Delay things by 1 tick so they have time to properly load in
        object : BukkitRunnable()
        {
            override fun run()
            {
                //The emojis don't require a delay, but the player list does if you want it to include yourself
                plugin.sendEmojiPackets(player)

                //Add this player for everyone else
                for (p in Bukkit.getOnlinePlayers())
                {
                    if (p == player) //No need to update yourself, that's already done
                        continue

                    plugin.updatePlayerForPlayer(p, player)
                }

            }
        }.runTaskLater(plugin, 1)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent)
    {
        plugin.removeFlippedUUIDFromTab(event.player).broadcastPacket()
    }
}
package com.github.remynfv.emojitab.utils

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

object Messager
{
    private const val PREFIX: String = "§a[EmojiTab] §8> §r"
    private const val WARNING_PREFIX: String = "§4WARN§8: §c"

    //Send a message to the console
    fun send(message: String)
    {
        Bukkit.getConsoleSender().sendMessage(PREFIX + message)
    }

    fun warn(warning: String)
    {
        Bukkit.getConsoleSender().sendMessage(PREFIX + WARNING_PREFIX + warning)
    }

    //Broadcast a message publicly in server chat
    fun broadcast(message: String)
    {
        Bukkit.getServer().sendMessage(Component.text(PREFIX + message))
    }
}
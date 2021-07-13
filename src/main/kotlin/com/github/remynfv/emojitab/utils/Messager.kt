package com.github.remynfv.emojitab.utils

import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import java.awt.TextComponent

object Messager
{
    private val PREFIX: String = "§a[EmojiTab] §8> §r"

    //Send a message to the console
    fun send(message: String)
    {
        Bukkit.getConsoleSender().sendMessage(PREFIX + message)
    }

    //Broadcast a message publicly in server chat
    fun broadcast(message: String)
    {
        Bukkit.getServer().sendMessage(Component.text(PREFIX + message))
    }
}
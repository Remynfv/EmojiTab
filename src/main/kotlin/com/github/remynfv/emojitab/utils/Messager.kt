package com.github.remynfv.emojitab.utils

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object Messager
{
    private const val PREFIX: String = "§a[EmojiTab] §8> §r"
    private const val WARNING_PREFIX: String = "§4WARN§8: §c"

    //Send a message to the console
    fun send(message: String)
    {
        Bukkit.getConsoleSender().sendMessage(PREFIX + message)
    }

    fun send(message: String, sender: CommandSender)
    {
        sender.sendMessage(PREFIX + message)
    }

    fun warn(warning: String)
    {
        Bukkit.getConsoleSender().sendMessage(PREFIX + WARNING_PREFIX + warning)
    }

    fun warn(warning: String, sender: CommandSender)
    {
        send("§c" + warning, sender)
    }

    //Broadcast a message publicly in server chat
    fun broadcast(message: String)
    {
        Bukkit.getServer().sendMessage(Component.text(PREFIX + message))
    }
}